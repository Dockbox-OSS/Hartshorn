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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.data.annotations.Value;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;

import java.io.IOException;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletHandler {

    private final HttpWebServer starter;
    private final HttpMethod httpMethod;
    private final MethodContext<?, ?> methodContext;
    private final HttpRequest httpRequest;

    @Inject
    private ApplicationContext context;

    @Inject
    private ObjectMapper mapper;

    @Value("hartshorn.web.headers.hartshorn")
    private boolean addHeader = true;

    @Bound
    public ServletHandler(final HttpWebServer starter, final HttpMethod httpMethod, final MethodContext<?, ?> methodContext) {
        this.starter = starter;
        this.httpMethod = httpMethod;
        this.methodContext = methodContext;
        this.httpRequest = methodContext.annotation(HttpRequest.class)
                .orThrow(() -> new IllegalArgumentException("Provided method is not annotated with @HttpRequest or an extension of that annotation (%s)".formatted(methodContext.qualifiedName())));
    }

    public ObjectMapper mapper() {
        return this.mapper;
    }

    @PostConstruct
    public void enable() throws ApplicationException {
        final MediaType mediaType = this.httpRequest.produces();
        if (!mediaType.isSerializable()) throw new ApplicationException("Provided media type '" + mediaType.value() + "' is not serializable");
        this.mapper.fileType(mediaType.fileFormat().get());
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

                final Result<?> result = this.methodContext.invoke(this.context, arguments);
                if (result.present()) {
                    this.context.log().debug("Request %s processed for session %s, writing response body".formatted(request, sessionId));
                    try {
                        if (String.class.equals(result.type())) {
                            res.setContentType(MediaType.TEXT_PLAIN.value());
                            this.context.log().debug("Returning plain body for request %s".formatted(request));
                            res.getWriter().print(result.get());
                        }
                        else {
                            res.setContentType(this.httpRequest.produces().value());
                            this.context.log().debug("Writing body to string for request %s".formatted(request));
                            final Result<String> write = this.mapper.write(result.get());
                            if (write.present()) {
                                this.context.log().debug("Printing response body to response writer");
                                res.getWriter().print(write.get());
                            }
                            else {
                                res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                if (write.caught()) this.context.handle("Could not process response for request %s for session %s".formatted(request, sessionId), write.error());
                                else this.context.log().warn("Could not process response for request %s for session %s".formatted(request, sessionId));
                            }
                        }
                        this.context.log().debug("Finished servlet handler for request %s with session %s in %dms".formatted(request, sessionId, System.currentTimeMillis() - start));
                        return;
                    }
                    catch (final IOException e) {
                        throw new ApplicationException(e);
                    }
                }
                else {
                    if (result.caught()) throw new ApplicationException(result.error());
                    else {
                        res.setStatus(HttpStatus.NO_CONTENT.value());
                    }
                    return;
                }
            }

            fallbackAction.fallback(req, res);
        }
    }
}
