package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.model.AccessToken;

/**
 * Created by max on 12/6/17 .
 */
public class LinkedInOauth2Configs extends AbstractOauth2Configs {

    @Override
    String getAccessTokenURL() {
        return "https://www.linkedin.com/oauth/v2/accessToken";
    }

    @Override
    public String getAuthorizationURL() {
        return "https://www.linkedin.com/uas/oauth2/authorization";
    }

    @Override
    protected String getUSerDataUrl() {
        return "https://api.linkedin.com/v1/people/~:(first-name,last-name,id,picture-url,email-address,location)?format=json";
    }

    @Override
    public String getClientId() {
        return "78njxd1uv7zrvq";//todo config
    }

    @Override
    @Deprecated
    /**
     * temp token for testing
     */
    public AccessToken getAccessToken(String code) {
        AccessToken accessTokenResponse = new AccessToken();
        accessTokenResponse.setAccess_token("AQVGeu0pcvo9lKAR2oJSJ1yq3xIz6OZpc3jFN52rstl6hGNMQDTQ86cWGK_C-YaAA5XfHwBXcbNMckyrtnAGSFQd1QQMw8SlE5wkCfgbTsx4O7_8DSQVXJ3q3tLny_MyAUqEuBYxgiL-N8ty2aREx257Teyeeq_lVIbvyXn_cNZ9jT2RQCb8iTBAqyk7HhkSLApJk-3_hcfJVVl-tzCHxBv9kNEZfR9AZkiQ5NpBsURi47Xbfu8EDxKkpgXZ0-MZ4kDRtsmJWNmQ-dGocqW-waYo1Wa8DR5I31vtSAmGOAzkiJXpgdKZnVsektrKyiBm0jLL_0eHUhtB59hf8ADhrkC_5v2O_Q");
        accessTokenResponse.setExpires_in(5183999);
//        return accessTokenResponse;
        return super.getAccessToken(code);
    }

    @Override
    public String getSecretKey() {
        return "HIGUbb2OmjFIxkEG";//todo config
    }

}
