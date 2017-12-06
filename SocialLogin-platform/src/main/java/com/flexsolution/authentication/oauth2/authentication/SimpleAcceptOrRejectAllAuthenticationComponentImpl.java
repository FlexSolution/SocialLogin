/*
 * #%L
 * Alfresco Repository
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.flexsolution.authentication.oauth2.authentication;

import com.flexsolution.authentication.oauth2.webscript.LinkedInSignInWebScript;
import org.alfresco.repo.security.authentication.AbstractAuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;

import java.util.stream.Stream;


public class SimpleAcceptOrRejectAllAuthenticationComponentImpl extends AbstractAuthenticationComponent {

    private boolean enabled;

    public SimpleAcceptOrRejectAllAuthenticationComponentImpl() {
        super();
    }

    public void authenticateImpl(String userName, char[] password) throws AuthenticationException {
        if (enabled && userName.equals(AlfrescoTransactionSupport.getResource("authenticationUserName")) &&
                // double check if it is called from our web script
                Stream.of(Thread.currentThread().getStackTrace()).anyMatch(s ->
                        LinkedInSignInWebScript.class.getName().equals(s.getClassName()))) {
            setCurrentUser(userName);
        } else {
            throw new AuthenticationException("Access Denied");
        }
    }

    @Override
    protected boolean implementationAllowsGuestLogin() {
        return false;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
