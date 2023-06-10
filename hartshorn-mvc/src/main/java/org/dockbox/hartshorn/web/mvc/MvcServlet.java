/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.web.HttpAction;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.mvc.model.ViewModelImpl;
import org.dockbox.hartshorn.web.mvc.template.ViewTemplate;
import org.dockbox.hartshorn.web.servlet.WebServlet;

import java.io.IOException;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MvcServlet implements WebServlet {

    private final MethodView<?, ViewTemplate> method;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private MVCInitializer initializer;

    @Inject
    private ViewContextAdapter adapter;

    @Inject
    @Named("mvc_webserver")
    private ParameterLoader<MvcParameterLoaderContext> parameterLoader;

    public MvcServlet(final MethodView<?, ViewTemplate> method) {
        this.method = method;
    }

    @Override
    public void get(final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallback) throws ApplicationException {
        final ParameterLoader<MvcParameterLoaderContext> loader = this.parameterLoader;
        final ViewModelImpl viewModel = new ViewModelImpl();
        final MvcParameterLoaderContext loaderContext = new MvcParameterLoaderContext(this.method,
                null, this.applicationContext, req, res, viewModel);
        final List<Object> arguments = loader.loadArguments(loaderContext);

        final Object instance = this.applicationContext.get(this.method.declaredBy().type());
        final Attempt<ViewTemplate, Throwable> result = this.method.invoke(TypeUtils.adjustWildcards(instance, Object.class), arguments);

        if (result.present()) {
            final ViewTemplate template = result.get();
            final String content = this.initializer.transform(template, viewModel);
            if (content != null) {
                try {
                    res.setContentType("text/html");
                    res.setStatus(HttpStatus.OK.value());
                    res.getWriter().write(content);
                    return;
                } catch (final IOException e) {
                    res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    throw new ApplicationException(e);
                }
            }
        }

        if (result.errorPresent()) {
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
