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

package org.dockbox.hartshorn.inject.provider;

import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;

/**
 * A {@link ComponentProvider} that uses a {@link SingletonCache} to store singleton components. Typically the
 * provider itself should not determine what is a singleton and what is not, but rather any backing {@link Provider},
 * {@link ComponentContainer}, or similar component should indicate this.
 *
 * @see SingletonCache
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface SingletonCacheComponentProvider extends ComponentProvider {

    /**
     * Returns the {@link SingletonCache} used by this provider to store singleton components.
     *
     * @return the singleton cache
     */
    SingletonCache singletonCache();
}
