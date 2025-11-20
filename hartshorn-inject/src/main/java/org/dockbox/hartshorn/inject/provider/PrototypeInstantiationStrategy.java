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

package org.dockbox.hartshorn.inject.provider;

import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A strategy for instantiating components with a prototype lifecycle.
 *
 * @param <T> the type of the component being instantiated
 *
 * @author Guus Lieben
 * @since 0.6.0
 */
@FunctionalInterface
public interface PrototypeInstantiationStrategy<T> extends NonTypeAwareInstantiationStrategy<T> {

    @Override
    default Option<ObjectContainer<T>> provide(
        InjectionCapableApplication application,
        ComponentRequestContext requestContext,
        Scope scope
    ) throws ApplicationException {
        return Option.of(ComponentObjectContainer.ofPrototype(this.get(requestContext, scope)));
    }

    /**
     * Instantiates a new component instance within the given context and scope.
     *
     * @param context the component request context
     * @param scope the scope in which the component is being instantiated
     *
     * @return the instantiated component
     *
     * @throws ApplicationException if instantiation fails
     */
    T get(ComponentRequestContext context, Scope scope) throws ApplicationException;

    @Override
    default LifecycleType defaultLifecycle() {
        return LifecycleType.PROTOTYPE;
    }

    @Override
    default Tristate defaultLazy() {
        return Tristate.TRUE;
    }

    /**
     * An empty prototype instantiation strategy that always returns {@code null}.
     *
     * @param <T> the type of the component
     *
     * @return an empty prototype instantiation strategy
     */
    static <T> PrototypeInstantiationStrategy<T> empty() {
        return (context, scope) -> null;
    }
}
