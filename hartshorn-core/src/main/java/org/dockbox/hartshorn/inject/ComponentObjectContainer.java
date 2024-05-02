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

package org.dockbox.hartshorn.inject;

/**
 * A specialized {@link ObjectContainer} for singular instances. This is a basic implementation,
 * and only tracks the instance and whether the instance has already been processed.
 *
 * @param <T> the type of the object instance
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ComponentObjectContainer<T> extends AbstractObjectContainer<T> {

    private final LifecycleType lifecycleType;
    private boolean processed = false;

    private ComponentObjectContainer(T instance, LifecycleType lifecycleType) {
        super(instance);
        this.lifecycleType = lifecycleType;
    }

    public static <T> ComponentObjectContainer<T> empty() {
        return ofLifecycleType(null, LifecycleType.PROTOTYPE);
    }

    public static <T> ComponentObjectContainer<T> ofPrototype(T instance) {
        return ofLifecycleType(instance, LifecycleType.PROTOTYPE);
    }

    public static <T> ComponentObjectContainer<T> ofSingleton(T instance) {
        return ofLifecycleType(instance, LifecycleType.SINGLETON);
    }

    public static <T> ComponentObjectContainer<T> ofLifecycleType(T instance, LifecycleType lifecycleType) {
        return new ComponentObjectContainer<>(instance, lifecycleType);
    }

    /**
     * Returns whether the object instance has been processed or not.
     *
     * @return {@code true} if the object instance has been processed, {@code false} otherwise
     */
    @Override
    public boolean processed() {
        return this.processed;
    }

    /**
     * Sets whether the object instance has been processed or not. This method is intended to be used by
     * providers that are responsible for processing the object instance.
     *
     * @param processed whether the object instance has been processed or not
     */
    @Override
    public void processed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public LifecycleType lifecycleType() {
        return this.lifecycleType;
    }

    @Override
    public ObjectContainer<T> copyForObject(T instance) {
        if (instance == this.instance()) {
            return this;
        }
        ComponentObjectContainer<T> container = ofLifecycleType(instance, this.lifecycleType);
        container.processed(this.processed);
        return container;
    }
}
