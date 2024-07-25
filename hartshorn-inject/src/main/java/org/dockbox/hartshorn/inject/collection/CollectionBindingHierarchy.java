/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.inject.collection;

import java.util.List;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.binding.AbstractBindingHierarchy;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;

/**
 * A specialized {@link AbstractBindingHierarchy} for {@link ComponentCollection} instances. The primary
 * difference being that this hierarchy is constrained to only permit {@link CollectionInstantiationStrategy} instances
 * which can delegate to zero or more other {@link InstantiationStrategy}s.
 *
 * @param <T> the type of the elements in the collection
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CollectionBindingHierarchy<T> extends AbstractBindingHierarchy<ComponentCollection<T>> {

    public CollectionBindingHierarchy(ComponentKey<ComponentCollection<T>> componentKey) {
        super(componentKey);
    }

    /**
     * Retrieves the {@link CollectionInstantiationStrategy} for the given priority, or creates a new one if it does not
     * exist yet.
     *
     * @param priority the priority of the instantiation strategy
     * @return the instantiation strategy
     */
    public CollectionInstantiationStrategy<T> getOrCreateInstantiationStrategy(int priority) {
        InstantiationStrategy<ComponentCollection<T>> existingStrategy = this.get(priority).orCompute(() -> {
            InstantiationStrategy<ComponentCollection<T>> collectionStrategy = new CollectionInstantiationStrategy<>();
            this.add(priority, collectionStrategy);
            return collectionStrategy;
        }).orNull();
        if (existingStrategy instanceof CollectionInstantiationStrategy<T> collectionProvider) {
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
            return parameters.getFirst().toString();
        }
        else {
            throw new IllegalStateException("Component key is not typed correctly");
        }
    }
}
