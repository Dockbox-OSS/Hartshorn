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
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.TypeAwareProvider;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBindingHierarchy<T> implements BindingHierarchy<T> {

    private final NavigableMap<Integer, Provider<T>> providers = new TreeMap<>(Collections.reverseOrder());

    protected NavigableMap<Integer, Provider<T>> priorityProviders() {
        return this.providers;
    }

    @Override
    public List<Provider<T>> providers() {
        return List.copyOf(this.priorityProviders().values());
    }

    @Override
    public BindingHierarchy<T> add(Provider<T> provider) {
        return this.add(-1, provider);
    }

    @Override
    public BindingHierarchy<T> add(int priority, Provider<T> provider) {
        // Default providers may be overwritten without further warnings
        if (this.priorityProviders().containsKey(priority) && priority != -1) {
            this.applicationContext().log().warn(("There is already a provider for %s with priority %d. It will be overwritten! " +
                    "To avoid unexpected behavior, ensure the priority is not already present. Current hierarchy: %s").formatted(this.key()
                    .type().getSimpleName(), priority, this));
        }
        this.priorityProviders().put(priority, provider);
        return this;
    }

    @Override
    public BindingHierarchy<T> addNext(final Provider<T> provider) {
        int next = -1;
        if (!this.priorityProviders().isEmpty()) {
            next = this.priorityProviders().lastKey()+1;
        }
        return this.add(next, provider);
    }

    @Override
    public BindingHierarchy<T> merge(final BindingHierarchy<T> hierarchy) {
        BindingHierarchy<T> merged = new NativeBindingHierarchy<>(this.key(), this.applicationContext());
        // Low priority, other
        for (final Entry<Integer, Provider<T>> entry : hierarchy) {
            merged.add(entry.getKey(), entry.getValue());
        }
        // High priority, self
        for (final Entry<Integer, Provider<T>> entry : this) {
            merged.add(entry.getKey(), entry.getValue());
        }
        return merged;
    }

    @Override
    public Option<Provider<T>> get(final int priority) {
        return Option.of(this.priorityProviders().getOrDefault(priority, null));
    }

    @Override
    public int highestPriority() {
        NavigableMap<Integer, Provider<T>> providers = this.priorityProviders();
        return providers.isEmpty() ? -1 : providers.firstKey();
    }

    @Override
    public int size() {
        return this.priorityProviders().size();
    }

    @NotNull
    @Override
    public Iterator<Entry<Integer, Provider<T>>> iterator() {
        return this.priorityProviders().entrySet().iterator();
    }

    @Override
    public String toString() {
        String contract = this.contractTypeToString();
        String keyName = this.key().name();
        String name = "";
        if (keyName != null) {
            name = "::" + keyName;
        }

        // The priorities are stored high to low, however we want to display them as low-to-high.
        List<Entry<Integer, Provider<T>>> entries = new ArrayList<>(this.priorityProviders().entrySet());
        Collections.reverse(entries);

        String hierarchy = entries.stream()
                .map(entry -> {
                    Provider<T> value = entry.getValue();
                    String target = value.toString();
                    if (value instanceof TypeAwareProvider<?> typeAwareProvider) {
                        target = typeAwareProvider.type().getSimpleName();
                    }
                    return "%s: %s".formatted(String.valueOf(entry.getKey()), target);
                })
                .collect(Collectors.joining(" -> "));

        return "Hierarchy[%s%s]: %s".formatted(contract, name, hierarchy);
    }

    protected String contractTypeToString() {
        return this.key().parameterizedType().toString();
    }
}
