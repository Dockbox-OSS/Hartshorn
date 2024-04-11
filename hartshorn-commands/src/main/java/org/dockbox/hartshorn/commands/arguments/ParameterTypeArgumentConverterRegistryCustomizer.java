/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.commands.arguments;

import java.util.Collection;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistry;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistryCustomizer;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Registers all {@link Parameter} annotated types as argument converters. This allows for dynamic
 * argument conversion based on complex patterns.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ParameterTypeArgumentConverterRegistryCustomizer implements ArgumentConverterRegistryCustomizer {

    private final ApplicationEnvironment environment;

    public ParameterTypeArgumentConverterRegistryCustomizer(ApplicationEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void configure(ArgumentConverterRegistry registry) {
        Collection<TypeView<?>> parameterDeclarations = this.environment.typeResolver().types(Parameter.class);
        for(TypeView<?> parameterDeclaration : parameterDeclarations) {
            Parameter meta = parameterDeclaration.annotations().get(Parameter.class).get();
            CustomParameterPattern pattern = this.environment.applicationContext().get(meta.pattern());
            String parameterKey = meta.value();
            ArgumentConverter<?> dynamicPatternConverter = new DynamicPatternConverter<>(parameterDeclaration.type(), pattern, parameterKey);
            registry.registerConverter(dynamicPatternConverter);
        }
    }
}
