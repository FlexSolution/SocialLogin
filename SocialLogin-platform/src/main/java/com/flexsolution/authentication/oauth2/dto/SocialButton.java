package com.flexsolution.authentication.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class SocialButton {
    /**
     * api short name
     */
    private String id;
    /**
     * localized key for display name of Sign In button
     */
    private String labelKey;
}
