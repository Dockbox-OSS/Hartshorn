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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ComposedProvider<T> implements Provider<T> {

    private final List<Function<ObjectContainer<T>, ObjectContainer<T>>> functions = new LinkedList<>();
    private final Provider<T> provider;

    @SafeVarargs
    public ComposedProvider(Provider<T> provider, Function<ObjectContainer<T>, ObjectContainer<T>>... functions) {
        this.provider = provider;
        this.functions.addAll(List.of(functions));
    }

    public Provider<T> provider() {
        return this.provider;
    }

    public List<Function<ObjectContainer<T>, ObjectContainer<T>>> functions() {
        return List.copyOf(this.functions);
    }

    @Override
    public Option<ObjectContainer<T>> provide(ApplicationContext context) throws ApplicationException {
        return this.provider.provide(context)
                .map(this::doMapContainer);
    }

    private ObjectContainer<T> doMapContainer(ObjectContainer<T> container) {
        for (Function<ObjectContainer<T>, ObjectContainer<T>> function : this.functions) {
            container = function.apply(container);
        }
        return container;
    }

    @Override
    public Provider<T> map(Function<ObjectContainer<T>, ObjectContainer<T>> mappingFunction) {
        this.functions.add(mappingFunction);
        return this;
    }
}
