package com.flexsolution.authentication.oauth2.webscript;

import com.flexsolution.authentication.oauth2.configs.AbstractOauth2Configs;
import com.flexsolution.authentication.oauth2.model.Oauth2ConfigModel;
import com.flexsolution.authentication.oauth2.util.ResourceService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.*;

import java.util.HashMap;
import java.util.Map;

/**
 * return NodeRef of Oauth2 config for admin users
 */
public class Oauth2ConfigNodeWebScript extends DeclarativeWebScript {

    private static final String NODE_REF = "nodeRef";
    private ResourceService resourceService;

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        HashMap<String, Object> model = new HashMap<>();
        NodeRef node = resourceService.getNode(AbstractOauth2Configs.OAUTH2_CONFIG_NODE_PATH, Oauth2ConfigModel.TYPE_OAUTH2_CONFIG);
        if (node != null) {
            model.put(NODE_REF, node.toString());
            return model;
        } else {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "error creating or finding oauth2 config node");
        }
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
