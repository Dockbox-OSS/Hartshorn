package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;

import java.util.Collection;
import java.util.Set;

public class PostProcessorDependencyResolver implements DependencyResolver {

    @Override
    public Set<DependencyContext<?>> resolve(Collection<DependencyDeclarationContext<?>> containers, ApplicationContext applicationContext) throws DependencyResolutionException {

        return null;
    }
}
