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
import java.util.Map;

public abstract class StandardMultiMap<K, V> extends AbstractMultiMap<K, V> {

    protected Map<K, Collection<V>> map;

    protected StandardMultiMap() {
    }

    protected StandardMultiMap(MultiMap<K, V> map) {
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

}
