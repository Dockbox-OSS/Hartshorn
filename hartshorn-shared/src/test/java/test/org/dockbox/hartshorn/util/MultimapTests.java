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

package test.org.dockbox.hartshorn.util;

import java.util.Collection;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.collections.CollectionUtilities;

import static org.assertj.core.api.Assertions.assertThat;

import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.collections.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.collections.CopyOnWriteArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedHashSetMultiMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
    void allValuesIsNonNull(MultiMap<String, String> map) {
        assertThat(map.allValues()).isNotNull();
        assertThat(map.allValues()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void getReturnsEmptyCollectionIfAbsent(MultiMap<String, String> map) {
        Collection<String> collection = map.get("test");
        assertThat(collection).isNotNull();
        assertThat(collection).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void putCreatesCollection(MultiMap<String, String> map) {
        map.put("test", "test");

        Collection<String> collection = map.get("test");
        assertThat(collection)
                .isNotEmpty()
                .hasSize(1);

        String value = CollectionUtilities.first(collection);
        assertThat(value).isEqualTo("test");
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void putMultipleAddsToCollection(MultiMap<String, String> map) {
        map.put("test", "test");
        map.put("test", "test2");

        Collection<String> collection = map.get("test");
        assertThat(collection)
                .isNotEmpty()
                .hasSize(2)
                .contains("test")
                .contains("test2");
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void putIfAbsentDoesNotAddDuplicate(MultiMap<String, String> map) {
        map.put("test", "test");
        map.putIfAbsent("test", "test");

        Collection<String> collection = map.get("test");
        assertThat(collection)
                .isNotEmpty()
                .hasSize(1);

        String value = CollectionUtilities.first(collection);
        assertThat(value).isEqualTo("test");
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void putIfAbsentAddsIfAbsent(MultiMap<String, String> map) {
        map.put("test", "test");
        map.putIfAbsent("test", "test2");

        assertThat(map.size()).isEqualTo(2);
        assertThat(map.allValues()).hasSize(2);

        Collection<String> collection = map.get("test");
        assertThat(collection)
                .isNotEmpty()
                .hasSize(2)
                .contains("test")
                .contains("test2");
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void keySetIsNonNull(MultiMap<String, String> map) {
        assertThat(map.keySet()).isNotNull();
        assertThat(map.keySet()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void keySetContainsKey(MultiMap<String, String> map) {
        map.put("test", "test");
        assertThat(map.keySet()).contains("test");
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void keySetDoesNotContainKeyIfEmpty(MultiMap<String, String> map) {
        assertThat(map.keySet()).doesNotContain("test");
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void containsKeyReturnsTrueIfKeyExists(MultiMap<String, String> map) {
        map.put("test", "test");
        assertThat(map.containsKey("test")).isTrue();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void containsKeyReturnsFalseIfKeyDoesNotExist(MultiMap<String, String> map) {
        assertThat(map.containsKey("test")).isFalse();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void containsValueReturnsTrueIfValueExists(MultiMap<String, String> map) {
        map.put("test", "test");
        assertThat(map.containsValue("test")).isTrue();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void containsValueReturnsFalseIfValueDoesNotExist(MultiMap<String, String> map) {
        assertThat(map.containsValue("test")).isFalse();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void containsEntryReturnsTrueIfEntryExists(MultiMap<String, String> map) {
        map.put("test", "test");
        assertThat(map.containsEntry("test", "test")).isTrue();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void containsEntryReturnsFalseIfEntryDoesNotExist(MultiMap<String, String> map) {
        assertThat(map.containsEntry("test", "test")).isFalse();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void removeRemovesAllEntries(MultiMap<String, String> map) {
        map.put("test", "test");
        assertThat(map.containsEntry("test", "test")).isTrue();

        map.remove("test", "test");
        assertThat(map.containsEntry("test", "test")).isFalse();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void removeKeyRemovesKey(MultiMap<String, String> map) {
        map.put("test", "test");
        assertThat(map.containsKey("test")).isTrue();

        map.remove("test");
        assertThat(map.containsKey("test")).isFalse();
    }

    @ParameterizedTest
    @MethodSource("multiMapImplementations")
    void replaceReplacesMatchingEntries(MultiMap<String, String> map) {
        map.put("test", "test");
        assertThat(map.containsEntry("test", "test")).isTrue();

        map.replace("test", "test", "test2");
        assertThat(map.containsEntry("test", "test")).isFalse();
        assertThat(map.containsEntry("test", "test2")).isTrue();
    }
}
