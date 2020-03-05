package com.flexsolution.authentication.oauth2.configs;

import com.flexsolution.authentication.oauth2.dto.AccessToken;
import com.flexsolution.authentication.oauth2.dto.UserMetadata;
import com.flexsolution.authentication.oauth2.model.Oauth2ConfigModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.namespace.QName;

import java.util.Map;

public class GoogleInOauth2Configs extends AbstractOauth2Configs{

    private static final String GOOGLE = "google";
    private static final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_AVATAR_JPEG = "google_avatar.jpeg";
    private static final String ACCESS_URL = "https://oauth2.googleapis.com/token";
    private static final String USER_DATA_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String GOOGLE_URL_ARGUMENTS = "?response_type=%s&access_type=offline&redirect_uri=%s&state=%s&client_id=%s&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";

    @Override
    public UserMetadata getUserMetadata (AccessToken accessToken) {
        UserMetadata userMetadata = new UserMetadata();

        setProfileUserData(accessToken,userMetadata);

        return userMetadata;
    }

    private void setProfileUserData(AccessToken accessToken,UserMetadata userMetadata){
        Map userData = getMetadataParts(getUserDataUrl(),accessToken);
        userMetadata.setId(userData.get("sub").toString());
        userMetadata.setLocalizedFirstName(userData.get("given_name").toString());
        userMetadata.setLocalizedLastName(userData.get("family_name").toString());
        userMetadata.setPictureUrl(userData.get("picture").toString());
        userMetadata.setEmailAddress(userData.get("email").toString());
    }

    @Override
    public String getUserNamePrefix () {
        return GOOGLE;
    }

    @Override
    public String getAvatarName () {
        return GOOGLE_AVATAR_JPEG;
    }

    @Override
    public String getApiName () {
        return GOOGLE;
    }

    @Override
    String getUrlArguments () { return GOOGLE_URL_ARGUMENTS; }

    @Override
    String getAccessTokenURL () {
        return ACCESS_URL;
    }

    @Override
    QName getClientIdQName () {
        return Oauth2ConfigModel.PROP_GOOGLE_CLIENT_ID;
    }

    @Override
    QName getSecretKeyQName () {
        return Oauth2ConfigModel.PROP_GOOGLE_SECRET_KEY;
    }

    @Override
    QName getQNameForEnableField () {
        return Oauth2ConfigModel.PROP_GOOGLE_OAUTH2_SIGN_IN_ENABLED;
    }

    @Override
    String getAuthorizationURL () {
        return AUTHORIZATION_URL;
    }

    @Override
    String getUserDataUrl () {
        return USER_DATA_URL;
    }

    @Override
    String getClientId() {
        return AuthenticationUtil.runAs(() ->
                        (String) nodeService.getProperty(getOauth2ConfigFile(), getClientIdQName()),
                AuthenticationUtil.getAdminUserName());
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
