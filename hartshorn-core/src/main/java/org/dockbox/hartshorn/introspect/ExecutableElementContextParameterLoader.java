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

import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.parameter.RuleBasedParameterLoader;

import jakarta.inject.Named;

public class ExecutableElementContextParameterLoader extends RuleBasedParameterLoader<ParameterLoaderContext> {

    public ExecutableElementContextParameterLoader() {
        this.add(new ContextParameterLoaderRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterView<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        final Named named = parameter.annotations().get(Named.class).orNull();
        final ComponentKey<T> key = ComponentKey.builder(parameter.type().type()).name(named).build();
        final boolean enable = Boolean.TRUE.equals(parameter.annotations().get(Enable.class)
                .map(Enable::value)
                .orElse(true));

        final ComponentKey<T> componentKey = key.mutable().enable(enable).build();
        final T out = context.provider().get(componentKey);

        final boolean required = Boolean.TRUE.equals(parameter.annotations().get(Required.class)
                .map(Required::value)
                .orElse(false));

        if (required && out == null) throw new ComponentRequiredException("Parameter " + parameter.name() + " on " + parameter.declaredBy().qualifiedName() + " is required");

        return out;
    }
}
