package com.flexsolution.authentication.oauth2.configs;

import java.io.UnsupportedEncodingException;

/**
 * Created by max on 12/6/17 .
 */
public interface Oauth2Configs {


    String getAuthorizationURL();
    String getClientId();
    String getSecretKey();
    String getRedirectURL();


    String constructFullAuthorizationUrl(String state) throws UnsupportedEncodingException;
}
