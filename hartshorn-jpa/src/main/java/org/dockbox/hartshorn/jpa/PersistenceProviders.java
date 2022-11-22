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
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerLookup;
import org.dockbox.hartshorn.jpa.entitymanager.EntityTypeLookup;
import org.dockbox.hartshorn.jpa.entitymanager.JpaEntityTypeLookup;
import org.dockbox.hartshorn.jpa.entitymanager.ProxyAttachedEntityManagerLookup;
import org.dockbox.hartshorn.jpa.query.context.AggregateJpaQueryContextCreator;
import org.dockbox.hartshorn.jpa.query.context.ImplicitNamedJpaQueryContextCreator;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContextCreator;
import org.dockbox.hartshorn.jpa.query.context.NamedJpaQueryContextCreator;
import org.dockbox.hartshorn.jpa.query.context.UnnamedJpaQueryContextCreator;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;

import jakarta.inject.Singleton;

@Service
@RequiresActivator(UsePersistence.class)
public class PersistenceProviders {

    @Provider("jpa_query")
    public ParameterLoader<?> jpaParameterLoader() {
        return new JpaParameterLoader();
    }

    @Provider
    public EntityManagerLookup entityManagerLookup() {
        return new ProxyAttachedEntityManagerLookup();
    }

    @Provider(phase = -128)
    public EntityTypeLookup entityTypeLookup() {
        return new JpaEntityTypeLookup();
    }

    @Provider
    @Singleton
    public JpaQueryContextCreator queryContextFactory(final EntityTypeLookup entityTypeLookup) {
        final AggregateJpaQueryContextCreator contextFactory = new AggregateJpaQueryContextCreator();
        contextFactory.register(0, new ImplicitNamedJpaQueryContextCreator(entityTypeLookup));
        contextFactory.register(25, new UnnamedJpaQueryContextCreator());
        contextFactory.register(50, new NamedJpaQueryContextCreator());
        return contextFactory;
    }
}
