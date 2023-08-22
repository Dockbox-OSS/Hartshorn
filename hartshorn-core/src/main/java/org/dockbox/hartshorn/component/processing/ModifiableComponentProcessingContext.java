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

package org.dockbox.hartshorn.component.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;

import java.util.function.Consumer;

public class ModifiableComponentProcessingContext<T> extends ComponentProcessingContext<T> {

    private final Consumer<T> onLockRequested;
    private boolean requestInstanceLock = false;

    public ModifiableComponentProcessingContext(final ApplicationContext applicationContext, final ComponentKey<T> key,
                                                final T instance, final Consumer<T> onLockRequested) {
        super(applicationContext, key, instance);
        this.onLockRequested = onLockRequested;
    }

    public ModifiableComponentProcessingContext<T> instance(final T instance) {
        if (this.requestInstanceLock) {
            throw new IllegalStateException("Cannot modify instance after lock has been requested");
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
