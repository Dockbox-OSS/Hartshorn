package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.DefaultBindingConfigurer;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.processing.HierarchicalBinderPostProcessor;
import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.inject.scope.Scope;

public record BindingConfigurerBinderPostProcessor(
        DefaultBindingConfigurer configurer
) implements HierarchicalBinderPostProcessor {

    @Override
    public void process(InjectionCapableApplication application, Scope scope, HierarchicalBinder binder) {
        this.configurer.configure(binder);
    }

    @Override
    public int priority() {
        return ProcessingPriority.HIGH_PRECEDENCE;
    }
}
