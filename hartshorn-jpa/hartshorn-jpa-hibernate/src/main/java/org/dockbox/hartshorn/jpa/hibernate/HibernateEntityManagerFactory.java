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
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerCarrier;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerFactory;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;

@Service
@RequiresActivator(UsePersistence.class)
public class HibernateEntityManagerFactory implements EntityManagerFactory {

    @Override
    public EntityManagerCarrier entityManagerCarrier(final ApplicationContext applicationContext, final DataSourceConfiguration configuration) {
        return new HibernateEntityManagerCarrier(applicationContext, configuration);
    }
}