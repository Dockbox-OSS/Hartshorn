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

package org.dockbox.hartshorn.inject.strategy;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.Binds;

/**
 * A {@link BindingDeclarationDependencyResolver} that resolves dependencies based on explicit non-direct
 * dependencies as defined by the {@link Binds#after()} attribute. This resolver indicates that any
 * dependency that is declared in the {@link Binds#after()} attribute should be resolved before the
 * binding that is being resolved.
 *
 * @see Binds#after()
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class BindingAfterDeclarationDependencyResolver implements BindingDeclarationDependencyResolver {

    @Override
    public <T> boolean canHandle(BindingStrategyContext<T> context) {
        return context instanceof MethodAwareBindingStrategyContext<T> methodAwareBindingStrategyContext
                && methodAwareBindingStrategyContext.method().annotations().has(Binds.class);
    }

    @Override
    public Set<ComponentKey<?>> dependencies(BindingStrategyContext<?> context) {
        MethodAwareBindingStrategyContext<?> strategyContext = (MethodAwareBindingStrategyContext<?>) context;
        Binds bindingDecorator = strategyContext.method().annotations()
                .get(Binds.class)
                .orElseThrow(() -> new IllegalStateException("Method is not annotated with @Binds (or a compatible meta-annotation)"));

        Class<?>[] after = bindingDecorator.after();
        return Arrays.stream(after).map(ComponentKey::of).collect(Collectors.toSet());
    }
}
