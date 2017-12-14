package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.constant.Oauth2Session;
import com.flexsolution.authentication.oauth2.dto.SocialButton;
import com.flexsolution.authentication.oauth2.webscript.WebScriptUtils;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.*;
import java.util.stream.Collectors;

public class Oauth2APIFactoryImpl implements Oauth2APIFactory, Oauth2APIFactoryRegisterInterface {

    private Map<String, Oauth2Config> registeredAPIs = new HashMap<>();

    @Override
    public Oauth2Config storeAPIConfigInUserSession(WebScriptRequest req, String api) throws Oauth2Exception {
        Oauth2Config config = findApiConfig(api);
        WebScriptUtils.setSessionAttribute(req, Oauth2Session.API_PROVIDER, api);
        return config;
    }

    @Override
    public Oauth2Config getAPIFromUserSession(WebScriptRequest req) throws Oauth2Exception {
        return findApiConfig((String) WebScriptUtils.getSessionAttribute(req, Oauth2Session.API_PROVIDER));
    }

    @Override
    public Oauth2Config findApiConfig(String api) throws Oauth2Exception {
        return Optional.ofNullable(registeredAPIs.get(api)).orElseThrow(() ->
                new Oauth2Exception("api [" + api + "] is not registered"));
    }

    @Override
    public List<SocialButton> getAllEnabledServicesNames() {
        return registeredAPIs.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getApiName()))
                .filter(a -> a.getValue().isEnabled())
                .map(api -> api.getValue().getSocialButton())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void registerAPI(AbstractOauth2Configs registeredAPIs, String api) {
        this.registeredAPIs.put(api, registeredAPIs);
    }
}
