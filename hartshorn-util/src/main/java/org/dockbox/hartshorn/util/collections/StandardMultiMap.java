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

package org.dockbox.hartshorn.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class StandardMultiMap<K, V> extends AbstractMultiMap<K, V> {

    protected Map<K, Collection<V>> map;

    protected StandardMultiMap() {
    }

    protected StandardMultiMap(final MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected Map<K, Collection<V>> map() {
        if (this.map == null) {
            this.map = this.createEmptyMap();
        }
        return this.map;
    }

    protected abstract Map<K, Collection<V>> createEmptyMap();

    public abstract static class ConcurrentMultiMap<K, V> extends StandardMultiMap<K, V> {

        protected ConcurrentMultiMap() {
        }

        protected ConcurrentMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Map<K, Collection<V>> createEmptyMap() {
            return new ConcurrentHashMap<>();
        }
    }

    public static class CopyOnWriteArrayListMultiMap<K, V> extends ConcurrentMultiMap<K, V> {

        public CopyOnWriteArrayListMultiMap() {
        }

        public CopyOnWriteArrayListMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Collection<V> createEmptyCollection() {
            return new CopyOnWriteArrayList<>();
        }
    }

    public static class ConcurrentSetMultiMap<K, V> extends ConcurrentMultiMap<K, V> {

        public ConcurrentSetMultiMap() {
        }

        public ConcurrentSetMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Collection<V> createEmptyCollection() {
            return ConcurrentHashMap.newKeySet();
        }
    }

    public abstract static class HashMultiMap<K, V> extends StandardMultiMap<K, V> {

        protected HashMultiMap() {
        }

        protected HashMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Map<K, Collection<V>> createEmptyMap() {
            return new HashMap<>();
        }
    }

    public static class ArrayListMultiMap<K, V> extends HashMultiMap<K, V> {

        public ArrayListMultiMap() {
        }

        public ArrayListMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Collection<V> createEmptyCollection() {
            return new ArrayList<>();
        }
    }

    public static class HashSetMultiMap<K, V> extends HashMultiMap<K, V> {

        public HashSetMultiMap() {
        }

        public HashSetMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Collection<V> createEmptyCollection() {
            return new HashSet<>();
        }
    }

    public abstract static class TreeMultiMap<K extends Comparable<K>, V> extends StandardMultiMap<K, V> {

        protected TreeMultiMap() {
        }

        protected TreeMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Map<K, Collection<V>> createEmptyMap() {
            return new TreeMap<>();
        }
    }

    public static class ConcurrentSetTreeMultiMap<K extends Comparable<K>, V> extends TreeMultiMap<K, V> {

        public ConcurrentSetTreeMultiMap() {
        }

        public ConcurrentSetTreeMultiMap(final MultiMap<K, V> map) {
            super(map);
        }

        @Override
        protected Collection<V> createEmptyCollection() {
            return ConcurrentHashMap.newKeySet();
        }
    }
}
