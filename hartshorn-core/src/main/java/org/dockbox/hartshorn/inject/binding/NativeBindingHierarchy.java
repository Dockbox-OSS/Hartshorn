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

package org.dockbox.hartshorn.inject.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.util.option.Option;

/**
 * The default implementation of the {@link BindingHierarchy} interface. This uses a specified {@link ComponentKey}
 * to identify the binding hierarchy, and stores the bindings in a {@link TreeMap}.
 *
 * @param <C> The type of type to provide.
 * @author Guus Lieben
 * @since 0.4.3
 * @see BindingHierarchy
 */
public class NativeBindingHierarchy<C> implements BindingHierarchy<C> {

    private final ComponentKey<C> key;
    private final ApplicationContext applicationContext;
    private final TreeMap<Integer, Provider<C>> bindings = new TreeMap<>(Collections.reverseOrder());

    public NativeBindingHierarchy(ComponentKey<C> key, ApplicationContext applicationContext) {
        this.key = key;
        this.applicationContext = applicationContext;
    }

    @Override
    public ComponentKey<C> key() {
        return this.key;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public List<Provider<C>> providers() {
        return List.copyOf(this.bindings.values());
    }

    @Override
    public BindingHierarchy<C> add(Provider<C> provider) {
        return this.add(-1, provider);
    }

    @Override
    public BindingHierarchy<C> add(int priority, Provider<C> provider) {
        // Default providers may be overwritten without further warnings
        if (this.bindings.containsKey(priority) && priority != -1) {
            this.applicationContext().log().warn(("There is already a provider for %s with priority %d. It will be overwritten! " +
                    "To avoid unexpected behavior, ensure the priority is not already present. Current hierarchy: %s").formatted(this.key()
                    .type().getSimpleName(), priority, this));
        }
        this.bindings.put(priority, provider);
        return this;
    }

    @Override
    public BindingHierarchy<C> addNext(Provider<C> provider) {
        int next = -1;
        if (!this.bindings.isEmpty()) {
            next = this.bindings.lastKey()+1;
        }
        return this.add(next, provider);
    }

    @Override
    public BindingHierarchy<C> merge(BindingHierarchy<C> hierarchy) {
        BindingHierarchy<C> merged = new NativeBindingHierarchy<>(this.key(), this.applicationContext);
        // Low priority, other
        for (Entry<Integer, Provider<C>> entry : hierarchy) {
            merged.add(entry.getKey(), entry.getValue());
        }
        // High priority, self
        for (Entry<Integer, Provider<C>> entry : this) {
            merged.add(entry.getKey(), entry.getValue());
        }
        return merged;
    }

    @Override
    public int size() {
        return this.bindings.size();
    }

    @Override
    public Option<Provider<C>> get(int priority) {
        return Option.of(this.bindings.getOrDefault(priority, null));
    }

    @Override
    public Option<Provider<C>> highestPriority() {
        return Option.of(this.bindings.firstEntry()).map(Entry::getValue);
    }

    @Override
    public String toString() {
        String contract = this.key().type().getSimpleName();
        String keyName = this.key().name();
        String name = "";
        if (keyName != null) {
            name = "::" + keyName;
        }

        // The priorities are stored high to low, however we want to display them as low-to-high.
        List<Entry<Integer, Provider<C>>> entries = new ArrayList<>(this.bindings.entrySet());
        Collections.reverse(entries);

        String hierarchy = entries.stream()
                .map(entry -> {
                    Provider<C> value = entry.getValue();
                    String target = value.toString();
                    if (value instanceof ContextDrivenProvider<?> contextDrivenProvider) {
                        target = contextDrivenProvider.type().getSimpleName();
                    }
                    return "%s: %s".formatted(String.valueOf(entry.getKey()), target);
                })
                .collect(Collectors.joining(" -> "));

        return "Hierarchy[%s%s]: %s".formatted(contract, name, hierarchy);
    }

    @NonNull
    @Override
    public Iterator<Entry<Integer, Provider<C>>> iterator() {
        return this.bindings.entrySet().iterator();
    }
}
