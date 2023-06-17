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

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.jpa.annotations.UseQuerying;
import org.dockbox.hartshorn.jpa.query.NamedQueryRegistry;
import org.dockbox.hartshorn.jpa.query.QueryComponentFactory;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.context.EntityManagerQueryConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Service
@RequiresActivator(UseQuerying.class)
public class HibernateQueryComponentFactory implements QueryComponentFactory {

    @Override
    public NamedQueryRegistry queryRegistry(final EntityManager entityManager) {
        return new HibernateNamedQueryRegistry(entityManager);
    }

    @Override
    public NamedQueryRegistry queryRegistry(final EntityManagerFactory entityManagerFactory) {
        return new HibernateNamedQueryRegistry(entityManagerFactory);
    }

    @Override
    public QueryConstructor queryConstructor(final EntityManager entityManager) {
        return new EntityManagerQueryConstructor(entityManager);
    }
}
