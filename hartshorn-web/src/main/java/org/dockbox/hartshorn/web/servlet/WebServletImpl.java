/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.HttpAction;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.RequestHandlerContext;
import org.dockbox.hartshorn.web.ServletFactory;
import org.dockbox.hartshorn.web.ServletHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;

@ComponentBinding(WebServletImpl.class)
public class WebServletImpl implements WebServlet {

    @Getter
    private final ServletHandler handler;

    @Bound
    public WebServletImpl(final HttpWebServer starter, final RequestHandlerContext context) {
        this.handler = context.applicationContext().get(ServletFactory.class)
                .servletHandler(starter, context.httpRequest().method(), context.methodContext());
    }

    @Override
    public void get(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.GET, req, res, fallback);
    }

    @Override
    public void head(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.HEAD, req, res, fallback);
    }

    @Override
    public void post(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.POST, req, res, fallback);
    }

    @Override
    public void put(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.PUT, req, res, fallback);
    }

    @Override
    public void delete(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.DELETE, req, res, fallback);
    }

    @Override
    public void options(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.OPTIONS, req, res, fallback);
    }

    @Override
    public void trace(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        this.handler.processRequest(HttpMethod.TRACE, req, res, fallback);
    }
}
