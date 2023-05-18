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

package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
import java.util.function.Supplier;

public class CollectionFactoryTests {

    @Test
    public void testCreateCollectionWithArrayList() {
        final List<Integer> list = createDefaultFactory().createCollection(List.class, Integer.class, 10);
        Assertions.assertTrue(list instanceof ArrayList);
        Assertions.assertEquals(list.size(), 0);
    }

    @Test
    public void testCreateCollectionWithHashSet() {
        final Set<String> set = createDefaultFactory().createCollection(Set.class, String.class, 5);
        Assertions.assertTrue(set instanceof HashSet);
        Assertions.assertEquals(set.size(), 0);
    }

    @Test
    public void testCreateCollectionWithTreeSet() {
        final SortedSet<Double> sortedSet = createDefaultFactory().createCollection(SortedSet.class, Double.class, 3);
        Assertions.assertTrue(sortedSet instanceof TreeSet);
        Assertions.assertEquals(sortedSet.size(), 0);
    }

    @Test
    public void testCreateCollectionWithLinkedList() {
        final Queue<Boolean> queue = createDefaultFactory().createCollection(Queue.class, Boolean.class, 7);
        Assertions.assertTrue(queue instanceof LinkedList);
        Assertions.assertEquals(queue.size(), 0);
    }

    @Test
    public void testCreateCollectionWithEnumSet() {
        final CollectionFactory factory = this.createFactory(EnumSet.class, () -> null);
        final EnumSet<Color> enumSet = factory.createCollection(EnumSet.class, Color.class, 2);
        Assertions.assertTrue(enumSet instanceof EnumSet);
        Assertions.assertEquals(enumSet.size(), 0);
    }

    @Test
    public void testCreateCollectionWithUnsupportedInterface() {
        final CollectionFactory factory = this.createFactory(BeanContext.class, () -> null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.createCollection(BeanContext.class, Integer.class, 0));
    }

    @Test
    public void testCreateCollectionWithConcreteImplementation() {
        final CollectionFactory factory = this.createFactory(LinkedList.class, LinkedList::new);
        final List<Integer> list = factory.createCollection(LinkedList.class, Integer.class, 0);
        Assertions.assertTrue(list instanceof LinkedList);
        Assertions.assertEquals(list.size(), 0);
    }
    
    private <T extends Collection<?>> CollectionFactory createFactory(final Class<T> targetType, final Supplier<T> constructor) {
        final Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(targetType, constructor);
        return new CollectionFactory(introspector);
    }

    private CollectionFactory createDefaultFactory() {
        return new CollectionFactory(null).withDefaults();
    }

    enum Color {
        RED, BLUE, GREEN
    }
}