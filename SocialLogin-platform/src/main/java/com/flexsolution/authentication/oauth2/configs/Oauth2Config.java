package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.dto.AccessToken;
import com.flexsolution.authentication.oauth2.dto.SocialButton;
import com.flexsolution.authentication.oauth2.dto.UserMetadata;

import java.io.UnsupportedEncodingException;

/**
 * Created by max on 12/6/17 .
 */
public interface Oauth2Config {

    String constructFullAuthorizationUrl(String state) throws UnsupportedEncodingException;

    AccessToken getAccessToken(String code);

    UserMetadata getUserMetadata(AccessToken accessToken);

    String getUserNamePrefix();

    String getAvatarName();

    boolean isEnabled();

    String getApiName();

    SocialButton getSocialButton();
}
