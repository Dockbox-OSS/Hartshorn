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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.core.Enableable;
import org.dockbox.hartshorn.core.annotations.component.Component;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;

@Component
@Binds(ServletHandler.class)
public class ServletHandler implements Enableable {

    private final HttpWebServer starter;
    private final HttpMethod httpMethod;
    private final MethodContext<?, ?> methodContext;
    private final HttpRequest httpRequest;

    @Inject
    private ApplicationContext context;

    @Inject
    @Getter
    private ObjectMapper mapper;

    @Value(value = "hartshorn.web.headers.hartshorn", or = "true")
    private boolean addHeader;

    @Bound
    public ServletHandler(final HttpWebServer starter, final HttpMethod httpMethod, final MethodContext<?, ?> methodContext) {
        this.starter = starter;
        this.httpMethod = httpMethod;
        this.methodContext = methodContext;
        this.httpRequest = methodContext.annotation(HttpRequest.class)
                .orThrow(() -> new IllegalArgumentException("Provided method is not annotated with @HttpRequest or an extension of that annotation (%s)".formatted(methodContext.qualifiedName())));
    }

    @Override
    public void enable() {
        this.mapper.fileType(this.httpRequest.responseFormat());
    }

    public synchronized void processRequest(final HttpMethod method, final HttpServletRequest req, final HttpServletResponse res, final HttpAction fallbackAction) throws ApplicationException {
        synchronized (this) {
            final long start = System.currentTimeMillis();
            final String sessionId = String.valueOf(req.hashCode());
            final String request = "%s %s".formatted(method, req.getRequestURI());

            if (method == this.httpMethod) {
                if (this.addHeader) res.addHeader("Hartshorn-Version", Hartshorn.VERSION);

                final ParameterLoader<HttpRequestParameterLoaderContext> loader = this.starter.loader();
                final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(this.methodContext, this.methodContext.parent(), null, this.context, req, res);
                final List<Object> arguments = loader.loadArguments(loaderContext);

                final Exceptional<?> result = this.methodContext.invoke(this.context, arguments);
                if (result.present()) {
                    this.context.log().debug("Request %s processed for session %s, writing response body".formatted(request, sessionId));
                    try {
                        res.setStatus(HttpStatus.OK.value());
                        if (String.class.equals(result.type())) {
                            res.setContentType("text/plain");
                            this.context.log().debug("Returning plain body for request %s".formatted(request));
                            res.getWriter().print(result.get());
                        }
                        else {
                            res.setContentType("application/" + this.mapper.fileType().extension());
                            this.context.log().debug("Writing body to string for request %s".formatted(request));
                            final Exceptional<String> write = this.mapper.write(result.get());
                            if (write.present()) {
                                this.context.log().debug("Printing response body to response writer");
                                res.getWriter().print(write.get());
                            }
                            else {
                                res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                if (write.caught()) Except.handle("Could not process response for request %s for session %s".formatted(request, sessionId), write.error());
                                else Except.handle("Could not process response for request %s for session %s".formatted(request, sessionId));
                            }
                        }
                        this.context.log().debug("Finished servlet handler for request %s with session %s in %dms".formatted(request, sessionId, System.currentTimeMillis() - start));
                        return;
                    }
                    catch (final IOException e) {
                        throw new ApplicationException(e);
                    }
                }
            }
            fallbackAction.fallback(req, res);
        }
    }
}
