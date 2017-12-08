package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.constant.Oauth2Parameters;
import com.flexsolution.authentication.oauth2.model.AccessToken;
import com.flexsolution.authentication.oauth2.model.UserMetadata;
import com.google.gson.Gson;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.UrlUtil;
import org.apache.commons.lang.CharEncoding;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 12/6/17 .
 */
public abstract class AbstractOauth2Configs implements Oauth2Configs {

    private static final String SHARE_REDIRECT_URL = "/service/api/social-login";
    private static final String ARGUMENTS = "?response_type=%s&redirect_uri=%s&state=%s&client_id=%s";

    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private static final String X_LI_FORMAT = "x-li-format";

    private String apiName;
    private SysAdminParams sysAdminParams;
    private Oauth2APIFactoryRegisterInterface registerAPI;

    abstract String getSecretKey();

    abstract String getAccessTokenURL();

    protected abstract String getClientId();

    protected abstract String getAuthorizationURL();

    protected abstract String getUserDataUrl();


    private String getRedirectURL() {
        return UrlUtil.getShareUrl(sysAdminParams) + SHARE_REDIRECT_URL;
    }

    @Override
    public String constructFullAuthorizationUrl(String state) throws UnsupportedEncodingException {

        ParameterCheck.mandatoryString(Oauth2Parameters.STATE, state);

        String fullUrl = getAuthorizationURL() + ARGUMENTS;

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
                    throw new WebScriptException(Status.STATUS_UNAUTHORIZED, entity.toString());//todo test
                }
            }

        } catch (IOException e) {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, e.toString(), e);
        }
    }

    @Override
    public UserMetadata getUserMetadata(AccessToken accessToken) {

        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            HttpGet get = new HttpGet(getUserDataUrl());
            get.setHeader(X_LI_FORMAT, WebScriptResponse.JSON_FORMAT);
            get.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            get.setHeader(HttpHeaders.AUTHORIZATION, OAuth2Version.BEARER.getAuthorizationHeaderValue(accessToken.getAccess_token()));

            try (CloseableHttpResponse response = httpclient.execute(get)) {

                int statusCode = response.getStatusLine().getStatusCode();

                HttpEntity entity = response.getEntity();

                if (statusCode == Status.STATUS_OK) {

                    Gson gson = new Gson();

                    String responseString = EntityUtils.toString(entity, CharEncoding.UTF_8);

                    System.out.println(responseString);

                    return gson.fromJson(responseString, UserMetadata.class);

                } else {
                    throw new WebScriptException(Status.STATUS_UNAUTHORIZED, entity.toString());//todo test
                }
            }
        } catch (IOException e) {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, e.toString());
        }
    }


    private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, CharEncoding.UTF_8);
    }

    public void setSysAdminParams(SysAdminParams sysAdminParams) {
        this.sysAdminParams = sysAdminParams;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public void setRegisterAPI(Oauth2APIFactoryRegisterInterface registerAPI) {
        this.registerAPI = registerAPI;
    }

    private void init() {
        registerAPI.registerAPI(this, apiName);
    }
}
