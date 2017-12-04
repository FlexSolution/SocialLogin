package com.flexsolution.authentication.oauth2.webscript;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString //todo remove

/**
 * https://developer.linkedin.com/docs/oauth2
 */
public class AccessTokenResponse {
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
