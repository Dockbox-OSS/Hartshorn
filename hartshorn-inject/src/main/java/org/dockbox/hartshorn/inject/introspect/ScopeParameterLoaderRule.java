/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.inject.introspect;

import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link ParameterLoaderRule} that loads the current {@link Scope} from the
 * {@link ApplicationBoundParameterLoaderContext} if a parameter matches the type of the current
 * scope (or a parent type thereof).
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ScopeParameterLoaderRule
    implements ParameterLoaderRule<ApplicationBoundParameterLoaderContext> {

    @Override
    public boolean accepts(
        ParameterView<?> parameter,
        int index,
        ApplicationBoundParameterLoaderContext context,
        Object... args
    ) {
        TypeView<?> genericScopeType =
            context.application().environment().introspector().introspect(context.scope()
                .installableScopeType()
                .scopeType()
            );
        return parameter.type().is(Scope.class)
            || parameter.type().isParentOf(genericScopeType.type())
            || parameter.type().equals(genericScopeType);
    }

    @Override
    public <T> Option<T> load(
        ParameterView<T> parameter,
        int index,
        ApplicationBoundParameterLoaderContext context,
        Object... args
    ) {
        return Option.of(context.scope())
            .orCompute(() -> context.application().defaultProvider().scope())
            .cast(parameter.type().type());
    }
}
