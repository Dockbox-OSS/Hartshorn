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
import org.dockbox.hartshorn.util.CustomMultiMap;
import org.dockbox.hartshorn.util.MultiMap;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.ObtainableElement;
import org.dockbox.hartshorn.util.reflect.TypedElementContext;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@AutoCreating
public class ProviderListContext extends DefaultContext {

    private final MultiMap<Key<?>, AnnotatedElementContext<?>> elements = new CustomMultiMap<>(CopyOnWriteArrayList::new);

    public <E extends AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>> void add(final Key<?> key, final E element) {
        this.elements.put(key, element);
    }

    public <E extends AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>> List<E> elements() {
        return this.elements.allValues().stream()
                .map(e -> (E) e)
                .toList();
    }

    public Set<Entry<Key<?>, Collection<AnnotatedElementContext<?>>>> entries() {
        return this.elements.entrySet();
    }

    public Collection<AnnotatedElementContext<?>> get(final Key<?> key) {
        return this.elements.get(key);
    }

    public boolean containsKey(final Key<?> key) {
        return this.elements.containsKey(key);
    }
}
