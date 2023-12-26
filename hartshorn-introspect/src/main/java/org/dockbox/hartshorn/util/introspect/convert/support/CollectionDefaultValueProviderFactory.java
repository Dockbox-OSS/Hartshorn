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

package org.dockbox.hartshorn.util.introspect.convert.support;

import java.util.Collection;
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.collections.SimpleCollectionFactory;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class CollectionDefaultValueProviderFactory implements DefaultValueProviderFactory<Collection<?>> {

    private final Introspector introspector;
    private final SimpleCollectionFactory helperFactory;

    public CollectionDefaultValueProviderFactory(Introspector introspector) {
        this.introspector = introspector;
        this.helperFactory = new SimpleCollectionFactory(introspector);
    }

    public <E, O extends Collection<E>> DefaultValueProvider<O> create(Class<O> targetType, Class<E> elementType) {
        TypeView<O> type = this.introspector.introspect(targetType);
        Option<ConstructorView<O>> defaultConstructor = type.constructors().defaultConstructor();
        if (defaultConstructor.present()) {
            return () -> {
                try {
                    return defaultConstructor.get().create().orNull();
                }
                catch(Throwable e) {
                    return null;
                }
            };
        }
        else {
            return () -> {
                try {
                    return this.helperFactory.createCollection(targetType, elementType, 0);
                }
                catch (IllegalArgumentException e) {
                    return null;
                }
            };
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <O extends Collection<?>> DefaultValueProvider<O> create(Class<O> targetType) {
        TypeView<O> type = this.introspector.introspect(targetType);
        Class<?> componentType = type.typeParameters()
                .inputFor(Collection.class)
                .atIndex(0)
                .flatMap(TypeParameterView::resolvedType)
                .map(TypeView::type)
                .map(Class.class::cast)
                .orElse(Object.class);

        return this.create((Class) targetType, componentType);
    }

    public CollectionDefaultValueProviderFactory withDefaults() {
        this.helperFactory.withDefaults();
        return this;
    }

    public <T extends Collection<?>> CollectionDefaultValueProviderFactory withDefault(Class<T> type, Supplier<T> supplier) {
        this.helperFactory.withDefault(type, supplier);
        return this;
    }
}
