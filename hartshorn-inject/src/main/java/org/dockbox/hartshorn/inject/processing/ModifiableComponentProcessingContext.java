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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectorContext;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.util.IllegalModificationException;

/**
 * TODO: #1060 Add documentation
 *
 * @param <T> ...
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ModifiableComponentProcessingContext<T> extends ComponentProcessingContext<T> {

    private final ComponentStoreCallback componentStoreCallback;
    private boolean requestInstanceLock = false;

    public ModifiableComponentProcessingContext(
        InjectorContext injectorContext,
        ComponentKey<T> key,
        ComponentRequestContext requestContext,
        ObjectContainer<T> container,
        boolean permitsProxying,
        ComponentStoreCallback componentStoreCallback
    ) {
        super(injectorContext, requestContext, key, container, permitsProxying);
        this.componentStoreCallback = componentStoreCallback;
    }

    public ModifiableComponentProcessingContext<T> instance(T instance) {
        if (this.requestInstanceLock) {
            throw new IllegalModificationException("Cannot modify instance after lock has been requested");
        }
        super.container = super.container.copyForObject(instance);
        this.componentStoreCallback.store(this.key(), this.container());
        return this;
    }

    public void requestInstanceLock() {
        this.requestInstanceLock = true;
        this.componentStoreCallback.lock(this.key(), this.container());
    }

    public boolean isInstanceLocked() {
        return this.requestInstanceLock;
    }
}
