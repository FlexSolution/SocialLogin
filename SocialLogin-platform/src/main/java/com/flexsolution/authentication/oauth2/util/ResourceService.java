package com.flexsolution.authentication.oauth2.util;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Created by max on 11/14/17 .
 */
public interface ResourceService {

    NodeRef getNode(String fileLocation);

    NodeRef getNode(String fileLocation, QName type);
}
