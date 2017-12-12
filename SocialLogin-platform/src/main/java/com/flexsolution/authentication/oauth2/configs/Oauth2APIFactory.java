package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.dto.SocialButton;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.List;

/**
 * Created by max on 12/7/17 .
 */
public interface Oauth2APIFactory {

    Oauth2Config storeAPIConfigInUserSession(WebScriptRequest req, String api) throws Oauth2Exception;

    Oauth2Config getAPIFromUserSession(WebScriptRequest req) throws Oauth2Exception;

    Oauth2Config findApiConfig(String api) throws Oauth2Exception;

    List<SocialButton> getAllEnabledServicesNames();
}
