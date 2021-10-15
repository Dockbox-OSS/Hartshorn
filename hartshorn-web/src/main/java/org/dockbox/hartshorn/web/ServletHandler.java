/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.ParameterContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.web.processing.RequestArgumentProcessor;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Binds(ServletHandler.class)
public class ServletHandler {

    private final WebStarter starter;
    private final HttpMethod httpMethod;
    private final MethodContext<?, ?> methodContext;

    @Inject private ApplicationContext context;
    @Inject private ObjectMapper mapper;

    @Bound
    public ServletHandler(WebStarter starter, HttpMethod httpMethod, MethodContext<?, ?> methodContext) {
        this.starter = starter;
        this.httpMethod = httpMethod;
        this.methodContext = methodContext;
    }

    public void processRequest(final HttpMethod method, final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallbackAction) throws ServletException, IOException {
        if (method == this.httpMethod) {
            res.addHeader("Hartshorn-Version", Hartshorn.VERSION);

            final Exceptional<?> result = this.methodContext.invoke(this.context, this.parameters(req, res));
            if (result.present()) {
                try {
                    res.setStatus(HttpStatus.OK_200);
                    res.setContentType("application/json");
                    if (String.class.equals(result.type())) {
                        res.getWriter().print(result.get());
                    } else {
                        Exceptional<String> write = this.mapper.write(result.get());
                        if (write.present()) res.getWriter().print(write.get());
                        else res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    }
                    return;
                }
                catch (final IOException e) {
                    Except.handle(e);
                }
            }
        }
        fallbackAction.fallback(req, res);
    }

    protected List<Object> parameters(final HttpServletRequest req, final HttpServletResponse res) {
        final List<Object> parameters = HartshornUtils.emptyList();
        final Set<RequestArgumentProcessor<?>> processors = this.starter.processors();

        parameter_loop:
        for (final ParameterContext<?> parameter : this.methodContext.parameters()) {
            if (TypeContext.of(req).childOf(parameter.type())) parameters.add(req);
            else if (TypeContext.of(res).childOf(parameter.type())) parameters.add(res);
            else {
                for (RequestArgumentProcessor<?> processor : processors) {
                    if (parameter.annotation(processor.annotation()).present() && processor.preconditions(this.context, parameter, req, res)) {
                        Exceptional<?> output = processor.process(this.context, parameter, req, res);
                        if (output.present()) {
                            parameters.add(output.get());
                            continue parameter_loop;
                        }
                    }
                }
                parameters.add(this.context.get(parameter.type()));
            }
        }

        return parameters;
    }
}
