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

import java.util.List;
import org.dockbox.hartshorn.util.ContextUtilities;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.convert.AdditionalTargetTypeContext;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.types.TypeUtils;

/**
 * Converts a {@link String} to a {@link Class} by introspecting the string as a type name.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StringToClassConverterFactory implements ConverterFactory<String, Class<?>> {

    @Override
    public <O extends Class<?>> Converter<String, O> create(Class<O> targetType) {
        return (input, contexts) -> {
            AdditionalTargetTypeContext<O> additionalTypeContext = ContextUtilities.first(
                    contexts,
                    AdditionalTargetTypeContext.class
            ).orElseGet(() -> {
                return new AdditionalTargetTypeContext<>(targetType, null);
            });

            if (input != null) {
                Class<?> type = TypeUtils.forName(input).orNull();
                if (type != null) {
                    ParameterizableType parameterizableType = additionalTypeContext
                        .parameterizableType();

                    if (!isValidBound(parameterizableType, type)) {
                        throw new IllegalArgumentException(
                                ("Cannot convert '%s' to type %s: " +
                                        "Target type %s is not within bound %s"
                                ).formatted(
                                        input,
                                        targetType.getName(),
                                        parameterizableType.toQualifiedString(),
                                        type.getName()
                                ));
                    }
                    @SuppressWarnings("unchecked")
                    O castedType = (O) type;
                    return castedType;
                }

            }
            return null;
        };
    }

    private static boolean isValidBound(
            ParameterizableType parameterizableType,
            Class<?> type
    ) {
        if (parameterizableType != null) {
            List<ParameterizableType> parameters = parameterizableType
                    .parameters();
            if (!parameters.isEmpty()) {
                ParameterizableType valueBound = parameters.getFirst();
                return valueBound.type().isAssignableFrom(type);
            }
        }
        return true;
    }
}
