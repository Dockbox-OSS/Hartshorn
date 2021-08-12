package org.dockbox.hartshorn.persistence.context;

import org.dockbox.hartshorn.di.context.DefaultContext;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EntityContext extends DefaultContext {
    @Getter private final Collection<Class<?>> entities;
}
