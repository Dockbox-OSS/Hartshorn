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
import org.dockbox.hartshorn.util.introspect.convert.AdditionalTargetTypeContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.rest.Header;

/**
 * A {@link ParameterLoaderRule} that loads parameter values from HTTP headers using the
 * {@link Header} annotation.
 *
 * @param <C> the type of the parameter loader context
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class HeaderValueParameterLoaderRule<C extends ParameterLoaderContext>
        implements ParameterLoaderRule<C> {

    private final HttpServletRequest request;
    private final ConversionService conversionService;

    public HeaderValueParameterLoaderRule(HttpServletRequest request, ConversionService conversionService) {
        this.request = request;
        this.conversionService = conversionService;
    }

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, C context, Object... args) {
        return parameter.annotations().has(Header.class);
    }

    @Override
    public <T> Option<T> load(ParameterView<T> parameter, int index, C context, Object... args) {
        Header header = parameter.annotations().get(Header.class).orElseThrow(() -> {
            return new IllegalStateException("Parameter is not annotated with @Header");
        });
        String value = this.request.getHeader(header.value());
        T result = this.conversionService.convert(
                value,
                parameter.type().type(),
                new AdditionalTargetTypeContext<>(parameter.type())
        );
        return Option.of(result);
    }
}
