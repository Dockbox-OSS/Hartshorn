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
