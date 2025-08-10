package org.dockbox.sample.processing.bindings;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.processing.HierarchicalBinderPostProcessor;
import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.sample.processing.HelloWorldSupplier;
import org.dockbox.sample.processing.SimpleHelloWorldSupplier;

public class CustomBindingPostProcessor implements HierarchicalBinderPostProcessor {

    @Override
    public void process(InjectionCapableApplication application, Scope scope, HierarchicalBinder binder) {
        binder.bind(HelloWorldSupplier.class).to(SimpleHelloWorldSupplier.class);
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
