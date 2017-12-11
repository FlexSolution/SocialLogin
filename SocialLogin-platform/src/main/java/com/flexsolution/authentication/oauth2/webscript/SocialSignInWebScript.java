/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flexsolution.authentication.oauth2.webscript;

import com.flexsolution.authentication.oauth2.configs.Oauth2APIFactory;
import com.flexsolution.authentication.oauth2.configs.Oauth2Configs;
import com.flexsolution.authentication.oauth2.constant.Oauth2Parameters;
import com.flexsolution.authentication.oauth2.constant.Oauth2Session;
import com.flexsolution.authentication.oauth2.constant.Oauth2Transaction;
import com.flexsolution.authentication.oauth2.dto.AccessToken;
import com.flexsolution.authentication.oauth2.dto.UserMetadata;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A demonstration Java controller for the Hello World Web Script.
 *
 * @author martin.bergljung@alfresco.com
 * @since 2.1.0
 */
public class SocialSignInWebScript extends DeclarativeWebScript {

    private static final Log logger = LogFactory.getLog(SocialSignInWebScript.class);

    private static final String TICKET = "ticket";
    private static final String USER = "user";

    private AuthenticationService authenticationService;
    private PersonService personService;
    private NodeService nodeService;
    private Oauth2APIFactory oauth2APIFactory;
    private ContentService contentService;

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        String state = req.getParameter(Oauth2Parameters.STATE);

        String sessionState = (String) WebScriptUtils.getSessionAttribute(req, Oauth2Session.OAUTH_2_STATE);
        if (StringUtils.isBlank(state) || StringUtils.isBlank(sessionState) || !state.equals(sessionState)) {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, "CSRF attack was detected");
        }

        String error = req.getParameter(Oauth2Parameters.ERROR);
        if (StringUtils.isNotBlank(error)) {
            /*
            user_cancelled_login - The user refused to login into LinkedIn account.
            user_cancelled_authorize - The user refused to authorize permissions request from your application.
             */
            String errorDescription = req.getParameter("error_description");
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, error + ": " + errorDescription);
        }

        String code = req.getParameter(Oauth2Parameters.CODE);
        if (StringUtils.isBlank(code)) {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, "'code' is mandatory parameter");
        }

        Oauth2Configs apiConfig = oauth2APIFactory.getAPIFromUserSession(req);

        AccessToken accessToken = apiConfig.getAccessToken(code);

        UserMetadata userMetadata = apiConfig.getUserMetadata(accessToken);

        String userName = apiConfig.getUserNamePrefix() + userMetadata.getId();

        AlfrescoTransactionSupport.bindResource(Oauth2Transaction.AUTHENTICATION_USER_NAME, userName);

        authenticationService.authenticate(userName, "1".toCharArray());//any password

        NodeRef personOrNull = personService.getPersonOrNull(userName);

        if (personOrNull != null) {
            // ensure cm:person has 'cm:preferences' aspect applied - as we want to add the avatar as
            // the child node of the 'cm:preferenceImage' association
            if (!nodeService.hasAspect(personOrNull, ContentModel.ASPECT_PREFERENCES)) {
                nodeService.addAspect(personOrNull, ContentModel.ASPECT_PREFERENCES, null);
            }

            nodeService.setProperty(personOrNull, ContentModel.PROP_FIRSTNAME, userMetadata.getFirstName());
            nodeService.setProperty(personOrNull, ContentModel.PROP_LASTNAME, userMetadata.getLastName());
            nodeService.setProperty(personOrNull, ContentModel.PROP_EMAIL, userMetadata.getEmailAddress());
            nodeService.setProperty(personOrNull, ContentModel.PROP_LOCATION, userMetadata.getLocation().getName());
            Optional<String> industry = Optional.ofNullable(userMetadata.getIndustry());
            Optional<String> headline = Optional.ofNullable(userMetadata.getHeadline());
            StringBuilder jobTitle = new StringBuilder();
            industry.ifPresent(jobTitle::append);
            if (industry.isPresent() && headline.isPresent()) {
                jobTitle.append(", ");
            }
            headline.ifPresent(jobTitle::append);
            nodeService.setProperty(personOrNull, ContentModel.PROP_JOBTITLE, jobTitle.toString());
            Optional.ofNullable(userMetadata.getSummary()).ifPresent(s ->
                    Optional.ofNullable(contentService.getWriter(personOrNull, ContentModel.PROP_PERSONDESC, true))
                            .ifPresent(w -> w.putContent(s)));

            updateUserAvatar(personOrNull, userMetadata, apiConfig.getAvatarName());
        } else {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "User " +
                    userMetadata + " was not created automatically by authentication chain");
        }

        String ticket = authenticationService.getCurrentTicket();

        Map<String, Object> model = new HashMap<>();
        model.put(TICKET, ticket);
        model.put(USER, userName);

        return model;
    }


    //todo do not overwrite if not changed!
    private void updateUserAvatar(NodeRef personOrNull, UserMetadata userMetadata, String avatarName) {

        // remove old image child node if we already have one
        List<ChildAssociationRef> childAssoc = nodeService.getChildAssocs(personOrNull,
                ContentModel.ASSOC_PREFERENCE_IMAGE, null, 1, false);
        if (!childAssoc.isEmpty()) {
            nodeService.deleteNode(childAssoc.get(0).getChildRef());
        }

        Map<QName, Serializable> map = new HashMap<>();
        map.put(ContentModel.PROP_NAME, avatarName);
        ChildAssociationRef associationRef = nodeService.createNode(personOrNull, ContentModel.ASSOC_PREFERENCE_IMAGE,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(avatarName)),
                ContentModel.TYPE_CONTENT, map);
        NodeRef newImageNodeRef = associationRef.getChildRef();

        try (InputStream inputStream = new URL(userMetadata.getPictureUrl()).openStream()) {
            ContentWriter writer = contentService.getWriter(newImageNodeRef, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_IMAGE_JPEG);//for LinkedIn avatar
            writer.putContent(inputStream);
        } catch (IOException e) {
            logger.error("Can not load user logo, ", e);
            e.printStackTrace();// continue here without logo
        }
//         wire up 'cm:avatar' target association - backward compatible with JSF web-client avatar
        List<ChildAssociationRef> childAssocOldAvatar = nodeService.getChildAssocs(personOrNull,
                ContentModel.ASSOC_AVATAR, null, 1, false);
        if (!childAssocOldAvatar.isEmpty()) {
            nodeService.removeAssociation(personOrNull, childAssocOldAvatar.get(0).getChildRef(), ContentModel.ASSOC_AVATAR);
        }

        nodeService.createAssociation(personOrNull, newImageNodeRef, ContentModel.ASSOC_AVATAR);
    }


    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setOauth2APIFactory(Oauth2APIFactory oauth2APIFactory) {
        this.oauth2APIFactory = oauth2APIFactory;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }
}