package com.flexsolution.authentication.oauth2.configs;

/**
 * Created by max on 12/6/17 .
 */
public class LinkedInOauth2Configs extends AbstractOauth2Configs {

    private static final String LINKED_IN = "linkedIn_";
    private static final String LINKED_IN_AVATAR_JPEG = "LinkedIn_avatar.jpeg";
    private static final String ACCESS_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String USER_DATA_URL = "https://api.linkedin.com/v1/people/~:(first-name,last-name,id,picture-url,email-address,location,headline,industry,current-share,summary,specialties,positions,public-profile-url)?format=json";

    @Override
    String getAccessTokenURL() {
        return ACCESS_URL;
    }

    @Override
    public String getAuthorizationURL() {
        return AUTHORIZATION_URL;
    }

    @Override
    protected String getUserDataUrl() {
        return USER_DATA_URL;
    }

    @Override
    public String getClientId() {
        return "78njxd1uv7zrvq";//todo config
    }

    @Override
    public String getSecretKey() {
        return "HIGUbb2OmjFIxkEG";//todo config
    }

    @Override
    public String getUserNamePrefix() {
        return LINKED_IN;
    }

    @Override
    public String getAvatarName() {
        return LINKED_IN_AVATAR_JPEG;
    }
}
