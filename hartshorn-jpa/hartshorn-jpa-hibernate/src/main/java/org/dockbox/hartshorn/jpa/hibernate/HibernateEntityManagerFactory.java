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
