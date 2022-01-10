/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.data.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.proxy.ProxyCallback;
import org.dockbox.hartshorn.core.services.PhasedProxyCallbackPostProcessor;
import org.dockbox.hartshorn.data.TransactionFactory;
import org.dockbox.hartshorn.data.TransactionManager;
import org.dockbox.hartshorn.data.annotations.Transactional;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.JpaRepository;

import javax.persistence.EntityManager;

@AutomaticActivation
public class TransactionalProxyCallbackPostProcessor extends PhasedProxyCallbackPostProcessor<UsePersistence> {

    @Override
    public Class<UsePersistence> activator() {
        return UsePersistence.class;
    }

    @Override
    public <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return !key.type().methods(Transactional.class).isEmpty();
    }

    @Override
    public <T> boolean wraps(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return method.annotation(Transactional.class).present();
    }

    @Override
    public <T> ProxyCallback<T> doBefore(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        final TransactionFactory transactionFactory = context.get(TransactionFactory.class);

        return (methodContext, target, args, proxyContext) -> {
            if (target instanceof JpaRepository jpaRepository) {
                final EntityManager entityManager = jpaRepository.entityManager();
                final TransactionManager manager = transactionFactory.manager(entityManager);
                manager.beginTransaction();
            } else {
                throw new IllegalStateException("No entity manager found in execution cache for method " + methodContext.qualifiedName() + " in type " + methodContext.parent().name() + ". Expected target to be a JpaRepository.");
            }
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
        return (methodContext, target, args, proxyContext) -> {
            if (target instanceof JpaRepository jpaRepository) {
                jpaRepository.flush();
            }
        };
    }

}
