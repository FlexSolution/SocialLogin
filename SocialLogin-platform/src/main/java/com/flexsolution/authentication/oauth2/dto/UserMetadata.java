package com.flexsolution.authentication.oauth2.dto;


import java.util.Map;

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
    private Location lastName = new Location();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalizedFirstName() {
        return localizedFirstName;
    }

    public void setLocalizedFirstName(String localizedFirstName) {
        this.localizedFirstName = localizedFirstName;
    }

    public String getLocalizedLastName() {
        return localizedLastName;
    }

    public void setLocalizedLastName(String localizedLastName) {
        this.localizedLastName = localizedLastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getPublicProfileUrl() {
        return publicProfileUrl;
    }

    public void setPublicProfileUrl(String publicProfileUrl) {
        this.publicProfileUrl = publicProfileUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public Location getLastName() {
        return lastName;
    }

    public void setLastName(Location lastName) {
        this.lastName = lastName;
    }

    public class Location {

        private Map<String,String> localized;

        private String name;

        public Map<String, String> getLocalized() {
            return localized;
        }

        public void setLocalized(Map<String, String> localized) {
            this.localized = localized;
        }

        public String getName() {
            for(Map.Entry<String,String> ent : getLocalized().entrySet()){
                name=ent.getKey();
            }
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
