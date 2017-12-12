package com.flexsolution.authentication.oauth2.configs;

/**
 * Created by max on 3/1/16
 */
public class Oauth2Exception extends Exception {

    public Oauth2Exception(String message) {
        super(message);
    }

    public Oauth2Exception(Throwable cause) {
        super(cause);
    }

    public Oauth2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Get the root cause.
     */
    public Throwable getRootCause() {
        Throwable cause = this;
        for (Throwable tmp = this; tmp != null; tmp = cause.getCause()) {
            cause = tmp;
        }
        return cause;
    }
}
