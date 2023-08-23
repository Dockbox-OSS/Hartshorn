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
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.function.Supplier;

public class CollectionDefaultValueProviderFactory implements DefaultValueProviderFactory<Collection<?>> {

    private final Introspector introspector;
    private final CollectionFactory helperFactory;

    public CollectionDefaultValueProviderFactory(final Introspector introspector) {
        this.introspector = introspector;
        this.helperFactory = new CollectionFactory(introspector);
    }

    public <E, O extends Collection<E>> DefaultValueProvider<O> create(final Class<O> targetType, final Class<E> elementType) {
        final TypeView<O> type = this.introspector.introspect(targetType);
        final Option<ConstructorView<O>> defaultConstructor = type.constructors().defaultConstructor();
        if (defaultConstructor.present()) {
            return () -> defaultConstructor.get().create().orNull();
        }
        else {
            return () -> {
                try {
                    return this.helperFactory.createCollection(targetType, elementType, 0);
                }
                catch (final IllegalArgumentException e) {
                    return null;
                }
            };
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <O extends Collection<?>> DefaultValueProvider<O> create(final Class<O> targetType) {
        final TypeView<O> type = this.introspector.introspect(targetType);
        final Class<?> componentType = type.typeParameters()
                .resolveInputFor(Collection.class)
                .atIndex(0)
                .flatMap(TypeParameterView::resolvedType)
                .map(TypeView::type)
                .map(Class.class::cast)
                .orElse(Object.class);

        return this.create((Class) targetType, (Class) componentType);
    }

    public CollectionDefaultValueProviderFactory withDefaults() {
        this.helperFactory.withDefaults();
        return this;
    }

    public <T extends Collection<?>> CollectionDefaultValueProviderFactory withDefault(final Class<T> type, final Supplier<T> supplier) {
        this.helperFactory.withDefault(type, supplier);
        return this;
    }
}
