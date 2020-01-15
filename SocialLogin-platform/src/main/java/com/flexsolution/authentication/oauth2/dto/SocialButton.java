package com.flexsolution.authentication.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class SocialButton {
    /**
     * api short name
     */
    private String id;
    /**
     * localized key for display name of Sign In button
     */
    private String labelKey;

    public SocialButton(String id, String labelKey) {
        this.id = id;
        this.labelKey = labelKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }
}
