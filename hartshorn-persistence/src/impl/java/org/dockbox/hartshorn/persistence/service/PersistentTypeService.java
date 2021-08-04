package org.dockbox.hartshorn.persistence.service;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.persistence.annotations.UsePersistence;
import org.dockbox.hartshorn.persistence.context.EntityContext;
import org.dockbox.hartshorn.util.Reflect;

import java.util.Collection;

import javax.persistence.Entity;

@Service(activators = { UsePersistence.class, UseBootstrap.class })
public class PersistentTypeService {

    @PostBootstrap
    public void scan() {
        final Collection<Class<?>> entities = Reflect.types(Entity.class);
        Hartshorn.context().add(new EntityContext(entities));
    }
}
