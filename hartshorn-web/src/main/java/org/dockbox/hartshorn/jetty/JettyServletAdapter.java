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

package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.ParameterContext;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.web.HttpAction;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.annotations.RequestBody;
import org.dockbox.hartshorn.web.annotations.RequestHeader;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JettyServletAdapter extends HttpServlet {

    private HttpMethod httpMethod;
    private MethodContext<?, ?> methodContext;
    private ApplicationContext context;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handleIf(HttpMethod.GET, req, res, super::doGet);
    }

    @Override
    protected void doHead(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handleIf(HttpMethod.HEAD, req, res, super::doHead);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handleIf(HttpMethod.POST, req, res, super::doPost);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handleIf(HttpMethod.PUT, req, res, super::doPut);
    }

    @Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handleIf(HttpMethod.DELETE, req, res, super::doDelete);
    }

    @Override
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handleIf(HttpMethod.OPTIONS, req, res, super::doOptions);
    }

    @Override
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        this.handleIf(HttpMethod.TRACE, req, res, super::doTrace);
    }

    protected void handleIf(final HttpMethod method, final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallbackAction) throws ServletException, IOException {
        if (method.equals(this.httpMethod)) {
            res.addHeader("Hartshorn-Version", Hartshorn.VERSION);

            final Exceptional<?> result = this.methodContext.invoke(this.context, this.parameters(req, res));
            if (result.present()) {
                try {
                    res.setStatus(HttpStatus.OK_200);
                    res.getWriter().print(result.get());
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
        final Exceptional<String> body = Exceptional.of(() -> req.getReader().lines().collect(Collectors.joining(System.lineSeparator())));

        for (final ParameterContext<?> parameter : this.methodContext.parameters()) {
            // TODO: Dynamic 
            if (parameter.annotation(RequestBody.class).present()) {
                if (body.absent()) parameters.add(null);
                else if (parameter.type().is(String.class)) parameters.add(body.orNull());
                else {
                    final ObjectMapper objectMapper = this.context.get(ObjectMapper.class);
                    final Exceptional<?> value = objectMapper.read(body.orNull(), parameter.type());
                    parameters.add(value.orNull());
                }
            }
            else if (parameter.annotation(RequestHeader.class).present()) {
                final RequestHeader requestHeader = parameter.annotation(RequestHeader.class).get();
                if (parameter.type().is(String.class)) parameters.add(req.getHeader(requestHeader.value()));
                else if (parameter.type().is(Integer.class) || parameter.type().is(int.class)) parameters.add(req.getIntHeader(requestHeader.value()));
            }
            else {
                parameters.add(null);
            }
        }

        return parameters;
    }
}
