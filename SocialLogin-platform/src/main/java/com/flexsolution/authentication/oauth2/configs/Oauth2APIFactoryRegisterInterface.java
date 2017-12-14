package com.flexsolution.authentication.oauth2.configs;

/**
 * interface for auto registration Oauth2Configs by api short name
 */
public interface Oauth2APIFactoryRegisterInterface {

    /**
     * autowired by spring (do not use it manually)
     *
     * @param api            api short name for provider
     * @param registeredAPIs bean implementation
     */
    void registerAPI(AbstractOauth2Configs registeredAPIs, String api);
}
