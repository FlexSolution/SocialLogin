package com.flexsolution.authentication.oauth2.constant;

/**
 * Inner repository user's session keys
 */
abstract public class Oauth2Session {

    /**
     * contains a {@link Oauth2Parameters#STATE} random generated value that will be
     * checked after allowing access on Oauth2 provider for preventing CSRF attacks.
     */
    public static final String OAUTH_2_STATE = "oauth2_state";

    /**
     * contains Oauth2Config implementation ipi short name that was chosen by user on the login page
     */
    public static final String API_PROVIDER = "api_provider";
}
