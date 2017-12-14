package com.flexsolution.authentication.oauth2.webscript;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy authorization endpoint that will be called by Oauth2 provider after succeed allowing access.
 * Authenticate user in Share after receiving an Alfresco Ticket from Alfresco repository
 */
public class AuthenticationURLProxy extends DeclarativeWebScript {

    private static final Logger logger = LogManager.getLogger(AuthenticationURLProxy.class);

    private static final String AUTHORIZATION_URL = "authorizationUrl";
    private static final String STATUS = "status";
    private static final String DESCRIPTION = "description";
    private static final String API = "api";

    private ConnectorService connectorService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();

        String api = templateVars.get(API);

        if (StringUtils.isBlank(api)) {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "'api' is missing");
        }

        final HttpServletRequest httpServletRequest = WebScriptServletRuntime.getHttpServletRequest(req);

        HttpSession session = httpServletRequest.getSession(true);

        try {
            final Connector connector = connectorService.getConnector(WebFrameworkConstants.DEFAULT_ALFRESCO_ENDPOINT_ID, session);

            final Response call = connector.call("/socialLogin/" + api + "/authorizationUrl");

            if (call.getStatus().getCode() == Status.STATUS_OK) {
                Map<String, Object> model = new HashMap<>();
                model.put(AUTHORIZATION_URL, new JSONObject(call.getText()).getString(AUTHORIZATION_URL));
                return model;

            } else {
                // receive description of the error
                JSONObject jsonErrObject = new JSONObject(call.getText());
                String errMessage = (String) ((JSONObject) jsonErrObject.get(STATUS)).get(DESCRIPTION);
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, errMessage);
            }
        } catch (ConnectorServiceException | JSONException e) {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

    }

    /**
     * Sets the connector service
     *
     * @param connectorService the connectorService to set
     * @see ConnectorService
     */
    public void setConnectorService(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }
}