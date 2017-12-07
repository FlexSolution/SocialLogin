package com.flexsolution.authentication.oauth2.configs;

import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Created by max on 12/7/17 .
 */
public interface Oauth2APIFactory {

    Oauth2Configs storeAPIConfigInUserSession(WebScriptRequest req, String api);

    Oauth2Configs getAPIFromUserSession(WebScriptRequest req);
}
