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

package org.dockbox.hartshorn.inject.binding.collection;

import java.util.Iterator;
import java.util.List;

import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.binding.AbstractBindingHierarchy;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;

public class CollectionBindingHierarchy<T> extends AbstractBindingHierarchy<ComponentCollection<T>> {

    public CollectionBindingHierarchy(ComponentKey<ComponentCollection<T>> componentKey, ApplicationContext applicationContext) {
        super(componentKey, applicationContext);
    }

    public CollectionProvider<T> getOrCreateProvider(int priority) {
        Provider<ComponentCollection<T>> existingProvider = this.get(priority).orCompute(() -> {
            Provider<ComponentCollection<T>> collectionProvider = new CollectionProvider<>();
            this.add(priority, collectionProvider);
            return collectionProvider;
        }).orNull();
        if (existingProvider instanceof CollectionProvider<T> collectionProvider) {
            return collectionProvider;
        }
        else {
            throw new IllegalStateException("Existing provider is not a CollectionProvider");
        }
    }

    @Override
    protected String contractTypeToString() {
        ParameterizableType collectionParameterizableType = this.key().parameterizedType();
        List<ParameterizableType> parameters = collectionParameterizableType.parameters();
        if (parameters.size() == 1) {
            return parameters.get(0).toString();
        }
        else {
            throw new IllegalStateException("Component key is not typed correctly");
        }
    }

    @NonNull
    @Override
    public Iterator<Map.Entry<Integer, Provider<ComponentCollection<T>>>> iterator() {
        return super.iterator();
    }
}
