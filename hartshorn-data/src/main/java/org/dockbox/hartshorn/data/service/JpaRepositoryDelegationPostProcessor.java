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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.data.annotations.DataSource;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.processing.ProxyDelegationPostProcessor;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.List;

public class JpaRepositoryDelegationPostProcessor extends ProxyDelegationPostProcessor<JpaRepository, UsePersistence> {

    @Override
    protected Class<JpaRepository> parentTarget() {
        return JpaRepository.class;
    }

    @Override
    protected JpaRepository concreteDelegator(final ApplicationContext context, final ProxyFactory<JpaRepository, ?> handler, final TypeContext<? extends JpaRepository> parent) {
        final TypeContext<JpaRepository> repositoryType = TypeContext.of(handler.type());
        final List<TypeContext<?>> list = repositoryType.typeParameters(JpaRepository.class);
        final Class<?> type = list.get(0).type();

        Key<PersistenceConnection> connection = Key.of(PersistenceConnection.class);
        final Result<DataSource> dataSource = repositoryType.annotation(DataSource.class);
        if (dataSource.present()) {
            connection = connection.name(dataSource.get().value());
        }

        return context.get(JpaRepositoryFactory.class).repository(type, this.connection(context, connection));
    }

    protected PersistenceConnection connection(final ApplicationContext context, final Key<PersistenceConnection> key) {
        PersistenceConnection connection = null;
        try {
            connection = context.get(key);
        }
        catch (final Exception e) {
            if (key.name() != null) {
                context.handle("Could not obtain custom data source '" + key.name().value() + "', attempting to get default connection", e);
                connection = context.get(Key.of(PersistenceConnection.class));
            }
        }
        return connection;
    }
}
