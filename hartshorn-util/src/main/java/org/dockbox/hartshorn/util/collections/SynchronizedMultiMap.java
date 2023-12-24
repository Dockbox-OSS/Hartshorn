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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link MultiMap} implementation that synchronizes all access to the backing map. This is
 * useful when the map is accessed concurrently by multiple threads, and it is not possible to
 * synchronize the access externally.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 *
 * @since 0.5.0
 *
 * @see MultiMap
 * @see StandardMultiMap
 *
 * @author Guus Lieben
 */
public abstract class SynchronizedMultiMap<K, V> extends AbstractMultiMap<K, V> {

    private transient Map<K, Collection<V>> map;

    protected SynchronizedMultiMap() {
    }

    protected SynchronizedMultiMap(MultiMap<K, V> map) {
        super(map);
    }

    @Override
    protected synchronized Map<K, Collection<V>> map() {
        synchronized (this) {
            if (this.map == null) {
                this.map = this.createEmptyMap();
            }
            return this.map;
        }
    }

    /**
     * Creates a new backing map. This method is invoked by {@link #map()} when the backing map
     * needs to be created. A subclass can override this method to return a map implementation of
     * its choice.
     *
     * @return the backing map
     */
    protected Map<K, Collection<V>> createEmptyMap() {
        return new HashMap<>();
    }

}
