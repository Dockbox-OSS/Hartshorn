/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.context.AutoCreating;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.collections.MultiMap;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

@AutoCreating
public class ProviderContextList extends DefaultContext {

    private final MultiMap<Integer, ProviderContext> elements = MultiMap.<Integer, ProviderContext>builder()
            .collectionSupplier(ConcurrentHashMap::newKeySet)
            .mapSupplier(ConcurrentSkipListMap::new)
            .makeConcurrent(true)
            .build();

    public void add(final ProviderContext context) {
        final int phase = context.provider().phase();
        this.elements.put(phase, context);
    }

    public MultiMap<Integer, ProviderContext> elements() {
        return this.elements;
    }

    public Collection<ProviderContext> get(final Key<?> key) {
        return this.elements.allValues().stream()
                .filter(context -> context.key().equals(key))
                .toList();
    }

    public boolean containsKey(final Key<?> key) {
        return this.elements.allValues().stream()
                .anyMatch(context -> context.key().equals(key));
    }
}
