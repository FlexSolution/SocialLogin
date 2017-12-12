package com.flexsolution.authentication.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by max on 12/7/17 .
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class SocialButton {
    private String id;
    private String labelKey;
}
