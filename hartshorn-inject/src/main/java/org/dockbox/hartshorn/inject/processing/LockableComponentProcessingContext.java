/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.util.IllegalModificationException;

/**
 * A modifiable version of the {@link ComponentProcessingContext}, which allows for modifying the
 * component instance and requesting a lock on the instance. This is useful for scenarios where the
 * component instance needs to be modified and potentially replaced, but afterward should not be
 * modified anymore.
 *
 * @param <T> the type of the component being processed
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class LockableComponentProcessingContext<T> extends ComponentProcessingContext<T> {

    private final ComponentStoreCallback componentStoreCallback;
    private boolean requestInstanceLock = false;

    public LockableComponentProcessingContext(
        InjectionCapableApplication application,
        ComponentKey<T> key,
        ComponentRequestContext requestContext,
        ObjectContainer<T> container,
        boolean permitsProxying,
        ComponentStoreCallback componentStoreCallback
    ) {
        super(application, requestContext, key, container, permitsProxying);
        this.componentStoreCallback = componentStoreCallback;
    }

    /**
     * Sets the instance of the component being processed. If a lock has already been requested,
     * this method will throw an {@link IllegalModificationException}.
     *
     * @param instance the new instance of the component
     *
     * @return this context, for chaining
     *
     * @throws IllegalModificationException if a lock has already been requested
     */
    public LockableComponentProcessingContext<T> instance(T instance) {
        if (this.requestInstanceLock) {
            throw new IllegalModificationException(
                "Cannot modify instance after lock has been requested");
        }
        super.container = super.container.copyForObject(instance);
        this.componentStoreCallback.store(this.key(), this.container());
        return this;
    }

    /**
     * Requests a lock on the component instance, preventing any further modifications to it. Once a
     * lock has been requested, the instance cannot be modified anymore. This method is idempotent;
     * calling it multiple times has no additional effect.
     */
    public void requestInstanceLock() {
        this.requestInstanceLock = true;
        this.componentStoreCallback.lock(this.key(), this.container());
    }

    /**
     * Returns whether a lock on the component instance has been requested.
     *
     * @return true if a lock has been requested, false otherwise
     */
    public boolean isInstanceLocked() {
        return this.requestInstanceLock;
    }
}
