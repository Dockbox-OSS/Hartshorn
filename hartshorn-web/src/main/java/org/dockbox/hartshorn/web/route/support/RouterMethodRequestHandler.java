/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.route.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.introspect.InjectorExecutableInvocationAdapter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.WebRequestScope;
import org.dockbox.hartshorn.web.route.RequestHandler;
import org.dockbox.hartshorn.web.response.ResponseHandler;
import org.dockbox.hartshorn.web.route.rules.HeaderValueParameterLoaderRule;
import org.dockbox.hartshorn.web.route.rules.PathParameterValueParameterLoaderRule;
import org.dockbox.hartshorn.web.route.rules.QueryParameterValueParameterLoaderRule;

/**
 * A request handler that invokes a method on a router component using dependency injection
 * to resolve method parameters.
 *
 * @param <P> the type of the router component declaring the method
 * @param <R> the return type of the method
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class RouterMethodRequestHandler<P, R> implements RequestHandler, Reportable {

    private final InjectionCapableApplication application;
    private final ConversionService conversionService;
    private final ResponseHandler responseHandler;
    private final MethodView<P, R> methodView;

    public RouterMethodRequestHandler(
            InjectionCapableApplication application,
            ConversionService conversionService,
            ResponseHandler responseHandler,
            MethodView<P, R> methodView
    ) {
        this.application = application;
        this.conversionService = conversionService;
        this.responseHandler = responseHandler;
        this.methodView = methodView;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebRequestScope scope = new WebRequestScope(request, response);
        var adapter = new InjectorExecutableInvocationAdapter(this.application)
                .scope(scope)
                .addParameterLoaderRule(new HeaderValueParameterLoaderRule<>(
                        request,
                        this.conversionService
                ))
                .addParameterLoaderRule(new PathParameterValueParameterLoaderRule<>(
                        request,
                        this.conversionService
                ))
                .addParameterLoaderRule(new QueryParameterValueParameterLoaderRule<>(
                        request,
                        this.conversionService
                ));

        P instance = this.application.defaultProvider().get(this.methodView.declaredBy().type());
        try {
            Option<R> result = adapter.invoke(this.methodView, instance);
            this.responseHandler.handleResponse(request, response, result.orNull());
        }
        catch (Exception e) {
            // Will be handled upstream
            throw e;
        }
        catch (Throwable t) {
            // Will be handled upstream
            throw new Exception(t);
        }
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("method").writeString(this.methodView.qualifiedName());
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("method", this.methodView.qualifiedName())
                .describe();
    }
}
