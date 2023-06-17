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

package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.JpaRepositoryFactory;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerCarrier;

@Service
@RequiresActivator(UsePersistence.class)
public class HibernateJpaRepositoryFactory implements JpaRepositoryFactory {

    @Override
    public <T> JpaRepository<T, ?> repository(final Class<T> type, final EntityManagerCarrier entityManagerCarrier) {
        if (entityManagerCarrier instanceof HibernateEntityManagerCarrier hibernateEntityManagerCarrier) {
            return new HibernateJpaRepository<>(type, hibernateEntityManagerCarrier);
        }
        throw new IllegalArgumentException("Expected HibernateEntityManagerCarrier, got " + entityManagerCarrier.getClass().getSimpleName());
    }

    @Enable(false)
    @Override
    public <T> JpaRepository<T, ?> repository(final Class<T> type, final ApplicationContext applicationContext) {
        return new HibernateJpaRepository<>(type, applicationContext);
    }
}
