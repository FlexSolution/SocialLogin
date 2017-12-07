package com.flexsolution.authentication.oauth2.webscript;

import org.alfresco.web.site.SlingshotUserFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.CredentialVaultProviderException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.connector.*;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by max on 11/24/17 .
 */
public class SocialSecure extends AbstractWebScript {

    private static final Logger logger = LogManager.getLogger(SocialSecure.class);

    private ConnectorService connectorService;
    private UserFactory userFactory;
    private WebFrameworkConfigElement webFrameworkConfiguration;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

        String error = req.getParameter("error");//todo error
        if (StringUtils.isNotBlank(error)) {
            System.out.println(error);
            /*
            user_cancelled_login - The user refused to login into LinkedIn account.
            user_cancelled_authorize - The user refused to authorize permissions request from your application.
             */
            System.out.println(StringUtils.isNotBlank(req.getParameter("error_description")));
        }

        String code = req.getParameter("code");
        String state = req.getParameter("state");

        final HttpServletRequest httpServletRequest = WebScriptServletRuntime.getHttpServletRequest(req);
        final HttpServletResponse servletResponse = WebScriptServletRuntime.getHttpServletResponse(res);

        HttpSession session = httpServletRequest.getSession(true);

        try {
            final Connector connector = connectorService.getConnector(WebFrameworkConstants.DEFAULT_ALFRESCO_ENDPOINT_ID, session);

            final Response call = connector.call("/socialLogin?code=" + code + "&state=" + state);

            if (call.getStatus().getCode() == Status.STATUS_OK) {

                final String text = call.getText();
                final JSONObject jsonObject = new JSONObject(text);

                final String ticket = jsonObject.getString("ticket");
                final String user = jsonObject.getString("user");

                logger.debug("Getting new alfresco ticket for user: " + user);

                AuthenticationUtil.login(httpServletRequest, servletResponse, user, false,
                        webFrameworkConfiguration.isLoginCookiesEnabled());

                // Add activiti-admin and alfresco credentials to the vault as well.
                CredentialVault vault = connectorService.getCredentialVault(session, user);
                Credentials credentials = vault.newCredentials(SlingshotUserFactory.ACTIVITI_ADMIN_ENDPOINT_ID);
                credentials.setProperty(Credentials.CREDENTIAL_USERNAME, user);

                Credentials credentials2 = vault.newCredentials(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
                credentials2.setProperty(Credentials.CREDENTIAL_USERNAME, user);

                ConnectorSession connectorSession = connector.getConnectorSession();
                connectorSession.setParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET, ticket);
                connectorSession.setParameter(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH, Boolean.TRUE.toString());

                servletResponse.setStatus(Status.STATUS_FOUND);

                servletResponse.sendRedirect(req.getContextPath() +
                        ((SlingshotUserFactory) userFactory).getUserHomePage(ThreadLocalRequestContext.getRequestContext(),
                                user));
            } else {
                // receive description of the error
                JSONObject jsonErrObject = new JSONObject(call.getText());
                String errMessage = (String) ((JSONObject) jsonErrObject.get("status")).get("description");
                logger.error("User switching is failed due to an error :\n" + call.getText());
                // set error status and message
                servletResponse.sendError(Status.STATUS_BAD_REQUEST, errMessage);
            }


        } catch (ConnectorServiceException e) {
            logger.error("Could not establish connection to alfresco or some error occurs", e);
            servletResponse.sendError(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JSONException e) {
            logger.error("Could not parse response into json object", e);
            servletResponse.sendError(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (UserFactoryException e) {
            logger.error("Could not load user home page", e);
            servletResponse.sendError(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (CredentialVaultProviderException e) {
            logger.error("Could not load user Credential Vault", e);
            servletResponse.sendError(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the connector service
     *
     * @param connectorService the connectorService to set
     * @see org.springframework.extensions.webscripts.connector.ConnectorService
     */
    public void setConnectorService(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    public void setWebFrameworkConfiguration(WebFrameworkConfigElement webFrameworkConfiguration) {
        this.webFrameworkConfiguration = webFrameworkConfiguration;
    }
}