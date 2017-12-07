package com.flexsolution.authentication.oauth2.webscript;

import com.flexsolution.authentication.oauth2.configs.AbstractOauth2Configs;
import com.flexsolution.authentication.oauth2.configs.Oauth2Configs;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by max on 12/6/17 .
 */
public class AuthorizationUrlWebScript extends DeclarativeWebScript {

    private static final String AUTHORIZATION_URL = "authorizationUrl";
    private Map<String, Oauth2Configs> registeredAPIs = new HashMap<>();

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();

        String api = templateVars.get("api");

        if (StringUtils.isBlank(api)) {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "'api' is missing");
        }

        try {
            Oauth2Configs config = getAPIConfig(req, api);
            String state = initOauth2StateValue(req);
            HashMap<String, Object> model = new HashMap<>();
            model.put(AUTHORIZATION_URL, config.constructFullAuthorizationUrl(state));
            return model;
        } catch (UnsupportedEncodingException e) {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private Oauth2Configs getAPIConfig(WebScriptRequest req, String api) {
        Oauth2Configs config = Optional.ofNullable(registeredAPIs.get(api)).orElseThrow(() ->
                new WebScriptException(Status.STATUS_NOT_FOUND, "api [" + api + "] is not registered"));
        setSessionAttribute(req, Oauth2Session.API_PROVIDER, api);
        return config;
    }

    private String initOauth2StateValue(WebScriptRequest req) {
        String state = RandomStringUtils.random(20, true, true);
        setSessionAttribute(req, Oauth2Session.OAUTH_2_STATE, state);
        return state;
    }

    private void setSessionAttribute(WebScriptRequest req, String key, Serializable value) {
        final HttpServletRequest httpServletRequest = WebScriptServletRuntime.getHttpServletRequest(req);
        httpServletRequest.getSession().setAttribute(key, value);
    }

    /**
     * autowired by spring
     * @param name api string name for provider
     * @param registeredAPIs bean implementation
     */
    public void registerAPI(AbstractOauth2Configs registeredAPIs, String name) {
        this.registeredAPIs.put(name, registeredAPIs);
    }
}
