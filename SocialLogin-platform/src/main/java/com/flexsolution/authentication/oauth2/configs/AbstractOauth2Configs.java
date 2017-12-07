package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.webscript.AuthorizationUrlWebScript;
import com.flexsolution.authentication.oauth2.webscript.Oauth2Parameters;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.UrlUtil;
import org.apache.commons.lang.CharEncoding;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by max on 12/6/17 .
 */
public abstract class AbstractOauth2Configs implements Oauth2Configs {

    private static final String SHARE_REDIRECT_URL = "/service/api/social-login";
    private static final String ARGUMENTS = "?response_type=%s&redirect_uri=%s&state=%s&client_id=%s";
    private String apiName;
    private AuthorizationUrlWebScript urlWebScript;
    private SysAdminParams sysAdminParams;

    @Override
    public String getRedirectURL() {
        return UrlUtil.getShareUrl(sysAdminParams) + SHARE_REDIRECT_URL;
    }

    @Override
    public String constructFullAuthorizationUrl(String state) throws UnsupportedEncodingException {

        ParameterCheck.mandatoryString(Oauth2Parameters.STATE, state);

        String fullUrl = getAuthorizationURL() + ARGUMENTS;

        return String.format(fullUrl, "code", encode(getRedirectURL()), encode(state), encode(getClientId()));
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

    public void setUrlWebScript(AuthorizationUrlWebScript urlWebScript) {
        this.urlWebScript = urlWebScript;
    }

    private void init() {
        urlWebScript.registerAPI(this, apiName);
    }
}
