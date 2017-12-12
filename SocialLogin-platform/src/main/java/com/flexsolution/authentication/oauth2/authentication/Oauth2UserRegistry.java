package com.flexsolution.authentication.oauth2.authentication;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.sync.NodeDescription;
import org.alfresco.repo.security.sync.UserRegistry;
import org.alfresco.service.namespace.QName;

import java.util.*;

/**
 * Created by max on 12/12/17 .
 */
//todo fix stability
@Deprecated
public class Oauth2UserRegistry implements UserRegistry {

    private static final HashSet<QName> Q_NAMES = new HashSet<>();

    static {
        Q_NAMES.add(ContentModel.PROP_FIRSTNAME);
        Q_NAMES.add(ContentModel.PROP_LASTNAME);
        Q_NAMES.add(ContentModel.PROP_EMAIL);
        Q_NAMES.add(ContentModel.PROP_LOCATION);
        Q_NAMES.add(ContentModel.PROP_JOBTITLE);
        Q_NAMES.add(ContentModel.PROP_PERSONDESC);
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
