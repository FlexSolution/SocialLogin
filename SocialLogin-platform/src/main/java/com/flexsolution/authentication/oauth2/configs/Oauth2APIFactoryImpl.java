package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.constant.Oauth2Session;
import com.flexsolution.authentication.oauth2.webscript.WebScriptUtils;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by max on 12/7/17 .
 */
public class Oauth2APIFactoryImpl implements Oauth2APIFactory, Oauth2APIFactoryRegisterInterface {

    private Map<String, Oauth2Configs> registeredAPIs = new HashMap<>();

    @Override
    public Oauth2Configs storeAPIConfigInUserSession(WebScriptRequest req, String api) {
        Oauth2Configs config = findConfig(api);
        WebScriptUtils.setSessionAttribute(req, Oauth2Session.API_PROVIDER, api);
        return config;
    }

    @Override
    public Oauth2Configs getAPIFromUserSession(WebScriptRequest req) {
        return findConfig((String) WebScriptUtils.getSessionAttribute(req, Oauth2Session.API_PROVIDER));
    }

    private Oauth2Configs findConfig(String api) {
        return Optional.ofNullable(registeredAPIs.get(api)).orElseThrow(() ->
                new WebScriptException(Status.STATUS_NOT_FOUND, "api [" + api + "] is not registered"));
    }

    @Override
    public void registerAPI(AbstractOauth2Configs registeredAPIs, String name) {
        this.registeredAPIs.put(name, registeredAPIs);
    }
}
