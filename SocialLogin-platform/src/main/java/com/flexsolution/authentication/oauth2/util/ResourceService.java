package com.flexsolution.authentication.oauth2.util;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * repository helper
 */
public interface ResourceService {

    NodeRef getNode(String fileLocation);

    /**
     * return a {@link NodeRef} by fileLocation or create new if not found
     * @param fileLocation string naming Path ("Data Dictionary/fs.oauth2.config")
     * @param type {@link QName} type of node, created if missed
     * @return {@link NodeRef}
     */
    NodeRef getNode(String fileLocation, QName type);
}
