package com.flexsolution.authentication.oauth2.configs;

/**
 * Created by max on 12/7/17 .
 */
public interface Oauth2APIFactoryRegisterInterface {

    /**
     * autowired by spring
     *
     * @param name           api string name for provider
     * @param registeredAPIs bean implementation
     */
    void registerAPI(AbstractOauth2Configs registeredAPIs, String name);
}
