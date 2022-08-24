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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.data.TransactionFactory;
import org.dockbox.hartshorn.data.TransactionManager;
import org.dockbox.hartshorn.data.annotations.DataSource;
import org.dockbox.hartshorn.data.annotations.Transactional;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.EntityManagerCarrier;
import org.dockbox.hartshorn.data.jpa.EntityManagerContext;
import org.dockbox.hartshorn.data.jpa.EntityManagerFactory;
import org.dockbox.hartshorn.data.jpa.EntityManagerLookup;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.data.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.data.remote.DataSourceList;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.proxy.ProxyCallback;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.processing.PhasedProxyCallbackPostProcessor;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

import jakarta.persistence.EntityManager;

public class TransactionalProxyCallbackPostProcessor extends PhasedProxyCallbackPostProcessor<UsePersistence> {

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        return !key.type().methods(Transactional.class).isEmpty();
    }

    @Override
    protected <T> T processProxy(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext, final ProxyFactory<T, ?> proxyFactory) {
        // JpaRepository and EntityManagerCarrier expose their own EntityManager, so we don't need to do anything here.
        if (!(instance instanceof JpaRepository || instance instanceof EntityManagerCarrier)) {
            final TypeContext<T> type = instance == null ? key.type() : TypeContext.of(instance);
            final DataSourceList dataSourceList = context.get(DataSourceList.class);
            final DataSourceConfiguration sourceConfiguration = type.annotation(DataSource.class)
                    .map(DataSource::value)
                    .map(dataSourceList::get)
                    .orElse(dataSourceList::defaultConnection)
                    .orNull();

            // Do not fail if no data source is configured, as this may be configured in a different way.
            if (sourceConfiguration != null) {
                final EntityManagerFactory factory = context.get(EntityManagerFactory.class);
                final EntityManagerCarrier carrier = factory.entityManagerCarrier(sourceConfiguration);
                final EntityManagerContext entityManagerContext = new EntityManagerContext(carrier::manager);
                proxyFactory.contextContainer().add(entityManagerContext);
            }
        }
        return instance;
    }

    @Override
    public <T> boolean wraps(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return method.annotation(Transactional.class).present();
    }

    @Override
    public <T> ProxyCallback<T> doBefore(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        final TransactionFactory transactionFactory = context.get(TransactionFactory.class);
        final EntityManagerLookup lookup = context.get(EntityManagerLookup.class);

        return callbackContext -> {
            final TransactionManager manager = this.transactionManager(transactionFactory, lookup, callbackContext.method(), callbackContext.proxy());
            manager.beginTransaction();
        };
    }

    private <T> TransactionManager transactionManager(final TransactionFactory transactionFactory, final EntityManagerLookup lookup, final MethodContext<?, T> methodContext, final T target) {
        final Result<EntityManager> entityManager = lookup.lookup(target);
        if (entityManager.absent()) {
            throw new MissingEntityManagerException(methodContext);
        }
        return transactionFactory.manager(entityManager.get());
    }

    @Override
    public <T> ProxyCallback<T> doAfter(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return this.performAndFlush(context, method, Transactional::commitOnSuccess, TransactionManager::commitTransaction);
    }

    @Override
    public <T> ProxyCallback<T> doAfterThrowing(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return this.performAndFlush(context, method, Transactional::rollbackOnError, TransactionManager::rollbackTransaction);
    }

    private <T> ProxyCallback<T> performAndFlush(final ApplicationContext context, final MethodContext<?, T> method, final Predicate<Transactional> rule, final Consumer<TransactionManager> consumer) {
        final Transactional annotation = method.annotation(Transactional.class).get();
        final boolean ruleResult = rule.test(annotation);

        final ProxyCallback<T> flush = this.flushTarget();
        if (ruleResult) {

            final TransactionFactory transactionFactory = context.get(TransactionFactory.class);
            final EntityManagerLookup lookup = context.get(EntityManagerLookup.class);

            final ProxyCallback<T> callback = callbackContext -> consumer.accept(this.transactionManager(transactionFactory, lookup, callbackContext.method(), callbackContext.proxy()));
            return callback.then(flush);
        }
        return flush;
    }

    protected <T> ProxyCallback<T> flushTarget() {
        return callbackContext -> {
            if (callbackContext.proxy() instanceof JpaRepository jpaRepository) {
                jpaRepository.flush();
            }
        };
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LATE + 1;
    }
}
