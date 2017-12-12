package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.model.Oauth2ConfigModel;
import org.alfresco.service.namespace.QName;

/**
 * Created by max on 12/6/17 .
 */
public class LinkedInOauth2Configs extends AbstractOauth2Configs {

    private static final String LINKED_IN = "linkedIn";
    private static final String LINKED_IN_AVATAR_JPEG = "LinkedIn_avatar.jpeg";
    private static final String ACCESS_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String USER_DATA_URL = "https://api.linkedin.com/v1/people/~:(first-name,last-name,id,picture-url,email-address,location,headline,industry,current-share,summary,specialties,positions,public-profile-url)?format=json";

    @Override
    String getAccessTokenURL() {
        return ACCESS_URL;
    }

    @Override
    String getAuthorizationURL() {
        return AUTHORIZATION_URL;
    }

    @Override
    String getUserDataUrl() {
        return USER_DATA_URL;
    }

    @Override
    QName getClientIdQName() {
        return Oauth2ConfigModel.PROP_LINKED_IN_CLIENT_ID;
    }

    @Override
    QName getSecretKeyQName() {
        return Oauth2ConfigModel.PROP_LINKED_IN_SECRET_KEY;
    }

    @Override
    QName getQNameForEnableField() {
        return Oauth2ConfigModel.PROP_LINKED_IN_OAUTH2_SIGN_IN_ENABLED;
    }

    @Override
    public String getUserNamePrefix() {
        return LINKED_IN;
    }

    @Override
    public String getAvatarName() {
        return LINKED_IN_AVATAR_JPEG;
    }

    @Override
    public String getApiName() {
        return LINKED_IN;
    }
}
