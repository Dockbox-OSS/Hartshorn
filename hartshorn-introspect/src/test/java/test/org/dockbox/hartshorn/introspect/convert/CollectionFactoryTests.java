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
import org.dockbox.hartshorn.util.introspect.convert.support.collections.CollectionFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.collections.SimpleCollectionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class CollectionFactoryTests {

    @Test
    public void testCreateCollectionWithArrayList() {
        List<Integer> list = this.createDefaultFactory().createCollection(List.class, Integer.class);
        Assertions.assertInstanceOf(ArrayList.class, list);
        Assertions.assertEquals(list.size(), 0);
    }

    @Test
    public void testCreateCollectionWithHashSet() {
        Set<String> set = this.createDefaultFactory().createCollection(Set.class, String.class);
        Assertions.assertInstanceOf(HashSet.class, set);
        Assertions.assertEquals(set.size(), 0);
    }

    @Test
    public void testCreateCollectionWithTreeSet() {
        SortedSet<Double> sortedSet = this.createDefaultFactory().createCollection(SortedSet.class, Double.class);
        Assertions.assertInstanceOf(TreeSet.class, sortedSet);
        Assertions.assertEquals(sortedSet.size(), 0);
    }

    @Test
    public void testCreateCollectionWithLinkedList() {
        Queue<Boolean> queue = this.createDefaultFactory().createCollection(Queue.class, Boolean.class);
        Assertions.assertInstanceOf(LinkedList.class, queue);
        Assertions.assertEquals(queue.size(), 0);
    }

    @Test
    public void testCreateCollectionWithEnumSet() {
        CollectionFactory factory = this.createFactory(EnumSet.class, () -> null);
        EnumSet<Color> enumSet = factory.createCollection(EnumSet.class, Color.class, 2);
        Assertions.assertInstanceOf(EnumSet.class, enumSet);
        Assertions.assertEquals(enumSet.size(), 0);
    }

    @Test
    public void testInitialCapacityIsConfigured() {
        int initialCapacity = 23;
        CollectionFactory factory = this.createFactory(Vector.class, Vector::new, Vector::new);
        Vector<String> enumSet = factory.createCollection(Vector.class, String.class, initialCapacity);
        Assertions.assertInstanceOf(Vector.class, enumSet);
        Assertions.assertEquals(enumSet.size(), 0);
        Assertions.assertEquals(enumSet.capacity(), initialCapacity);
    }

    @Test
    public void testCreateCollectionWithUnsupportedInterface() {
        CollectionFactory factory = this.createFactory(BeanContext.class, () -> null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.createCollection(BeanContext.class, Integer.class, 0));
    }

    @Test
    public void testCreateCollectionWithConcreteImplementation() {
        CollectionFactory factory = this.createFactory(LinkedList.class, LinkedList::new);
        List<Integer> list = factory.createCollection(LinkedList.class, Integer.class, 0);
        Assertions.assertInstanceOf(LinkedList.class, list);
        Assertions.assertEquals(list.size(), 0);
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
