package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentKey.Builder;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BindsMethodDependencyResolver implements DependencyResolver {

    @Override
    public Set<DependencyContext> resolve(final Collection<ComponentContainer> containers, final ApplicationContext applicationContext) {
        return containers.stream()
                .flatMap((ComponentContainer componentContainer) -> this.resolveSingle(componentContainer, applicationContext).stream())
                .collect(Collectors.toSet());
    }

    private Set<DependencyContext> resolveSingle(final ComponentContainer componentContainer, final ApplicationContext applicationContext) {
        final TypeView<?> componentType = componentContainer.type();
        final List<? extends MethodView<?, ?>> bindsMethods = componentType.methods().annotatedWith(Binds.class);
        return bindsMethods.stream()
                .map(bindsMethod -> this.resolve(bindsMethod, componentContainer, applicationContext))
                .collect(Collectors.toSet());
    }

    private DependencyContext resolve(final MethodView<?, ?> bindsMethod, final ComponentContainer componentContainer, final ApplicationContext applicationContext) {
        final Binds bindingDecorator = bindsMethod.annotations()
                .get(Binds.class)
                .orElseThrow(() -> new IllegalStateException("Method is not annotated with @Binds"));
        final ComponentKey<?> componentKey = this.constructComponentKey(bindsMethod, bindingDecorator);
        final Set<ComponentKey<?>> dependencies = this.resolveDependencies(bindsMethod, componentContainer, applicationContext);
        return null;
    }

    private Set<ComponentKey<?>> resolveDependencies(final MethodView<?, ?> bindsMethod, final ComponentContainer componentContainer, final ApplicationContext applicationContext) {
        for (ParameterView<?> parameter : bindsMethod.parameters().all()) {

        }
        return null;
    }

    private ComponentKey<?> constructComponentKey(final MethodView<?, ?> bindsMethod, final Binds bindingDecorator) {
        Builder<?> keyBuilder = ComponentKey.builder(bindsMethod.returnType().type());
        if (StringUtilities.notEmpty(bindingDecorator.value())) {
            keyBuilder = keyBuilder.name(bindingDecorator.value());
        }
        return keyBuilder.build();
    }
}
