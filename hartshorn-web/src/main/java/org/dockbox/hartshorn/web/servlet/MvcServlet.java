/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.web.HttpAction;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.mvc.MVCInitializer;
import org.dockbox.hartshorn.web.mvc.ViewModelImpl;
import org.dockbox.hartshorn.web.mvc.ViewTemplate;
import org.dockbox.hartshorn.web.processing.MvcParameterLoaderContext;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ComponentBinding(MvcServlet.class)
public class MvcServlet implements WebServlet {

    private final MethodContext<ViewTemplate, ?> methodContext;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private MVCInitializer initializer;

    @Inject
    @Named("mvc_webserver")
    private ParameterLoader<MvcParameterLoaderContext> parameterLoader;

    @Bound
    public MvcServlet(final MethodContext<ViewTemplate, ?> methodContext) {
        this.methodContext = methodContext;
    }

    @Override
    public void get(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        final ParameterLoader<MvcParameterLoaderContext> loader = this.parameterLoader;
        final ViewModelImpl viewModel = new ViewModelImpl();
        final MvcParameterLoaderContext loaderContext = new MvcParameterLoaderContext(this.methodContext, TypeContext.of(ViewTemplate.class),
                null, this.applicationContext, req, res, viewModel);
        final List<Object> arguments = loader.loadArguments(loaderContext);

        final Exceptional<ViewTemplate> result = this.methodContext.invoke(this.applicationContext, arguments);

        if (result.present()) {
            final ViewTemplate template = result.get();
            final String content = this.initializer.transform(template, viewModel);
            if (content != null) {
                try {
                    res.setContentType("text/html");
                    res.setStatus(HttpStatus.OK.value());
                    res.getWriter().write(content);
                    return;
                } catch (final Exception e) {
                    res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    throw new ApplicationException(e);
                }
            }
        }

        if (result.caught()) {
            final Throwable error = result.error();
            if (error instanceof ApplicationException applicationException) throw applicationException;
            else throw new ApplicationException(error);
        }

        fallback.fallback(req, res);
    }

    @Override
    public void head(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        fallback.fallback(req, res);
    }

    @Override
    public void post(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        fallback.fallback(req, res);
    }

    @Override
    public void put(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        fallback.fallback(req, res);
    }

    @Override
    public void delete(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        fallback.fallback(req, res);
    }

    @Override
    public void options(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        fallback.fallback(req, res);
    }

    @Override
    public void trace(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        fallback.fallback(req, res);
    }
}
