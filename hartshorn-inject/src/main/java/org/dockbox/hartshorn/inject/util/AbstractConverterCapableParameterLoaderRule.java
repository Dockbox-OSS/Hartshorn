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

package org.dockbox.hartshorn.inject.util;

import org.dockbox.hartshorn.util.introspect.convert.AdditionalTargetTypeContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * An abstract {@link ParameterLoaderRule} that supports conversion using the provided
 * {@link ConversionService}, providing additional standard context for the target type.
 *
 * @param <C> the type of the parameter loader context
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractConverterCapableParameterLoaderRule<
        C extends ParameterLoaderContext
        > implements ParameterLoaderRule<C> {

    private final ConversionService conversionService;

    protected AbstractConverterCapableParameterLoaderRule(
            ConversionService conversionService
    ) {
        this.conversionService = conversionService;
    }

    /**
     * Loads the raw parameter value for the given parameter. This value will be converted
     * using the {@link ConversionService} before being returned.
     *
     * @param parameter the parameter to load the value for
     * @param index the index of the parameter
     * @param context the parameter loader context
     * @param args the arguments that are passed to the method that is being invoked
     *
     * @param <T> the type of the parameter
     *
     * @return the raw parameter value
     */
    protected abstract <T> Object loadRawParameterValue(
            ParameterView<T> parameter,
            int index,
            C context,
            Object... args
    );

    @Override
    public final <T> Option<T> load(
        ParameterView<T> parameter,
        int index,
        C context,
        Object... args
    ) {
        Object value = this.loadRawParameterValue(parameter, index, context, args);
        T converted = this.conversionService.convert(
                value,
                parameter.type().type(),
                new AdditionalTargetTypeContext<>(parameter.type())
        );
        return Option.of(converted);
    }
}
