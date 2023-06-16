package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;

import java.util.Collection;
import java.util.Set;

public interface DependencyResolver {

    Set<DependencyContext<?>> resolve(Collection<ComponentContainer> containers, ApplicationContext applicationContext) throws DependencyResolutionException;

}
