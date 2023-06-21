package org.dockbox.hartshorn.inject.strategy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.InstallTo;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.AutoConfiguringDependencyContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

import jakarta.inject.Singleton;

public class MethodInstanceBindingStrategy implements BindingStrategy {

    @Override
    public <T> boolean canHandle(final BindingStrategyContext<T> context) {
        return context instanceof MethodAwareBindingStrategyContext<T> methodAwareBindingStrategyContext
                && methodAwareBindingStrategyContext.method().annotations().has(Binds.class);
    }

    @Override
    public <T> DependencyContext<?> handle(final BindingStrategyContext<T> context) {
        final MethodAwareBindingStrategyContext<T> strategyContext = (MethodAwareBindingStrategyContext<T>) context;
        final Binds bindingDecorator = strategyContext.method().annotations()
                .get(Binds.class)
                .orElseThrow(() -> new IllegalStateException("Method is not annotated with @Binds"));

        return this.resolveInstanceBinding(strategyContext.method(), bindingDecorator, context.applicationContext());
    }

    @Override
    public BindingStrategyPriority priority() {
        return BindingStrategyPriority.LOW;
    }

    private <T> DependencyContext<T> resolveInstanceBinding(final MethodView<?, T> bindsMethod, final Binds bindingDecorator, final ApplicationContext applicationContext) {
        final ComponentKey<T> componentKey = this.constructInstanceComponentKey(bindsMethod, bindingDecorator);
        final Set<ComponentKey<?>> dependencies = DependencyResolverUtils.resolveDependencies(bindsMethod);
        final Class<? extends Scope> scope = this.resolveComponentScope(bindsMethod);
        final int priority = bindingDecorator.priority();

        final ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(applicationContext);
        final CheckedSupplier<T> supplier = () -> contextAdapter.load(bindsMethod)
                .mapError(error -> new ComponentInitializationException("Failed to obtain instance for " + bindsMethod.qualifiedName(), error))
                .rethrow()
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

    private <T> ComponentKey<T> constructInstanceComponentKey(final MethodView<?, T> bindsMethod, final Binds bindingDecorator) {
        ComponentKey.Builder<T> keyBuilder = ComponentKey.builder(bindsMethod.returnType().type());
        if (StringUtilities.notEmpty(bindingDecorator.value())) {
            keyBuilder = keyBuilder.name(bindingDecorator.value());
        }
        return keyBuilder.build();
    }
}
