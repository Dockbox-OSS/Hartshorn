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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.binding.SingletonCache;
import org.dockbox.hartshorn.util.IllegalModificationException;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class LocalCacheComponentStoreCallback implements ComponentStoreCallback {

    private final SingletonCache singletonCache;

    public LocalCacheComponentStoreCallback(SingletonCache singletonCache) {
        this.singletonCache = singletonCache;
    }

    @Override
    public <T> void store(ComponentKey<T> key, ObjectContainer<T> container) {
        this.actOnSingletonOnly(container, () -> this.singletonCache.put(key, container.instance()));
    }

    @Override
    public <T> void lock(ComponentKey<T> key, ObjectContainer<T> container) {
        this.actOnSingletonOnly(container, () -> this.lockSingleton(key, container));
    }

    private <T> void lockSingleton(ComponentKey<T> key, ObjectContainer<T> container) {
        this.singletonCache.get(key).peek(instance -> {
            if (instance != container.instance()) {
                this.store(key, container);
            }
        });
        this.singletonCache.lock(key);
    }

    private <T> void actOnSingletonOnly(ObjectContainer<T> container, Runnable singletonOperation) {
        if (!container.permitsObjectCaching()) {
            return;
        }
        switch (container.lifecycleType()) {
            case SINGLETON:
                singletonOperation.run();
                break;
            case PROTOTYPE:
                // Do nothing, as prototypes are not stored in the cache
                break;
            default:
                throw new IllegalModificationException("Unknown lifecycle type " + container.lifecycleType());
        }
    }
}
