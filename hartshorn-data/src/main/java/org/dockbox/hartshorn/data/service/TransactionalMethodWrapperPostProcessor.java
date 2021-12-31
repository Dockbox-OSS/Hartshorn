package org.dockbox.hartshorn.data.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.proxy.MethodWrapperFunction;
import org.dockbox.hartshorn.core.services.PhasedMethodWrapperPostProcessor;
import org.dockbox.hartshorn.data.TransactionFactory;
import org.dockbox.hartshorn.data.TransactionManager;
import org.dockbox.hartshorn.data.annotations.Transactional;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.JpaRepository;

import javax.persistence.EntityManager;

@AutomaticActivation
public class TransactionalMethodWrapperPostProcessor extends PhasedMethodWrapperPostProcessor<UsePersistence> {

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
    public <T> MethodWrapperFunction<T> wrapBefore(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
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
    public <T> MethodWrapperFunction<T> wrapAfter(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return this.flushTarget();
    }

    @Override
    public <T> MethodWrapperFunction<T> wrapAfterThrowing(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance) {
        return this.flushTarget();
    }

    protected <T> MethodWrapperFunction<T> flushTarget() {
        return (methodContext, target, args, proxyContext) -> {
            if (target instanceof JpaRepository jpaRepository) {
                jpaRepository.flush();
            }
        };
    }

}
