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

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.providers.dao.AuthenticationDao;
import net.sf.acegisecurity.providers.dao.UsernameNotFoundException;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AbstractAuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;


/**
 * This implementation of an AuthenticationComponent can be configured to accept or reject all attempts to login.
 * 
 * This only affects attempts to login using a user name and password.
 * Authentication filters etc. could still support authentication but not via user names and passwords.
 * For example, where they set the current user using the authentication component.
 * Then the current user is set in the security context and asserted to be authenticated.
 * 
 * By default, the implementation rejects all authentication attempts.
 *  
 * @author Andy Hind
 */
public class SimpleAcceptOrRejectAllAuthenticationComponentImpl extends AbstractAuthenticationComponent
{
    private AuthenticationDao authenticationDao;

    public SimpleAcceptOrRejectAllAuthenticationComponentImpl()
    {
        super();
    }

    public void setAuthenticationDao(AuthenticationDao authenticationDao)
    {
        this.authenticationDao = authenticationDao;
    }

   public void authenticateImpl(String userName, char[] password) throws AuthenticationException
    {
        if(userName.equals(AlfrescoTransactionSupport.getResource("authenticationUserName")))//todo filter here, Now is always true!!!
        {
            setCurrentUser(userName);
        }
        else
        {
            throw new AuthenticationException("Access Denied");
        }

    }

    @Override
    protected boolean implementationAllowsGuestLogin()
    {
       return false;
    }


    /**
     * We actually have an acegi object so override the default method.
     */
//    @Override
    protected UserDetails getUserDetails1(String userName)//todo overwrite
    {
        UserDetails userDetails = null;
//        if (AuthenticationUtil.isMtEnabled())
        if (true)
        {
            // ALF-9403 - "manual" runAs to avoid clearing ticket, eg. when called via "validate" (->setCurrentUser->CheckCurrentUser)
            Authentication originalFullAuthentication = AuthenticationUtil.getFullAuthentication();
            try
            {
                if (originalFullAuthentication == null)
                {
                    AuthenticationUtil.setFullyAuthenticatedUser(getSystemUserName(getUserDomain(userName)));
                }
                userDetails = authenticationDao.loadUserByUsername(userName);
            }
            catch (UsernameNotFoundException unfe)
            {
                // the user was not created beforehand
                userDetails = super.getUserDetails(userName);
            }
            finally
            {
                if (originalFullAuthentication == null)
                {
                    ContextHolder.setContext(null); // note: does not clear ticket (unlike AuthenticationUtil.clearCurrentSecurityContext())
                }
            }
        }
        else
        {
            try
            {
                userDetails = authenticationDao.loadUserByUsername(userName);
            }
            catch (UsernameNotFoundException unfe)
            {
                // the user was not created beforehand
                userDetails = super.getUserDetails(userName);
            }
        }
        return userDetails;
    }
}
