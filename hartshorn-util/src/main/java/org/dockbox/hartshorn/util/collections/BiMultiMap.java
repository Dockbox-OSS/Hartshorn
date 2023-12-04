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

/**
 * A {@link MultiMap} that can be inverted, meaning that the values become the keys and the keys become the values.
 *
 * <p>If a value is present in the map multiple times, all keys that are associated with that value will be present in
 * the inverse map. For example, if the map contains the mapping {@code "a" -> "b"} and {@code "c" -> "b"}, the inverse
 * map will contain the mapping {@code "b" -> ["a", "c"]}.
 *
 * <p>This interface is comparable to {@link BiMap}, but is specifically designed for {@link MultiMap} instances.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface BiMultiMap<K, V> extends MultiMap<K, V> {

    /**
     * Returns the inverse of this {@link BiMultiMap}. The inverse of a {@link BiMultiMap} is another {@link MultiMap}
     * that contains the same entries as the original {@link BiMultiMap}, but with the keys and values swapped.
     *
     * @return the inverse of this {@link BiMultiMap}
     */
    MultiMap<V, K> inverse();

}
