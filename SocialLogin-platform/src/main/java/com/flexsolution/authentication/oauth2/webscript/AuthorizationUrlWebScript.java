package com.flexsolution.authentication.oauth2.webscript;

import com.flexsolution.authentication.oauth2.configs.Oauth2APIFactory;
import com.flexsolution.authentication.oauth2.configs.Oauth2Configs;
import com.flexsolution.authentication.oauth2.constant.Oauth2Session;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.extensions.webscripts.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by max on 12/6/17 .
 */
public class AuthorizationUrlWebScript extends DeclarativeWebScript {

    private static final String AUTHORIZATION_URL = "authorizationUrl";

    private Oauth2APIFactory oauth2APIFactory;


    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();

        String api = templateVars.get("api");

        if (StringUtils.isBlank(api)) {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "'api' is missing");
        }

        try {
            Oauth2Configs config = oauth2APIFactory.storeAPIConfigInUserSession(req, api);
            String state = initOauth2StateValue(req);
            HashMap<String, Object> model = new HashMap<>();
            model.put(AUTHORIZATION_URL, config.constructFullAuthorizationUrl(state));
            return model;
        } catch (UnsupportedEncodingException e) {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }


    private String initOauth2StateValue(WebScriptRequest req) {
        String state = RandomStringUtils.random(20, true, true);
        WebScriptUtils.setSessionAttribute(req, Oauth2Session.OAUTH_2_STATE, state);
        return state;
    }

    public void setOauth2APIFactory(Oauth2APIFactory oauth2APIFactory) {
        this.oauth2APIFactory = oauth2APIFactory;
    }
}
