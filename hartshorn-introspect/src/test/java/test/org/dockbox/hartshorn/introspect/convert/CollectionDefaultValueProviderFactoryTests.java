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

package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionDefaultValueProviderFactory;
import org.junit.jupiter.api.Test;

import java.beans.beancontext.BeanContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionDefaultValueProviderFactoryTests {

    @Test
    void concreteListCanBeProvided() {
        DefaultValueProvider<ArrayList> provider = createProvider(ArrayList.class, ArrayList::new);

        List<?> list = provider.defaultValue();
        assertThat(list)
                .isInstanceOf(ArrayList.class)
                .isEmpty();
    }

    @Test
    void concreteSetCanBeProvided() {
        DefaultValueProvider<HashSet> provider = createProvider(HashSet.class, HashSet::new);

        Set<?> set = provider.defaultValue();
        assertThat(set).isEmpty();
    }

    @Test
    void interfaceListCanBeProvided() {
        DefaultValueProvider<List> provider = createProvider(List.class);

        List<?> list = provider.defaultValue();
        assertThat(list)
                .isInstanceOf(ArrayList.class)
                .isEmpty();
    }

    @Test
    void interfaceCollectionCanBeProvided() {
        DefaultValueProvider<Collection> provider = createProvider(Collection.class);

        Collection<?> collection = provider.defaultValue();
        assertThat(collection)
                .isInstanceOf(ArrayList.class)
                .isEmpty();
    }

    @Test
    void interfaceSetCanBeProvided() {
        DefaultValueProvider<Set> provider = createProvider(Set.class);

        Set<?> set = provider.defaultValue();
        assertThat(set)
                .isInstanceOf(HashSet.class)
                .isEmpty();
    }

    @Test
    void interfaceQueueCanBeProvided() {
        DefaultValueProvider<Queue> provider = createProvider(Queue.class);

        Queue<?> queue = provider.defaultValue();
        assertThat(queue)
                .isInstanceOf(LinkedList.class)
                .isEmpty();
    }

    @Test
    void interfaceDequeCanBeProvided() {
        DefaultValueProvider<Deque> provider = createProvider(Deque.class);

        Deque<?> deque = provider.defaultValue();
        assertThat(deque)
                .isInstanceOf(LinkedList.class)
                .isEmpty();
    }

    @Test
    void unsupportedCollectionTypeCannotBeProvided() {
        DefaultValueProvider<BeanContext> provider = createProvider(BeanContext.class);

        Collection<?> collection = provider.defaultValue();
        assertThat(collection).isNull();
    }

    private static <T extends Collection<?>> DefaultValueProvider<T> createProvider(Class<T> type) {
        return createProvider(type, () -> null);
    }

    private static <T extends Collection<?>> DefaultValueProvider<T> createProvider(
            Class<T> type,
            Supplier<T> supplier
    ) {
        Introspector introspector =
                ConverterIntrospectionHelper.createIntrospectorForCollection(type, supplier);
        DefaultValueProviderFactory<Collection<?>> factory =
                new CollectionDefaultValueProviderFactory(introspector).withDefaults();

        DefaultValueProvider<T> provider = factory.create(type);
        assertThat(provider)
                .isNotNull();

        return provider;
    }
}
