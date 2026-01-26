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

package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.ContextUtilities;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.convert.AdditionalTargetTypeContext;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;

/**
 * Converts a {@link String} to a {@link Boolean}.
 *
 * @author Guus Lieben
 * @see Boolean#parseBoolean(String)
 * @since 0.5.0
 */
public class StringToTypeViewConverterFactory implements ConverterFactory<String, TypeView<?>> {

    private final Introspector introspector;

    public StringToTypeViewConverterFactory(Introspector introspector) {
        this.introspector = introspector;
    }

    @Override
    public <O extends TypeView<?>> Converter<String, O> create(Class<O> targetType) {
        return (input, contexts) -> {
            AdditionalTargetTypeContext<O> additionalTypeContext = ContextUtilities.first(
                    contexts,
                    AdditionalTargetTypeContext.class
            ).orElseGet(() -> {
                return new AdditionalTargetTypeContext<>(targetType, null);
            });

            if (input != null) {
                TypeView<?> type = this.introspector.introspect(input);
                if (!type.isVoid()) {
                    ParameterizableType parameterizableType = additionalTypeContext.parameterizableType();
                    if (!isValidBound(parameterizableType, type)) {
                        throw new IllegalArgumentException(
                                ("Cannot convert '%s' to type %s: " +
                                        "Target type %s is not within bound %s"
                                ).formatted(
                                        input,
                                        targetType.getName(),
                                        parameterizableType.toQualifiedString(),
                                        type.qualifiedName()
                                ));
                    }
                    return targetType.cast(type);
                }
            }
            return null;
        };
    }

    private static boolean isValidBound(
            ParameterizableType parameterizableType,
            TypeView<?> type
    ) {
        if (parameterizableType != null) {
            List<ParameterizableType> parameters = parameterizableType
                    .parameters();
            if (!parameters.isEmpty()) {
                ParameterizableType valueBound = parameters.getFirst();
                return type.isChildOf(valueBound.type());
            }
        }
        return true;
    }
}
