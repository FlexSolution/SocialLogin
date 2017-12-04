/*
 * #%L
 * Alfresco Share WAR
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
package com.flexsolution.authentication.oauth2.filter;

import org.alfresco.util.log.NDC;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.mvc.PageViewResolver;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.servlet.DependencyInjectedFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Oauth2 Filter
 */
public class Oauth2AuthenticationFilter implements DependencyInjectedFilter, ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(Oauth2AuthenticationFilter.class);

    private static final String PAGE_SERVLET_PATH = "/page";
    private static final String LOGIN_PATH_INFORMATION = "/dologin";
    private static final String UNAUTHENTICATED_ACCESS_PROXY = "/proxy/alfresco-noauth";
    private static final String PAGE_VIEW_RESOLVER = "pageViewResolver";

    private ApplicationContext context;
    private ConnectorService connectorService;
    private String endpoint;
    private UserFactory userFactory;
    private ConfigService configService;

    /**
     * Initializing and checking the filter
     */
    public void init() {

        // Retrieve the remote configuration
        RemoteConfigElement remoteConfig = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");
        if (remoteConfig == null) {
            logger.error("There is no Remote configuration element. This is required to use Oauth2AuthenticationFilter.");
            return;
        }

        // get the endpoint id to use
        if (this.endpoint == null) {
            logger.error("There is no 'endpoint' property in the Oauth2AuthenticationFilter bean parameters. Cannot initialise filter.");
            return;
        }

        // Get the endpoint descriptor and check if external auth is enabled
        EndpointDescriptor endpointDescriptor = remoteConfig.getEndpointDescriptor(endpoint);

        try {
            this.connectorService.getConnector(endpoint);

        } catch (ConnectorServiceException e) {
            logger.error("Unable to find connector " + endpointDescriptor.getConnectorId() + " for the endpoint " + endpoint, e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }


    @Override
    public void doFilter(ServletContext context, ServletRequest request,
                         ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        doFilter(request, response, chain);
    }


    /**
     * Run the filter
     *
     * @param sreq  ServletRequest
     * @param sresp ServletResponse
     * @param chain FilterChain
     * @throws IOException
     * @throws ServletException
     */
    private void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain chain) throws IOException, ServletException {

        NDC.remove();
        NDC.push(Thread.currentThread().getName());
        final boolean debug = logger.isDebugEnabled();

        // Bypass the filter if we don't have an endpoint with external auth enabled
        if (this.endpoint == null) {
            if (debug)
                logger.debug("There is no endpoint with external auth enabled.");
            chain.doFilter(sreq, sresp);
            return;
        }

        // Get the HTTP request/response/session
        HttpServletRequest req = (HttpServletRequest) sreq;
        HttpServletResponse res = (HttpServletResponse) sresp;
        HttpSession session = req.getSession();

        if (req.getServletPath() != null && req.getServletPath().startsWith(UNAUTHENTICATED_ACCESS_PROXY)) {
            if (debug)
                logger.debug("Oauth2 is by-passed for unauthenticated access endpoint.");
            chain.doFilter(sreq, sresp);
            return;
        }

        if (debug) logger.debug("Processing request " + req.getRequestURI() + " SID:" + session.getId());

        // Login page or login submission
        String pathInfo = req.getPathInfo();
        if (PAGE_SERVLET_PATH.equals(req.getServletPath())
                && (LOGIN_PATH_INFORMATION.equals(pathInfo) || pathInfo == null)) {
            if (debug)
                logger.debug("Login page requested, chaining ...");

            // Chain to the next filter
            chain.doFilter(sreq, sresp);
            return;
        }


        // initialize a new request context
        RequestContext context = null;
        try {
            // perform a "silent" init - i.e. no user creation or remote connections
            context = RequestContextUtil.initRequestContext(this.context, req, true);
        } catch (Exception ex) {
            logger.error("Error calling initRequestContext", ex);
            throw new ServletException(ex);
        }

        // get the page from the model if any - it may not require authentication
        Page page = context.getPage();
        if (page == null && pathInfo != null) {
            // we didn't find a page - this may be a top-level URL call - so attempt to manually resolve the page
            PageViewResolver pageViewResolver = (PageViewResolver) this.context.getBean(PAGE_VIEW_RESOLVER);
            if (pageViewResolver != null) {
                try {
                    // as a side-effect of resolving the view ID into an View object
                    // the Page context will be updated on the request context for us
                    if (pageViewResolver.resolveViewName(pathInfo, null) != null) {
                        page = context.getPage();
                    }
                } catch (Exception e) {
                    // OK to fall back to null page reference if this happens
                }
            }
        }
        if (page != null && page.getAuthentication() == RequiredAuthentication.none) {
            if (logger.isDebugEnabled())
                logger.debug("Unauthenticated page requested - skipping auth filter...");
            chain.doFilter(sreq, sresp);
            return;
        }

        //todo testing
        Object ololoKey = session.getAttribute("OLOLO_KEY");
        if (ololoKey != null) {
            boolean authenticated = userFactory.authenticate(req, (String) ololoKey, "1");
            if (authenticated) {
                AuthenticationUtil.login(req, res, (String) ololoKey, false, true);
            }
            chain.doFilter(sreq, sresp);
            return;
        }

        chain.doFilter(req, res);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        /* do nothing */
    }

    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    public void setConnectorService(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
