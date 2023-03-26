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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.CopyOnWriteArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedMultiMap.SynchronizedArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedMultiMap.SynchronizedHashSetMultiMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Stream;

public class MultimapTests {

    public static Stream<Arguments> multiMapImplementations() {
        return Stream.of(
                Arguments.of(new ArrayListMultiMap<>()),
                Arguments.of(new HashSetMultiMap<>()),
                Arguments.of(new CopyOnWriteArrayListMultiMap<>()),
                Arguments.of(new ConcurrentSetMultiMap<>()),
                Arguments.of(new ConcurrentSetTreeMultiMap<>()),
                Arguments.of(new SynchronizedArrayListMultiMap<>()),
                Arguments.of(new SynchronizedHashSetMultiMap<>())
        );
    }
    
    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testAllValuesIsNonNull(final MultiMap<String, String> map) {
        Assertions.assertNotNull(map.allValues());
        Assertions.assertTrue(map.allValues().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testGetReturnsEmptyCollectionIfAbsent(final MultiMap<String, String> map) {
        final Collection<String> collection = map.get("test");
        Assertions.assertNotNull(collection);
        Assertions.assertTrue(collection.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testPutCreatesCollection(final MultiMap<String, String> map) {
        map.put("test", "test");

        final Collection<String> collection = map.get("test");
        Assertions.assertNotNull(collection);
        Assertions.assertFalse(collection.isEmpty());
        Assertions.assertEquals(1, collection.size());

        final String value = collection.iterator().next();
        Assertions.assertEquals("test", value);
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testPutMultipleAddsToCollection(final MultiMap<String, String> map) {
        map.put("test", "test");
        map.put("test", "test2");

        final Collection<String> collection = map.get("test");
        Assertions.assertNotNull(collection);
        Assertions.assertFalse(collection.isEmpty());
        Assertions.assertEquals(2, collection.size());

        Assertions.assertTrue(collection.contains("test"));
        Assertions.assertTrue(collection.contains("test2"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testPutIfAbsentDoesNotAddDuplicate(final MultiMap<String, String> map) {
        map.put("test", "test");
        map.putIfAbsent("test", "test");

        final Collection<String> collection = map.get("test");
        Assertions.assertNotNull(collection);
        Assertions.assertFalse(collection.isEmpty());
        Assertions.assertEquals(1, collection.size());

        final String value = collection.iterator().next();
        Assertions.assertEquals("test", value);
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testPutIfAbsentAddsIfAbsent(final MultiMap<String, String> map) {
        map.put("test", "test");
        map.putIfAbsent("test", "test2");

        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals(2, map.allValues().size());

        final Collection<String> collection = map.get("test");
        Assertions.assertNotNull(collection);
        Assertions.assertFalse(collection.isEmpty());
        Assertions.assertEquals(2, collection.size());

        Assertions.assertTrue(collection.contains("test"));
        Assertions.assertTrue(collection.contains("test2"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testKeySetIsNonNull(final MultiMap<String, String> map) {
        Assertions.assertNotNull(map.keySet());
        Assertions.assertTrue(map.keySet().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testKeySetContainsKey(final MultiMap<String, String> map) {
        map.put("test", "test");
        Assertions.assertTrue(map.keySet().contains("test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testKeySetDoesNotContainKeyIfEmpty(final MultiMap<String, String> map) {
        Assertions.assertFalse(map.keySet().contains("test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testContainsKeyReturnsTrueIfKeyExists(final MultiMap<String, String> map) {
        map.put("test", "test");
        Assertions.assertTrue(map.containsKey("test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testContainsKeyReturnsFalseIfKeyDoesNotExist(final MultiMap<String, String> map) {
        Assertions.assertFalse(map.containsKey("test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testContainsValueReturnsTrueIfValueExists(final MultiMap<String, String> map) {
        map.put("test", "test");
        Assertions.assertTrue(map.containsValue("test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testContainsValueReturnsFalseIfValueDoesNotExist(final MultiMap<String, String> map) {
        Assertions.assertFalse(map.containsValue("test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testContainsEntryReturnsTrueIfEntryExists(final MultiMap<String, String> map) {
        map.put("test", "test");
        Assertions.assertTrue(map.containsEntry("test", "test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testContainsEntryReturnsFalseIfEntryDoesNotExist(final MultiMap<String, String> map) {
        Assertions.assertFalse(map.containsEntry("test", "test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testRemoveRemovesAllEntries(final MultiMap<String, String> map) {
        map.put("test", "test");
        Assertions.assertTrue(map.containsEntry("test", "test"));
        
        map.remove("test", "test");
        Assertions.assertFalse(map.containsEntry("test", "test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testRemoveKeyRemovesKey(final MultiMap<String, String> map) {
        map.put("test", "test");
        Assertions.assertTrue(map.containsKey("test"));
        
        map.remove("test");
        Assertions.assertFalse(map.containsKey("test"));
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void testReplaceReplacesMatchingEntries(final MultiMap<String, String> map) {
        map.put("test", "test");
        Assertions.assertTrue(map.containsEntry("test", "test"));
        
        map.replace("test", "test", "test2");
        Assertions.assertFalse(map.containsEntry("test", "test"));
        Assertions.assertTrue(map.containsEntry("test", "test2"));
    }
}
