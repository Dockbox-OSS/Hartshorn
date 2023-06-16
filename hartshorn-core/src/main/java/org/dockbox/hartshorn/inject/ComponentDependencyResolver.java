package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;

public class ComponentDependencyResolver extends AbstractExecutableElementDependencyResolver {

    @Override
    protected Set<DependencyContext<?>> resolveSingle(final ComponentContainer componentContainer, final ApplicationContext applicationContext) throws DependencyResolutionException {
        final TypeView<?> type = componentContainer.type();
        final ConstructorView<?> constructorView = CyclingConstructorAnalyzer.findConstructor(type)
                .mapError(DependencyResolutionException::new)
                .rethrow()
                .orNull();

        if (constructorView == null) {
            return Set.of();
        }
        final Set<ComponentKey<?>> dependencies = this.resolveDependencies(constructorView);

        final ComponentKey<?> componentKey = ComponentKey.of(type.type());
        return Set.of(new ManagedComponentDependencyContext<>(componentKey, dependencies));
    }
}
