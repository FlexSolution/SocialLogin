package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.constant.Oauth2Parameters;
import com.flexsolution.authentication.oauth2.dto.AccessToken;
import com.flexsolution.authentication.oauth2.dto.SocialButton;
import com.flexsolution.authentication.oauth2.model.Oauth2ConfigModel;
import com.flexsolution.authentication.oauth2.util.ResourceService;
import com.google.gson.Gson;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.UrlUtil;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.http.MediaType;
import org.springframework.social.oauth2.OAuth2Version;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


public abstract class AbstractOauth2Configs implements Oauth2Config {

    public static final String OAUTH2_CONFIG_NODE_PATH = "Data Dictionary/fs.oauth2.config";
    private static final String SHARE_REDIRECT_URL = "/service/api/social-login";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    protected static final String X_LI_FORMAT = "x-li-format";
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected NodeService nodeService;
    private SysAdminParams sysAdminParams;
    private Oauth2APIFactoryRegisterInterface registerAPI;
    protected ResourceService resourceService;
    private String labelKey;

    abstract String getSecretKey();

    abstract String getClientId();

    abstract String getUrlArguments();

    abstract String getAccessTokenURL();

    abstract QName getClientIdQName();

    abstract QName getSecretKeyQName();

    abstract QName getQNameForEnableField();

    abstract String getAuthorizationURL();

    abstract String getUserDataUrl();

    private String getRedirectURL() {
        return UrlUtil.getShareUrl(sysAdminParams) + SHARE_REDIRECT_URL;
    }

    @Override
    public String constructFullAuthorizationUrl(String state) throws UnsupportedEncodingException {

        ParameterCheck.mandatoryString(Oauth2Parameters.STATE, state);

        String fullUrl = getAuthorizationURL() + getUrlArguments();

        return String.format(fullUrl, CODE, encode(getRedirectURL()), encode(state), encode(getClientId()));

    }


    @Override
    public AccessToken getAccessToken(String code) {

        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {

            HttpPost post = new HttpPost(getAccessTokenURL());

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(Oauth2Parameters.CODE, code));
            params.add(new BasicNameValuePair(Oauth2Parameters.REDIRECT_URI, getRedirectURL()));
            params.add(new BasicNameValuePair(Oauth2Parameters.CLIENT_ID, getClientId()));
            params.add(new BasicNameValuePair(Oauth2Parameters.CLIENT_SECRET, getSecretKey()));
            params.add(new BasicNameValuePair(Oauth2Parameters.GRANT_TYPE, AUTHORIZATION_CODE));

            post.setEntity(new UrlEncodedFormEntity(params));
            post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

            try (CloseableHttpResponse response = httpclient.execute(post)) {

                int statusCode = response.getStatusLine().getStatusCode();

                HttpEntity entity = response.getEntity();

                if (statusCode == Status.STATUS_OK) {

                    Gson gson = new Gson();

                    String responseString = EntityUtils.toString(entity, CharEncoding.UTF_8);

                    return gson.fromJson(responseString, AccessToken.class);

                } else {
                    throw new WebScriptException(Status.STATUS_UNAUTHORIZED, entity.toString());
                }
            }

        } catch (IOException e) {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, e.toString(), e);
        }
    }

    protected Map getMetadataParts(String userDataUrl,AccessToken accessToken) {

        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            HttpGet metadataRequest = new HttpGet(userDataUrl);

            metadataRequest.setHeader(X_LI_FORMAT, WebScriptResponse.JSON_FORMAT);
            metadataRequest.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            metadataRequest.setHeader("X-Restli-Protocol-Version", "2.0.0");
            metadataRequest.setHeader(HttpHeaders.AUTHORIZATION, OAuth2Version.BEARER.getAuthorizationHeaderValue(accessToken.getAccess_token()));

            try (CloseableHttpResponse response = httpclient.execute(metadataRequest)) {

                int statusCode = response.getStatusLine().getStatusCode();

                HttpEntity entity = response.getEntity();

                if (statusCode == Status.STATUS_OK) {

                        Gson gson = new Gson();

                        String responseString = EntityUtils.toString(entity, CharEncoding.UTF_8);

                        logger.debug(responseString);

                        return gson.fromJson(responseString,Map.class);

                } else {
                    throw new WebScriptException(Status.STATUS_UNAUTHORIZED, entity.toString());
                }
            }
        } catch (IOException e) {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, e.toString());
        }
    }

    protected NodeRef getOauth2ConfigFile() {
        return resourceService.getNode(OAUTH2_CONFIG_NODE_PATH, Oauth2ConfigModel.TYPE_OAUTH2_CONFIG);
    }

    @Override
    public SocialButton getSocialButton() {
        return new SocialButton(getApiName(), labelKey);
    }


    private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, CharEncoding.UTF_8);
    }


    public void setSysAdminParams(SysAdminParams sysAdminParams) {
        this.sysAdminParams = sysAdminParams;
    }

    public void setRegisterAPI(Oauth2APIFactoryRegisterInterface registerAPI) {
        this.registerAPI = registerAPI;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    private void init() {
        registerAPI.registerAPI(this, getApiName());
    }
}
