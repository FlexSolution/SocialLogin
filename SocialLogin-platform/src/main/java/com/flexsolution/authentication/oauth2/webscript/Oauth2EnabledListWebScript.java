package com.flexsolution.authentication.oauth2.webscript;

import com.flexsolution.authentication.oauth2.configs.Oauth2APIFactory;
import com.flexsolution.authentication.oauth2.dto.SocialButton;
import com.google.gson.Gson;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Return the list of enabled Oauth2 providers
 */
public class Oauth2EnabledListWebScript extends DeclarativeWebScript {

    private static final String RESPONSE = "response";
    private Oauth2APIFactory oauth2APIFactory;

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        HashMap<String, Object> model = new HashMap<>();
        List<SocialButton> socialButtons = AuthenticationUtil.runAs(() ->
                        oauth2APIFactory.getAllEnabledServicesNames(),
                AuthenticationUtil.getAdminUserName());
        Gson gson = new Gson();
        model.put(RESPONSE, gson.toJson(socialButtons));
        return model;
    }

    public void setOauth2APIFactory(Oauth2APIFactory oauth2APIFactory) {
        this.oauth2APIFactory = oauth2APIFactory;
    }
}
