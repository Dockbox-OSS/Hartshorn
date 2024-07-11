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

package org.dockbox.hartshorn.inject.provider;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link Provider} of which the result can be mapped using a list of {@link Function}s. This
 * can be useful to apply a set of transformations to a {@link ObjectContainer}, without having
 * to create a new provider.
 *
 * @param <T> The type instance to provide.
 *
 * @see Provider
 * @see ObjectContainer
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class ComposedProvider<T> implements Provider<T> {

    private final List<Function<ObjectContainer<T>, ObjectContainer<T>>> functions = new LinkedList<>();
    private final Provider<T> provider;

    @SafeVarargs
    public ComposedProvider(Provider<T> provider, Function<ObjectContainer<T>, ObjectContainer<T>>... functions) {
        this.provider = provider;
        this.functions.addAll(List.of(functions));
    }

    /**
     * Returns the provider that is used as the source of the instance. This provider is not
     * modified by the functions that are applied to the result of the provider.
     *
     * @return The provider that is used as the source of the instance.
     */
    public Provider<T> provider() {
        return this.provider;
    }

    /**
     * Returns the list of functions that are applied to the result of the provider. The list
     * is immutable, and will be in the order in which the functions are applied.
     *
     * @return The list of functions that are applied to the result of the provider.
     */
    public List<Function<ObjectContainer<T>, ObjectContainer<T>>> functions() {
        return List.copyOf(this.functions);
    }

    @Override
    public Option<ObjectContainer<T>> provide(ComponentRequestContext requestContext) throws ApplicationException {
        return this.provider.provide(requestContext)
                .map(this::transformContainer);
    }

    private ObjectContainer<T> transformContainer(ObjectContainer<T> container) {
        for (Function<ObjectContainer<T>, ObjectContainer<T>> function : this.functions) {
            container = function.apply(TypeUtils.unchecked(container, ObjectContainer.class));
        }
        return container;
    }

    @Override
    public Provider<T> map(Function<ObjectContainer<T>, ObjectContainer<T>> mappingFunction) {
        this.functions.add(mappingFunction);
        return this;
    }

    @Override
    public LifecycleType defaultLifecycle() {
        return this.provider.defaultLifecycle();
    }

    @Override
    public Tristate defaultLazy() {
        return this.provider.defaultLazy();
    }
}
