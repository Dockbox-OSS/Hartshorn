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
import org.dockbox.hartshorn.data.annotations.Transactional;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.EntityManagerLookup;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.proxy.ProxyCallback;
import org.dockbox.hartshorn.proxy.processing.PhasedProxyCallbackPostProcessor;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.MethodContext;

import jakarta.persistence.EntityManager;

public class TransactionalProxyCallbackPostProcessor extends PhasedProxyCallbackPostProcessor<UsePersistence> {

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        return !key.type().methods(Transactional.class).isEmpty();
    }

    @Override
    public <T> boolean wraps(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return method.annotation(Transactional.class).present();
    }

    @Override
    public <T> ProxyCallback<T> doBefore(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        final TransactionFactory transactionFactory = context.get(TransactionFactory.class);
        final EntityManagerLookup lookup = context.get(EntityManagerLookup.class);

        return (methodContext, target, args) -> {
            final Result<EntityManager> entityManager = lookup.lookup(target);
            if (entityManager.absent()) {
                throw new IllegalStateException("No entity manager found in execution cache for method " + methodContext.qualifiedName() + " in type " + methodContext.parent().name() + ". Expected target to be a JpaRepository.");
            }

            final TransactionManager manager = transactionFactory.manager(entityManager.get());
            manager.beginTransaction();
        };
    }

    @Override
    public <T> ProxyCallback<T> doAfter(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return this.flushTarget();
    }

    @Override
    public <T> ProxyCallback<T> doAfterThrowing(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return this.flushTarget();
    }

    protected <T> ProxyCallback<T> flushTarget() {
        return (methodContext, target, args) -> {
            if (target instanceof JpaRepository jpaRepository) {
                jpaRepository.flush();
            }
        };
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LATE + 1;
    }
}
