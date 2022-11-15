/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.factory.Factory;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;

/**
 * Default factory for bound {@link JpaRepository} instances.
 * @author Guus Lieben
 * @since 22.3
 */
@Service
@RequiresActivator(UsePersistence.class)
public interface JpaRepositoryFactory {

    /**
     * Creates a new {@link JpaRepository} instance for the given type and configuration.
     * The connection is immediately set up.
     *
     * @param type The entity type
     * @param connection The connection configuration
     * @return A new {@link JpaRepository} instance
     * @param <T> The entity type
     * @since 22.4
     */
    @Factory
    <T> JpaRepository<T, ?> repository(Class<T> type, DataSourceConfiguration connection);

    /**
     * Creates a new {@link JpaRepository} instance for the given type, without setting the
     * connection. As the connection is not set, the repository will not be enabled automatically.
     *
     * This is useful for creating a repository that is configured manually. In scenarios where
     * the connection is already known, use {@link #repository(Class, DataSourceConfiguration)}.
     *
     * @param type The entity type
     * @return A new {@link JpaRepository} instance
     * @param <T> The entity type
     * @since 22.3
     */
    @Factory
    @Enable(false)
    <T> JpaRepository<T, ?> repository(Class<T> type);
}
