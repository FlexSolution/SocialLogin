package com.flexsolution.authentication.oauth2.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class AccessToken {
    /***
     * The access token for the user.  This value must be kept secure
     */
    private String access_token;

    /**
     * The number of seconds remaining, from the time it was requested, before the token will expire.
     * Currently, all access tokens are issued with a 60 day lifespan.
     */
    private Integer expires_in;


}
