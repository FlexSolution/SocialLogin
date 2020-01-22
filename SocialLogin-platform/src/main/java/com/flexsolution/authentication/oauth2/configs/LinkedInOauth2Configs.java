package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.model.Oauth2ConfigModel;
import org.alfresco.service.namespace.QName;

public class LinkedInOauth2Configs extends AbstractOauth2Configs {

    private static final String LINKED_IN = "linkedIn";
    private static final String LINKED_IN_AVATAR_JPEG = "LinkedIn_avatar.jpeg";
    private static final String ACCESS_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private static final String USER_DATA_URL = "https://api.linkedin.com/v2/me";
    private static final String USER_PHOTO_URL = "https://api.linkedin.com/v2/me?projection=(id,profilePicture(displayImage~:playableStreams))";
    private static final String USER_EMAIL_URL = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";


    @Override
    String getUserPhotoUrl() {
        return USER_PHOTO_URL;
    }

    @Override
    String getUserEmailUrl() {
        return USER_EMAIL_URL;
    }

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
