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

package org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerCarrier;

/**
 * Default factory for bound {@link JpaRepository} instances.
 * @author Guus Lieben
 * @since 22.3
 */
public interface JpaRepositoryFactory {

    /**
     * Creates a new {@link JpaRepository} instance for the given type and configuration.
     * The connection is immediately set up.
     *
     * @param type The entity type
     * @param entityManagerCarrier The entity manager carrier, which is used to set up the connection
     * @return A new {@link JpaRepository} instance
     * @param <T> The entity type
     * @since 22.4
     */
    <T> JpaRepository<T, ?> repository(Class<T> type, EntityManagerCarrier entityManagerCarrier);

    /**
     * Creates a new {@link JpaRepository} instance for the given type, without setting the
     * connection. As the connection is not set, the repository will not be enabled automatically.
     *
     * <p>This is useful for creating a repository that is configured manually. In scenarios where
     * the connection is already known, use {@link #repository(Class, EntityManagerCarrier)}.
     *
     * @param type The entity type
     * @return A new {@link JpaRepository} instance
     * @param <T> The entity type
     * @since 22.3
     */
    <T> JpaRepository<T, ?> repository(Class<T> type, ApplicationContext applicationContext);
}
