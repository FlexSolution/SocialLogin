package com.flexsolution.authentication.oauth2.model;

import org.alfresco.service.namespace.QName;


public abstract class Oauth2ConfigModel {

    public static final String NAMESPACE = "http://flex-solution.com/model/analytics/1.0";
    public static final String PREFIX = "fs";

    // Types
    public static final QName TYPE_OAUTH2_CONFIG = QName.createQName(NAMESPACE, "oauth2-config");

    // Aspects
    public static final QName ASPECT_OAUTH2_LINKED_IN_CONFIG = QName.createQName(NAMESPACE, "Oauth2LinkedInConfig");

    // LinkedIn Properties
    public static final QName PROP_LINKED_IN_CLIENT_ID = QName.createQName(NAMESPACE, "LinkedInClientId");
    public static final QName PROP_LINKED_IN_SECRET_KEY = QName.createQName(NAMESPACE, "LinkedInSecretKey");
    public static final QName PROP_LINKED_IN_OAUTH2_SIGN_IN_ENABLED = QName.createQName(NAMESPACE, "LinkedInOauth2SignInEnabled");
    // Google Properties
    public static final QName PROP_GOOGLE_CLIENT_ID = QName.createQName(NAMESPACE, "GoogleClientId");
    public static final QName PROP_GOOGLE_SECRET_KEY = QName.createQName(NAMESPACE, "GoogleSecretKey");
    public static final QName PROP_GOOGLE_OAUTH2_SIGN_IN_ENABLED = QName.createQName(NAMESPACE, "GoogleOauth2SignInEnabled");
}
