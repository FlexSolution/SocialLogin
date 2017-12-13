package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.dto.AccessToken;
import com.flexsolution.authentication.oauth2.dto.SocialButton;
import com.flexsolution.authentication.oauth2.dto.UserMetadata;

import java.io.UnsupportedEncodingException;

/**
 * Public interface for Oauth2Config provider
 */
public interface Oauth2Config {

    /**
     * @param state random String value required for preventing CSRF attacks
     * @return full URL that can used for opening in the user's browser for allowing access by Oauth2 protocol
     * @throws UnsupportedEncodingException if using unsupported characters on the {@code state} parameter
     */
    String constructFullAuthorizationUrl(String state) throws UnsupportedEncodingException;

    /**
     * @param code The authorization code that you have received from {@link #constructFullAuthorizationUrl(String)}
     * @return {@link AccessToken}
     */
    AccessToken getAccessToken(String code);

    /**
     * @param accessToken {@link AccessToken} that you can get by {@link #getAccessToken(String)}
     * @return {@link UserMetadata} profile info, email, user Id.
     */
    UserMetadata getUserMetadata(AccessToken accessToken);

    /**
     * @return api short name that used for Alfresco user name generation
     */
    String getUserNamePrefix();

    /**
     * @return file name for avatar file
     */
    String getAvatarName();

    /**
     * @return true if current oauth2 provider is enabled
     */
    boolean isEnabled();

    /**
     * @return api short name for current oauth2 provider
     */
    String getApiName();

    /**
     * @return {@link SocialButton} data object for login page
     */
    SocialButton getSocialButton();
}
