package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.dto.SocialButton;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.List;

/**
 * Created by max on 12/7/17 .
 */
public interface Oauth2APIFactory {

    /**
     * store the API short name in the user session to loading exact implementation after allowing access on the Oauth2 service
     * @param req current WebScriptRequest
     * @param api api short name
     * @return {@link Oauth2Config} implementation
     * @throws Oauth2Exception if not found any implementation by api short name in Spring Context
     */
    Oauth2Config storeAPIConfigInUserSession(WebScriptRequest req, String api) throws Oauth2Exception;

    /**
     * load Oauth2Config implementation from user session that was stored while choosing Oauth2 provider on the login page
     * @param req current WebScriptRequest
     * @return {@link Oauth2Config} implementation
     * @throws Oauth2Exception if not found any implementation by api short name in Spring Context
     */
    Oauth2Config getAPIFromUserSession(WebScriptRequest req) throws Oauth2Exception;

    /**
     * find implementation by api short name in Spring Context
     * @param api api short name
     * @return {@link Oauth2Config} implementation
     * @throws Oauth2Exception if not found any implementation by api short name in Spring Context
     */
    Oauth2Config findApiConfig(String api) throws Oauth2Exception;

    /**
     * @return List of {@link SocialButton}s that registered in the Spring Context and have enabled option sorted by api short name
     */
    List<SocialButton> getAllEnabledServicesNames();
}
