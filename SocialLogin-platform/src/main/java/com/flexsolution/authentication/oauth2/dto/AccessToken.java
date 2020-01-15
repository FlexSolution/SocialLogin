package com.flexsolution.authentication.oauth2.dto;


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

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }
}
