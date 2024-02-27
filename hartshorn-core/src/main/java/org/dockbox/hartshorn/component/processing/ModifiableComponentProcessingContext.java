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

package org.dockbox.hartshorn.component.processing;

import java.util.function.Consumer;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.util.IllegalModificationException;

public class ModifiableComponentProcessingContext<T> extends ComponentProcessingContext<T> {

    private final Consumer<T> onLockRequested;
    private boolean requestInstanceLock = false;

    public ModifiableComponentProcessingContext(ApplicationContext applicationContext, ComponentKey<T> key,
            ComponentRequestContext requestContext,
            T instance, boolean permitsProxying, Consumer<T> onLockRequested) {
        super(applicationContext, requestContext, key, instance, permitsProxying);
        this.onLockRequested = onLockRequested;
    }

    public ModifiableComponentProcessingContext<T> instance(T instance) {
        if (this.requestInstanceLock) {
            throw new IllegalModificationException("Cannot modify instance after lock has been requested");
        }
        super.instance = instance;
        return this;
    }

    public void requestInstanceLock() {
        this.requestInstanceLock = true;
        this.onLockRequested.accept(this.instance());
    }

    public boolean isInstanceLocked() {
        return this.requestInstanceLock;
    }
}
