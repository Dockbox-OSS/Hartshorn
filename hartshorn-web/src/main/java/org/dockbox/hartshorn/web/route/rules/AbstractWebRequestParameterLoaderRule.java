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

package org.dockbox.hartshorn.web.route.rules;

import jakarta.servlet.http.HttpServletRequest;
import org.dockbox.hartshorn.inject.util.AbstractConverterCapableParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;

/**
 * An abstract {@link ParameterLoaderRule} that loads parameter values from a
 * {@link HttpServletRequest} based on a specific parameter annotation.
 *
 * @param <A> the type of the annotation
 * @param <C> the type of the parameter loader context
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractWebRequestParameterLoaderRule<
        A extends Annotation,
        C extends ParameterLoaderContext
        > extends AbstractConverterCapableParameterLoaderRule<C> {

    private final Class<A> annotationType;
    private final HttpServletRequest request;

    protected AbstractWebRequestParameterLoaderRule(
            Class<A> annotationType,
            HttpServletRequest request,
            ConversionService conversionService
    ) {
        super(conversionService);
        this.annotationType = annotationType;
        this.request = request;
    }

    /**
     * Looks up the value from the request based on the provided annotation.
     *
     * @param request the web request
     * @param annotation the annotation instance
     * @return an option containing the value if found, or empty if not found
     */
    protected abstract Option<String> lookupValue(HttpServletRequest request, A annotation);

    /**
     * Gets the default value from the annotation, which is used if no value is found in the
     * request.
     *
     * @param annotation the annotation instance
     * @return the default value as a string
     */
    protected abstract String getDefaultValue(A annotation);

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, C context, Object... args) {
        return parameter.annotations().has(this.annotationType);
    }

    @Override
    public <T> Object loadRawParameterValue(
        ParameterView<T> parameter,
        int index,
        C context,
        Object... args
    ) {
        A annotation = parameter.annotations().get(this.annotationType).orElseThrow(() -> {
            return new IllegalStateException("Parameter is not annotated with @%s".formatted(
                    this.annotationType.getSimpleName()
            ));
        });
        return this.lookupValue(this.request, annotation)
                .orElseGet(() -> this.getDefaultValue(annotation));
    }
}
