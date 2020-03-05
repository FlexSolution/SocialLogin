package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.dto.AccessToken;
import com.flexsolution.authentication.oauth2.dto.UserMetadata;
import com.flexsolution.authentication.oauth2.model.Oauth2ConfigModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.namespace.QName;


import java.util.List;
import java.util.Map;

public class LinkedInOauth2Configs extends AbstractOauth2Configs {

    private static final String LINKED_IN = "linkedIn";
    private static final String LINKED_IN_AVATAR_JPEG = "LinkedIn_avatar.jpeg";
    private static final String ACCESS_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private static final String LINKEDIN_USER_DATA_URL = "https://api.linkedin.com/v2/me";
    private static final String LINKEDIN_USER_PHOTO_URL = "https://api.linkedin.com/v2/me?projection=(id,profilePicture(displayImage~:playableStreams))";
    private static final String LINKEDIN_USER_EMAIL_URL = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";

    private static final String LINKED_IN_ARGUMENTS = "?response_type=%s&redirect_uri=%s&state=%s&client_id=%s&scope=r_liteprofile r_emailaddress w_member_social";


    @Override
    String getUrlArguments () { return LINKED_IN_ARGUMENTS; }

    @Override
    String getAccessTokenURL() {
        return ACCESS_URL;
    }

    @Override
    String getAuthorizationURL() {
        return AUTHORIZATION_URL;
    }

    @Override
    String getUserDataUrl() {
        return LINKEDIN_USER_DATA_URL;
    }

    @Override
    QName getClientIdQName() {
        return Oauth2ConfigModel.PROP_LINKED_IN_CLIENT_ID;
    }

    @Override
    QName getSecretKeyQName() {
        return Oauth2ConfigModel.PROP_LINKED_IN_SECRET_KEY;
    }

    @Override
    QName getQNameForEnableField() {
        return Oauth2ConfigModel.PROP_LINKED_IN_OAUTH2_SIGN_IN_ENABLED;
    }

    @Override
    public String getUserNamePrefix() {
        return LINKED_IN;
    }

    @Override
    public String getAvatarName() {
        return LINKED_IN_AVATAR_JPEG;
    }

    @Override
    public String getApiName() {
        return LINKED_IN;
    }

    @Override
    public String getClientId () {
        return AuthenticationUtil.runAs(() ->
                        (String) nodeService.getProperty(getOauth2ConfigFile(), getClientIdQName()),
                AuthenticationUtil.getAdminUserName());
    }

    @Override
    public UserMetadata getUserMetadata(AccessToken accessToken) {

        UserMetadata userMetadata = new UserMetadata();

        setUnderlyingUserData(accessToken,userMetadata);
        setUserEmail(accessToken,userMetadata);
        setUserPhotoUrl(accessToken,userMetadata);

        logger.debug(userMetadata);
        return userMetadata;
    }

    private void setUnderlyingUserData(AccessToken accessToken,UserMetadata userMetadata){
        Map userData = getMetadataParts(getUserDataUrl(),accessToken);
        userMetadata.setId(userData.get("id").toString());
        userMetadata.setLocalizedFirstName(userData.get("localizedFirstName").toString());
        userMetadata.setLocalizedLastName(userData.get("localizedLastName").toString());
    }

    private void setUserEmail (AccessToken accessToken, UserMetadata userMetadata){
        Map emailFullJson = getMetadataParts(LINKEDIN_USER_EMAIL_URL,accessToken);
        List listElements = (List)emailFullJson.get("elements");
        Map getElement = (Map) listElements.get(0);
        Map getHandleObject = (Map) getElement.get("handle~");
        userMetadata.setEmailAddress(getHandleObject.get("emailAddress").toString());
    }

    private void setUserPhotoUrl(AccessToken accessToken, UserMetadata userMetadata){
        Map pictureUrlScheme = getMetadataParts(LINKEDIN_USER_PHOTO_URL,accessToken);
        Map getProfilePicture = (Map) pictureUrlScheme.get("profilePicture");
        Map getDisplayImage = (Map) getProfilePicture.get("displayImage~");
        List listElements = (List) getDisplayImage.get("elements");
        Map getElement = (Map) listElements.get(0);
        List listIdentifiers = (List)getElement.get("identifiers");
        Map getIdentifier = (Map) listIdentifiers.get(0);
        userMetadata.setPictureUrl(getIdentifier.get("identifier").toString());
    }

    @Override
    String getSecretKey() {
        return AuthenticationUtil.runAs(() ->
                        (String) nodeService.getProperty(getOauth2ConfigFile(), getSecretKeyQName()),
                AuthenticationUtil.getAdminUserName());
    }

    @Override
    public boolean isEnabled() {
        return AuthenticationUtil.runAs(() ->
                        Boolean.TRUE.equals(nodeService.getProperty(getOauth2ConfigFile(), getQNameForEnableField())),
                AuthenticationUtil.getAdminUserName());
    }

}
