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

import com.google.gson.Gson;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.UrlUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;
import org.springframework.http.MediaType;
import org.springframework.social.oauth2.OAuth2Version;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A demonstration Java controller for the Hello World Web Script.
 *
 * @author martin.bergljung@alfresco.com
 * @since 2.1.0
 */
public class LinkedInSignInWebScript extends DeclarativeWebScript {
    private static Log logger = LogFactory.getLog(LinkedInSignInWebScript.class);

    private AuthenticationService authenticationService;
    private PersonService personService;
    private NodeService nodeService;
    @Deprecated
    private Boolean testing;

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        String error = req.getParameter("error");
        if (StringUtils.isNotBlank(error)) {
            System.out.println(error);
            /*
            user_cancelled_login - The user refused to login into LinkedIn account.
            user_cancelled_authorize - The user refused to authorize permissions request from your application.
             */
            System.out.println(StringUtils.isNotBlank(req.getParameter("error_description")));
        }

        String code = req.getParameter("code");
        String state = req.getParameter("state");

        final HttpServletRequest httpServletRequest = WebScriptServletRuntime.getHttpServletRequest(req);
        String sessionState = (String) httpServletRequest.getSession().getAttribute(Oauth2Session.OAUTH_2_STATE);
        if (!state.equals(sessionState)) {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, "CSRF attack was detected");
        }

        String userName = null;
        if (!testing) {
            try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
                HttpPost post = new HttpPost("https://www.linkedin.com/oauth/v2/accessToken");//todo config file
                post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("grant_type", "authorization_code"));
                params.add(new BasicNameValuePair("code", code));
//                params.add(new BasicNameValuePair("redirect_uri", UrlUtil.getShareUrl(sysAdminParams) + "/service/api/social-login"));//todo config file
                params.add(new BasicNameValuePair("client_id", "78njxd1uv7zrvq"));//todo config file
                params.add(new BasicNameValuePair("client_secret", "HIGUbb2OmjFIxkEG"));//todo config file

                post.setEntity(new UrlEncodedFormEntity(params));

                try (CloseableHttpResponse response = httpclient.execute(post)) {

                    int statusCode = response.getStatusLine().getStatusCode();

                    HttpEntity entity = response.getEntity();

                    if (statusCode == Status.STATUS_OK) {

                        Gson gson = new Gson();

                        String responseString = EntityUtils.toString(entity, "UTF-8");

                        AccessTokenResponse accessTokenResponse = gson.fromJson(responseString, AccessTokenResponse.class);

                        System.out.println(accessTokenResponse);//todo remove

                        userName = loadUserData(accessTokenResponse);

                    } else {
                        throw new WebScriptException(Status.STATUS_UNAUTHORIZED, entity.toString());//todo test
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
            accessTokenResponse.setAccess_token("AQVGeu0pcvo9lKAR2oJSJ1yq3xIz6OZpc3jFN52rstl6hGNMQDTQ86cWGK_C-YaAA5XfHwBXcbNMckyrtnAGSFQd1QQMw8SlE5wkCfgbTsx4O7_8DSQVXJ3q3tLny_MyAUqEuBYxgiL-N8ty2aREx257Teyeeq_lVIbvyXn_cNZ9jT2RQCb8iTBAqyk7HhkSLApJk-3_hcfJVVl-tzCHxBv9kNEZfR9AZkiQ5NpBsURi47Xbfu8EDxKkpgXZ0-MZ4kDRtsmJWNmQ-dGocqW-waYo1Wa8DR5I31vtSAmGOAzkiJXpgdKZnVsektrKyiBm0jLL_0eHUhtB59hf8ADhrkC_5v2O_Q");
            accessTokenResponse.setExpires_in(5183999);

            userName = loadUserData(accessTokenResponse);
        }

        String ticket = authenticationService.getCurrentTicket();

        Map<String, Object> model = new HashMap<>();
        model.put("ticket", ticket);
        model.put("user", userName);

        return model;
    }

    private String loadUserData(AccessTokenResponse accessTokenResponse) {

        //todo load user data
        String userDataUrl = "https://api.linkedin.com/v1/people/~:(first-name,last-name,id,picture-url,email-address,location)?format=json";

        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            HttpGet get = new HttpGet(userDataUrl);//todo config file
            get.setHeader("x-li-format", WebScriptResponse.JSON_FORMAT);
            get.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            get.setHeader(HttpHeaders.AUTHORIZATION, OAuth2Version.BEARER.getAuthorizationHeaderValue(accessTokenResponse.getAccess_token()));

            try (CloseableHttpResponse response = httpclient.execute(get)) {

                int statusCode = response.getStatusLine().getStatusCode();

                HttpEntity entity = response.getEntity();

                if (statusCode == Status.STATUS_OK) {

                    Gson gson = new Gson();

                    String responseString = EntityUtils.toString(entity, "UTF-8");

                    System.out.println(responseString);

                    Map<String, Object> map = new HashMap<>();
                    map = (Map<String, Object>) gson.fromJson(responseString, map.getClass()); // todo convert

                    System.out.println(map);

                    String username = "social_" + map.get("id");

                    AlfrescoTransactionSupport.bindResource("authenticationUserName", username);

                    authenticationService.authenticate(username, "1".toCharArray());

                    NodeRef personOrNull = personService.getPersonOrNull(username);
                    if (personOrNull != null) {
                        //todo move in the oauth subsystem
                        nodeService.setProperty(personOrNull, ContentModel.PROP_FIRSTNAME, (Serializable) map.get("firstName"));
                        nodeService.setProperty(personOrNull, ContentModel.PROP_LASTNAME, (Serializable) map.get("lastName"));
                        nodeService.setProperty(personOrNull, ContentModel.PROP_EMAIL, (Serializable) map.get("emailAddress"));
                    }

                    return username;

                    /*
                    {
  "emailAddress": "test@flex-solution.com",
  "firstName": "test first name",
  "id": "MUX4KjhJev",
  "lastName": "test last name",
  "location": {
    "country": {"code": "ua"},
    "name": "Ukraine"
  },
  "pictureUrl": "https://media.licdn.com/mpr/mprx/0_PKKbUcCRdDsMtoqF_Tu3RaYYdDVMtERb5Tu8adijJCcytQBb5MSLf9_ZeCRKnkNC6nS3R9GZR4UJ3UbmrR3Hu2CRu4UM3UQk5R3H0DiRu4BJ3UM7ka2iSJ7AsibvKDcfrhjeJ91tu7oKC0nH97bGsI"
}
                     */

                } else {
                    throw new WebScriptException(Status.STATUS_UNAUTHORIZED, entity.toString());//todo test
                }
            }
        } catch (IOException e) {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, e.toString());
        }
    }

//    public void setSysAdminParams(SysAdminParams sysAdminParams) {
//        this.sysAdminParams = sysAdminParams;
//    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setTesting(Boolean testing) {
        this.testing = testing;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}