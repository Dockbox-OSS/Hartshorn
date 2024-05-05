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

package org.dockbox.hartshorn.proxy.lookup;

import java.util.function.Consumer;

import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.advice.registry.AdvisorRegistry;
import org.dockbox.hartshorn.proxy.advice.registry.StateAwareAdvisorRegistry;

/**
 * A specific {@link ProxyFactory} that is aware of its own state, and exposes it to the outside world.
 *
 * @param <T> the type of the proxy
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public interface StateAwareProxyFactory<T> extends ProxyFactory<T> {

    @Override
    StateAwareAdvisorRegistry<T> advisors();

    @Override
    StateAwareProxyFactory<T> advisors(Consumer<? super AdvisorRegistry<T>> registryConsumer);

    /**
     * Sets whether the current factory should continue tracking changes. If set to false, the factory will not track
     * changes.
     *
     * @param trackState whether the factory should track changes
     * @return the current factory
     */
    StateAwareProxyFactory<T> trackState(boolean trackState);

    /**
     * Returns whether the current factory was modified since its creation. If {@link #trackState(boolean)}
     * was previously set to {@code false}, this method will always return {@code false}. Otherwise, it will return
     * {@code true} if the factory was modified since its creation. If the factory was not modified since its
     * creation, it will return {@code false}.
     *
     * @return whether the factory was modified since its creation
     */
    boolean modified();

    @Override
    StateAwareProxyFactory<T> implement(Class<?>... interfaces);
}
