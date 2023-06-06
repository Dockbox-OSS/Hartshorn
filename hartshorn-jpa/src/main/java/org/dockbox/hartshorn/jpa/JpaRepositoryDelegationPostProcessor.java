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
import org.dockbox.hartshorn.jpa.annotations.DataSource;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerCarrier;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerFactory;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.component.processing.proxy.ProxyDelegationPostProcessor;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;

@SuppressWarnings("rawtypes")
public class JpaRepositoryDelegationPostProcessor extends ProxyDelegationPostProcessor<JpaRepository> {

    @Override
    protected Class<JpaRepository> parentTarget() {
        return JpaRepository.class;
    }

    @Override
    protected JpaRepository concreteDelegator(final ApplicationContext context, final ProxyFactory<JpaRepository> handler, final Class<? extends JpaRepository> parent) {
        final TypeView<JpaRepository> repositoryType = context.environment().introspect(handler.type());
        final List<TypeView<?>> list = repositoryType.typeParameters().from(JpaRepository.class);
        final Class<?> type = list.get(0).type();

        final DataSourceList dataSourceList = context.get(DataSourceList.class);
        final DataSourceConfiguration sourceConfiguration = repositoryType.annotations()
                .get(DataSource.class)
                .map(DataSource::value)
                .map(dataSourceList::get)
                .orCompute(dataSourceList::defaultConnection)
                .orElseThrow(() -> new IllegalStateException("No data source found for repository " + repositoryType.type().getName()));

        final EntityManagerCarrier entityManagerCarrier = context.get(EntityManagerFactory.class)
                .entityManagerCarrier(context, sourceConfiguration);

        return context.get(JpaRepositoryFactory.class).repository(type, entityManagerCarrier);
    }
}
