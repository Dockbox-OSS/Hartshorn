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

package test.org.dockbox.hartshorn.introspect.convert;

import java.beans.beancontext.BeanContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.introspect.Introspector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import org.dockbox.hartshorn.util.introspect.convert.support.collections.CollectionFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.collections.SimpleCollectionFactory;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class CollectionFactoryTests {

    @Test
    void createCollectionWithArrayList() {
        List<Integer> list = this.createDefaultFactory().createCollection(List.class, Integer.class);
        assertThat(list).isInstanceOf(ArrayList.class);
        assertThat(list).isEmpty();
    }

    @Test
    void createCollectionWithHashSet() {
        Set<String> set = this.createDefaultFactory().createCollection(Set.class, String.class);
        assertThat(set).isInstanceOf(HashSet.class);
        assertThat(set).isEmpty();
    }

    @Test
    void createCollectionWithTreeSet() {
        SortedSet<Double> sortedSet = this.createDefaultFactory().createCollection(SortedSet.class, Double.class);
        assertThat(sortedSet).isInstanceOf(TreeSet.class);
        assertThat(sortedSet).isEmpty();
    }

    @Test
    void createCollectionWithLinkedList() {
        Queue<Boolean> queue = this.createDefaultFactory().createCollection(Queue.class, Boolean.class);
        assertThat(queue).isInstanceOf(LinkedList.class);
        assertThat(queue).isEmpty();
    }

    @Test
    void createCollectionWithEnumSet() {
        CollectionFactory factory = this.createFactory(EnumSet.class, () -> null);
        EnumSet<Color> enumSet = factory.createCollection(EnumSet.class, Color.class, 2);
        assertThat(enumSet).isInstanceOf(EnumSet.class);
        assertThat(enumSet).isEmpty();
    }

    @Test
    void initialCapacityIsConfigured() {
        int initialCapacity = 23;
        CollectionFactory factory = this.createFactory(Vector.class, Vector::new, Vector::new);
        Vector<String> enumSet = factory.createCollection(Vector.class, String.class, initialCapacity);
        assertThat(enumSet).isInstanceOf(Vector.class);
        assertThat(enumSet).isEmpty();
        assertThat(initialCapacity).isEqualTo(enumSet.capacity());
    }

    @Test
    void createCollectionWithUnsupportedInterface() {
        CollectionFactory factory = this.createFactory(BeanContext.class, () -> null);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> factory.createCollection(BeanContext.class, Integer.class, 0));
    }

    @Test
    void createCollectionWithConcreteImplementation() {
        CollectionFactory factory = this.createFactory(LinkedList.class, LinkedList::new);
        List<Integer> list = factory.createCollection(LinkedList.class, Integer.class, 0);
        assertThat(list).isInstanceOf(LinkedList.class);
        assertThat(list).isEmpty();
    }

    private <T extends Collection<?>> CollectionFactory createFactory(Class<T> targetType, Supplier<T> constructor) {
        Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(targetType, constructor);
        return new SimpleCollectionFactory(introspector);
    }

    private <T extends Collection<?>> CollectionFactory createFactory(Class<T> targetType, Supplier<T> constructor, IntFunction<T> capacityConstructor) {
        Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(targetType, constructor, capacityConstructor);
        return new SimpleCollectionFactory(introspector);
    }

    private CollectionFactory createDefaultFactory() {
        return new SimpleCollectionFactory(null).withDefaults();
    }

    enum Color {
        RED, BLUE, GREEN
    }
}
