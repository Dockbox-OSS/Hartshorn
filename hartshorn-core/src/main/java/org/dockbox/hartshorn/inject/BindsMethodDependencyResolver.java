package org.dockbox.hartshorn.inject;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentKey.Builder;
import org.dockbox.hartshorn.component.InstallTo;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BindsMethodDependencyResolver implements DependencyResolver {

    @Override
    public Set<DependencyContext<?>> resolve(final Collection<ComponentContainer> containers, final ApplicationContext applicationContext) {
        return containers.stream()
                .flatMap((ComponentContainer componentContainer) -> this.resolveSingle(componentContainer, applicationContext).stream())
                .collect(Collectors.toSet());
    }

    private Set<DependencyContext<?>> resolveSingle(final ComponentContainer componentContainer, final ApplicationContext applicationContext) {
        final TypeView<?> componentType = componentContainer.type();
        final List<? extends MethodView<?, ?>> bindsMethods = componentType.methods().annotatedWith(Binds.class);
        return bindsMethods.stream()
                .map(bindsMethod -> this.resolve(bindsMethod, applicationContext))
                .collect(Collectors.toSet());
    }

    private <T> DependencyContext<?> resolve(final MethodView<?, T> bindsMethod, final ApplicationContext applicationContext) {
        final Binds bindingDecorator = bindsMethod.annotations()
                .get(Binds.class)
                .orElseThrow(() -> new IllegalStateException("Method is not annotated with @Binds"));

        // TODO: Binding strategies -> registry -> resolve
        if (this.isClassBinding(bindsMethod)) {
            return this.resolveClassBinding(bindsMethod, bindingDecorator, applicationContext);
        }
        else {
            return this.resolveInstanceBinding(bindsMethod, bindingDecorator, applicationContext);
        }
    }

    private <T> DependencyContext<?> resolveClassBinding(final MethodView<?, T> bindsMethod, final Binds bindingDecorator, final ApplicationContext applicationContext) {
        final ComponentKey<?> componentKey = this.constructClassComponentKey(bindsMethod, bindingDecorator);
        return null;
    }

    private <T> ComponentKey<?> constructClassComponentKey(MethodView<?, T> bindsMethod, Binds bindingDecorator) {
        return null;
    }

    private <T> DependencyContext<T> resolveInstanceBinding(final MethodView<?, T> bindsMethod, final Binds bindingDecorator, final ApplicationContext applicationContext) {
        final ComponentKey<T> componentKey = this.constructInstanceComponentKey(bindsMethod, bindingDecorator);
        final Set<ComponentKey<?>> dependencies = this.resolveDependencies(bindsMethod);
        final Class<? extends Scope> scope = this.resolveComponentScope(bindsMethod);
        final int priority = bindingDecorator.priority();

        final ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(applicationContext);
        final CheckedSupplier<T> supplier = () -> contextAdapter.load(bindsMethod)
                .mapError(error -> new ComponentInitializationException("Failed to obtain instance for " + bindsMethod.qualifiedName(), error))
                .orNull();

        final boolean lazy = bindingDecorator.lazy();
        final boolean singleton = this.isSingleton(applicationContext, bindsMethod, componentKey);

        return new AutoConfiguringDependencyContext<>(componentKey, dependencies, scope, priority, supplier)
                .lazy(lazy)
                .singleton(singleton);
    }

    private boolean isSingleton(final ApplicationContext applicationContext, final MethodView<?, ?> methodView,
                                final ComponentKey<?> componentKey) {
        // TODO: Include BindingType to check if it's a class or instance binding
        return methodView.annotations().has(Singleton.class)
                || applicationContext.environment().singleton(componentKey.type());
    }

    private Class<? extends Scope> resolveComponentScope(final MethodView<?, ?> bindsMethod) {
        final Option<InstallTo> installToCandidate = bindsMethod.annotations().get(InstallTo.class);
        return installToCandidate.present()
                ? installToCandidate.get().value()
                : Scope.DEFAULT_SCOPE.installableScopeType();
    }

    private Set<ComponentKey<?>> resolveDependencies(final MethodView<?, ?> bindsMethod) {
        return bindsMethod.parameters().all().stream()
                .filter(parameter -> !parameter.annotations().has(HandledInjection.class))
                .map(this::resolveComponentKey)
                .collect(Collectors.toSet());
    }

    private <T> ComponentKey<T> resolveComponentKey(final ParameterView<T> parameter) {
        final TypeView<T> type = parameter.genericType();
        final Builder<T> keyBuilder = ComponentKey.builder(type.type());
        parameter.annotations().get(Named.class)
                .filter(qualifier -> StringUtilities.notEmpty(qualifier.value()))
                .peek(qualifier -> {
            if (StringUtilities.notEmpty(qualifier.value())) {
                keyBuilder.name(qualifier);
            }
        });
        return keyBuilder.build();
    }

    private <T> ComponentKey<T> constructInstanceComponentKey(final MethodView<?, T> bindsMethod, final Binds bindingDecorator) {
        Builder<T> keyBuilder = ComponentKey.builder(bindsMethod.returnType().type());
        if (StringUtilities.notEmpty(bindingDecorator.value())) {
            keyBuilder = keyBuilder.name(bindingDecorator.value());
        }
        return keyBuilder.build();
    }

    private boolean isClassBinding(final MethodView<?, ?> bindsMethod) {
        final TypeView<?> returnType = bindsMethod.returnType();
        return returnType.is(Class.class) || returnType.isChildOf(TypeView.class);
    }
}
