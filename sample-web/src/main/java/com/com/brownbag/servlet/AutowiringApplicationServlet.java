/*
 * BROWN BAG CONFIDENTIAL
 *
 * Brown Bag Consulting LLC
 * Copyright (c) 2011. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.com.brownbag.servlet;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link ApplicationServlet} that autowires and configures the {@link Application}
 * objects it creates using the containing Spring {@link WebApplicationContext}.
 * <p/>
 * <p>
 * When using this servlet, annotations such as
 * <code>@{@link org.springframework.beans.factory.annotation.Autowired Autowired}</code>
 * and <code>@{@link org.springframework.beans.factory.annotation.Required Required}</code>
 * and interfaces such as {@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware},
 * etc. will work on your {@link Application} instances.
 * </p>
 * <p/>
 * <p/>
 * An example:
 * <blockquote><pre>
 *  &lt;bean id="applicationServlet" class="org.springframework.web.servlet.mvc.ServletWrappingController"
 *     p:servletClass="com.com.brownbag.servlet.AutowiringApplicationServlet"&gt;
 *      &lt;otherProperty name="initParameters"&gt;
 *          &lt;props&gt;
 *              &lt;prop key="application"&gt;some.spring.configured.Application&lt;/prop&gt;
 *              &lt;prop key="productionMode"&gt;true&lt;/prop&gt;
 *          &lt;/props&gt;
 *      &lt;/otherProperty&gt;
 *  &lt;/bean&gt;
 * </pre></blockquote>
 *
 * @see org.springframework.web.servlet.mvc.ServletWrappingController
 * @see AutowireCapableBeanFactory
 */
public class AutowiringApplicationServlet extends ApplicationServlet {

    protected final Logger log = Logger.getLogger(getClass());

    private WebApplicationContext webApplicationContext;

    /**
     * Initialize this servlet.
     *
     * @throws ServletException if there is no {@link WebApplicationContext} associated with this servlet's context
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.debug("finding containing WebApplicationContext");
        try {
            this.webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        } catch (IllegalStateException e) {
            throw new ServletException("could not locate containing WebApplicationContext");
        }
    }

    /**
     * Get the containing Spring {@link WebApplicationContext}.
     * This only works after the servlet has been initialized (via {@link #init init()}).
     *
     * @throws ServletException if the operation fails
     */
    protected final WebApplicationContext getWebApplicationContext() throws ServletException {
        if (this.webApplicationContext == null)
            throw new ServletException("can't retrieve WebApplicationContext before init() is invoked");
        return this.webApplicationContext;
    }

    /**
     * Get the {@link AutowireCapableBeanFactory} associated with the containing Spring {@link WebApplicationContext}.
     * This only works after the servlet has been initialized (via {@link #init init()}).
     *
     * @throws ServletException if the operation fails
     */
    protected final AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws ServletException {
        try {
            return getWebApplicationContext().getAutowireCapableBeanFactory();
        } catch (IllegalStateException e) {
            throw new ServletException("containing context " + getWebApplicationContext() + " is not autowire-capable", e);
        }
    }

    /**
     * Create and configure a new instance of the configured application class.
     * <p/>
     * <p>
     * The implementation in {@link AutowiringApplicationServlet} delegates to
     * {@link #getAutowireCapableBeanFactory getAutowireCapableBeanFactory()}, then invokes
     * {@link AutowireCapableBeanFactory#createBean AutowireCapableBeanFactory.createBean()}
     * using the configured {@link Application} class.
     * </p>
     *
     * @param request the triggering {@link HttpServletRequest}
     * @throws ServletException if creation or autowiring fails
     */
    @Override
    protected Application getNewApplication(HttpServletRequest request) throws ServletException {
        Class<? extends Application> cl = null;
        try {
            cl = getApplicationClass();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        log.debug("creating new instance of " + cl);
        AutowireCapableBeanFactory beanFactory = getAutowireCapableBeanFactory();
        try {
            return beanFactory.createBean(cl);
        } catch (BeansException e) {
            throw new ServletException("failed to create new instance of " + cl, e);
        }
    }
}

