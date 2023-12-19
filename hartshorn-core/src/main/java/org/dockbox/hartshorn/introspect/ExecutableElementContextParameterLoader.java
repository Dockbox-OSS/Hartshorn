/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.introspect;

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentKey.Builder;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.inject.ComponentKeyCustomizerContext;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.RuleBasedParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;

import jakarta.inject.Named;

public class ExecutableElementContextParameterLoader extends RuleBasedParameterLoader<ApplicationBoundParameterLoaderContext> {

    public ExecutableElementContextParameterLoader(ApplicationContext applicationContext) {
        super(ApplicationBoundParameterLoaderContext.class);
        this.add(new ContextParameterLoaderRule(applicationContext));
    }

    @Override
    protected <T> T loadDefault(ParameterView<T> parameter, int index, ApplicationBoundParameterLoaderContext context, Object... args) {
        Named named = parameter.annotations().get(Named.class).orNull();
        ComponentKey<T> key = ComponentKey.builder(parameter.genericType())
            .name(named)
            .build();

        boolean enable = requiresEnabling(parameter);
        Builder<T> keyBuilder = key.mutable().enable(enable);

        this.configureKeyBuilder(parameter, context, keyBuilder);

        ComponentKey<T> componentKey = keyBuilder.build();
        T out = context.provider().get(componentKey);

        boolean required = isRequired(parameter);

        if (required && out == null) {
            throw new ComponentRequiredException("Parameter " + parameter.name() + " on " + parameter.declaredBy().qualifiedName() + " is required");
        }

        return out;
    }

    private <T> void configureKeyBuilder(ParameterView<T> parameter, Context context, Builder<T> keyBuilder) {
        List<ComponentKeyCustomizerContext> customizerContexts = context.all(ComponentKeyCustomizerContext.class);
        for(ComponentKeyCustomizerContext customizerContext : customizerContexts) {
            customizerContext.customizer().configure(parameter, keyBuilder);
        }
    }

    private static boolean requiresEnabling(AnnotatedElementView parameter) {
        return Boolean.TRUE.equals(parameter.annotations().get(Enable.class)
                .map(Enable::value)
                .orElse(true));
    }

    private static boolean isRequired(AnnotatedElementView parameter) {
        return Boolean.TRUE.equals(parameter.annotations().get(Required.class)
                .map(Required::value)
                .orElse(false));
    }
}
