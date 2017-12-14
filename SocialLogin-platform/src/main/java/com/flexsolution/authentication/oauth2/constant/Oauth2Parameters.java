package com.flexsolution.authentication.oauth2.constant;

/**
 * Oauth2 URL parameters
 */
public abstract class Oauth2Parameters {
    /**
     * A value used to test for possible CSRF attacks.
     */
    public static final String STATE = "state";
    /**
     * The OAuth 2.0 authorization code.
     */
    public static final String CODE = "code";
    /**
     * A code indicating the type of error. Two available strings are:<br>
     * "user_cancelled_login" - The user refused to login into LinkedIn account.<br>
     * "user_cancelled_authorize" - The user refused to authorize permissions request
     */
    public static final String ERROR = "error";
    /**
     * The value of this field should always be:  "authorization_code"
     */
    public static final String GRANT_TYPE = "grant_type";
    /**
     * The "API Key" value form Oauth2 provider
     */
    public static final String CLIENT_ID = "client_id";
    /**
     * The "Secret Key" value form Oauth2 provider
     */
    public static final String CLIENT_SECRET = "client_secret";
    /**
     * The URI your users will be sent back to after authorization.<br>
     * This value must match one of the defined OAuth 2.0 Redirect URLs in your application configuration.<br>
     * e.g. https://www.example.com/auth/linkedin
     */
    public static final String REDIRECT_URI = "redirect_uri";
}
