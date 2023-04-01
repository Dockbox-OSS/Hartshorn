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

package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;

public class CollectionDefaultValueProviderFactory implements DefaultValueProviderFactory<Collection<?>> {

    private final Introspector introspector;
    private final Map<Class<?>, Supplier<Collection<?>>> defaults = new HashMap<>();

    public CollectionDefaultValueProviderFactory(final Introspector introspector) {
        this.introspector = introspector;
    }

    @Override
    public <O extends Collection<?>> DefaultValueProvider<O> create(final Class<O> targetType) {
        final TypeView<O> type = this.introspector.introspect(targetType);
        final Option<ConstructorView<O>> defaultConstructor = type.constructors().defaultConstructor();
        if (defaultConstructor.present()) {
            return () -> defaultConstructor.get().create().orNull();
        }
        else {
            final Supplier<Collection<?>> supplier = this.defaults.getOrDefault(targetType, this.defaults.get(Collection.class));
            if (supplier != null) {
                return () -> {
                    final Collection<?> collection = supplier.get();
                    return targetType.cast(collection);
                };
            }
        }
        return () -> null;
    }

    public CollectionDefaultValueProviderFactory withDefaults() {
        this.withDefault(Collection.class, ArrayList::new);
        this.withDefault(List.class, ArrayList::new);
        this.withDefault(Set.class, HashSet::new);
        this.withDefault(Queue.class, LinkedList::new);
        this.withDefault(Deque.class, LinkedList::new);
        return this;
    }

    public <T extends Collection<?>> CollectionDefaultValueProviderFactory withDefault(final Class<T> type, final Supplier<T> supplier) {
        //noinspection unchecked
        this.defaults.put(type, (Supplier<Collection<?>>) supplier);
        return this;
    }
}
