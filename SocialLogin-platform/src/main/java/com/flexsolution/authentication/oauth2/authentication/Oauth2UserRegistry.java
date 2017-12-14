package com.flexsolution.authentication.oauth2.authentication;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.management.subsystems.ActivateableBean;
import org.alfresco.repo.security.sync.NodeDescription;
import org.alfresco.repo.security.sync.UserRegistry;
import org.alfresco.service.namespace.QName;

import java.util.*;

/**
 * Registry bean for returning a list of blocked field for user profile
 */
public class Oauth2UserRegistry implements UserRegistry, ActivateableBean {

    private static final HashSet<QName> Q_NAMES = new HashSet<>();

    static {
        Q_NAMES.add(ContentModel.PROP_FIRSTNAME);
        Q_NAMES.add(ContentModel.PROP_LASTNAME);
        Q_NAMES.add(ContentModel.PROP_EMAIL);
        Q_NAMES.add(ContentModel.PROP_LOCATION);
        Q_NAMES.add(ContentModel.PROP_JOBTITLE);
//        Q_NAMES.add(ContentModel.PROP_PERSONDESC);//todo this field is text area and doesn't block by Share UI and avatar too
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public Set<QName> getPersonMappedProperties() {
        return Q_NAMES;
    }

    @Override
    public Collection<NodeDescription> getPersons(Date modifiedSince) {
        return Collections.emptySet();
    }

    @Override
    public Collection<NodeDescription> getGroups(Date modifiedSince) {
        return Collections.emptySet();
    }

    @Override
    public Collection<String> getPersonNames() {
        return Collections.emptySet();
    }

    @Override
    public Collection<String> getGroupNames() {
        return Collections.emptySet();
    }
}
