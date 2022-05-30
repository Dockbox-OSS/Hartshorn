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

package org.dockbox.hartshorn.data.service;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.factory.Factory;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.dockbox.hartshorn.inject.Enable;

/**
 * Default factory for bound {@link JpaRepository} instances.
 * @author Guus Lieben
 * @since 22.3
 */
@Service
@RequiresActivator(UsePersistence.class)
public interface JpaRepositoryFactory {
    @Factory
    <T> JpaRepository<T, ?> repository(Class<T> type, PersistenceConnection connection);

    @Factory
    @Enable(false)
    <T> JpaRepository<T, ?> repository(Class<T> type);
}
