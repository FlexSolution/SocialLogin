package com.flexsolution.authentication.oauth2.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserMetadata {

    private String id;
    /**
     * The member's first name.
     */
    private String localizedFirstName;
    /**
     * The member's last name.
     */
    private String localizedLastName;
    /**
     * The LinkedIn member's primary email address.
     * Secondary email addresses associated with the member are not available via the API.
     */
    private String emailAddress;
    /**
     * A URL to the member's formatted profile picture, if one has been provided.
     */
    private String pictureUrl;
    /**
     * The industry the member belongs to.
     */
    private String industry;
    /**
     * The member's headline.
     */
    private String headline;
    /**
     * The URL to the member's public profile on LinkedIn.
     */
    private String publicProfileUrl;
    /**
     * A long-form text area describing the member's professional profile.
     */
    private String summary;
    /**
     * An object representing the user's physical location.
     */
    private String location;

}
