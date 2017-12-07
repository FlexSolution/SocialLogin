package com.flexsolution.authentication.oauth2.model;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
        /*
         * https://developer.linkedin.com/docs/oauth2
         */
public class AccessToken {
    /***
     * The access token for the user.  This value must be kept secure, as per your agreement to the
     */
    private String access_token;

    /**
     * The number of seconds remaining, from the time it was requested, before the token will expire.
     * Currently, all access tokens are issued with a 60 day lifespan.
     */
    private Integer expires_in;
}
