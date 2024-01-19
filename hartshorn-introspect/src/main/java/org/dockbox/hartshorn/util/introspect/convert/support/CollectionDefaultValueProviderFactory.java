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
import org.dockbox.hartshorn.util.introspect.convert.support.collections.CollectionProvider;
import org.dockbox.hartshorn.util.introspect.convert.support.collections.SimpleCollectionFactory;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A {@link DefaultValueProviderFactory} for {@link Collection}s, backed by a {@link SimpleCollectionFactory}. If
 * the requested {@link Collection} type is parameterized, this provider will attempt to retrieve its type parameter
 * for the element type. If the type parameter is not available, {@link Object} will be used as the element type.
 *
 * @see SimpleCollectionFactory
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CollectionDefaultValueProviderFactory implements DefaultValueProviderFactory<Collection<?>> {

    private final Introspector introspector;
    private final SimpleCollectionFactory helperFactory;

    public CollectionDefaultValueProviderFactory(Introspector introspector) {
        this.introspector = introspector;
        this.helperFactory = new SimpleCollectionFactory(introspector);
    }

    /**
     * Creates a new {@link DefaultValueProvider} for the given {@link Collection} type. The given {@code elementType}
     * will be used as the element type of the {@link Collection} if the collection requires it, in other cases this
     * is only used for convenient type inference.
     *
     * @param targetType the {@link Collection} type
     * @param elementType the element type
     * @return a new {@link DefaultValueProvider}
     * @param <E> the element type
     * @param <O> the {@link Collection} type
     */
    public <E, O extends Collection<E>> DefaultValueProvider<O> create(Class<O> targetType, Class<E> elementType) {
        return () -> {
            try {
                return this.helperFactory.createCollection(targetType, elementType, 0);
            }
            catch(IllegalArgumentException e) {
                return null;
            }
        };
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

    /**
     * Configures sensible defaults for the {@link SimpleCollectionFactory}.
     *
     * @return the current instance
     *
     * @see SimpleCollectionFactory#withDefaults()
     */
    public CollectionDefaultValueProviderFactory withDefaults() {
        this.helperFactory.withDefaults();
        return this;
    }

    /**
     * Registers a default {@link Collection} implementation for the given {@link Collection} type. The given
     * {@code provider} will be used to create a new instance of the {@link Collection} type.
     *
     * @param type the {@link Collection} type
     * @param provider the provider that creates a new instance of the {@link Collection} type
     * @return the current instance
     * @param <T> the {@link Collection} type
     *
     * @see SimpleCollectionFactory#withDefault(Class, Supplier)
     */
    public <T extends Collection<?>> CollectionDefaultValueProviderFactory withDefault(Class<T> type, CollectionProvider<T> provider) {
        this.helperFactory.withDefault(type, provider);
        return this;
    }
}
