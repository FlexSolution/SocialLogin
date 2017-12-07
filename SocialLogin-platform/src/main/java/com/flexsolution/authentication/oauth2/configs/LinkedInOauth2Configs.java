package com.flexsolution.authentication.oauth2.configs;

/**
 * Created by max on 12/6/17 .
 */
public class LinkedInOauth2Configs extends AbstractOauth2Configs {


    @Override
    public String getAuthorizationURL() {
        return "https://www.linkedin.com/uas/oauth2/authorization";
    }

    @Override
    public String getClientId() {
        return "78njxd1uv7zrvq";//todo config
    }

    @Override
    public String getSecretKey() {
        return "HIGUbb2OmjFIxkEG";//todo config
    }

}
