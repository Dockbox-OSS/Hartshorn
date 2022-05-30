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

import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;
import org.dockbox.hartshorn.web.HttpAction;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.mvc.MVCInitializer;
import org.dockbox.hartshorn.web.mvc.ViewModelImpl;
import org.dockbox.hartshorn.web.mvc.ViewTemplate;
import org.dockbox.hartshorn.web.processing.MvcParameterLoaderContext;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

        final Result<ViewTemplate> result = this.methodContext.invoke(this.applicationContext, arguments);

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
