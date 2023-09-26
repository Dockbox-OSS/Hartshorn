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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class ContextParameterLoaderRule implements ParameterLoaderRule<ApplicationBoundParameterLoaderContext> {

    @Override
    public boolean accepts(final ParameterView<?> parameter, final int index, final ApplicationBoundParameterLoaderContext context, final Object... args) {
        return parameter.annotations().has(Context.class) && parameter.type().isChildOf(org.dockbox.hartshorn.context.Context.class);
    }

    @Override
    public <T> Option<T> load(final ParameterView<T> parameter, final int index, final ApplicationBoundParameterLoaderContext context, final Object... args) {
        final ApplicationContext applicationContext = context.applicationContext();
        final String name = parameter.annotations().get(Context.class).map(Context::value).orNull();

        final TypeView<? extends org.dockbox.hartshorn.context.Context> type = TypeUtils.adjustWildcards(parameter.type(), TypeView.class);
        ContextKey<? extends org.dockbox.hartshorn.context.Context> key = ContextKey.of(type.type());
        if (StringUtilities.notEmpty(name)) {
            key = key.mutable().name(name).build();
        }

        final Option<? extends org.dockbox.hartshorn.context.Context> out = applicationContext.first(key);

        final boolean required = Boolean.TRUE.equals(parameter.annotations().get(Required.class)
                .map(Required::value)
                .orElse(false));
        if (required && out.absent()) throw new ComponentRequiredException("Parameter " + parameter.name() + " on " + parameter.declaredBy().qualifiedName() + " is required");

        return out.cast(parameter.type().type());
    }
}
