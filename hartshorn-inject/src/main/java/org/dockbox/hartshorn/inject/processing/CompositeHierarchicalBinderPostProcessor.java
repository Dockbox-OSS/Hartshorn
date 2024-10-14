package org.dockbox.hartshorn.inject.processing;

import java.util.function.Supplier;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.collections.MultiMap;

public class CompositeHierarchicalBinderPostProcessor implements HierarchicalBinderPostProcessor {

    private final Supplier<MultiMap<Integer, HierarchicalBinderPostProcessor>> postProcessors;

    public CompositeHierarchicalBinderPostProcessor(Supplier<MultiMap<Integer, HierarchicalBinderPostProcessor>> postProcessors) {
        this.postProcessors = postProcessors;
    }

    @Override
    public void process(InjectionCapableApplication application, Scope scope, HierarchicalBinder binder) {
        MultiMap<Integer, HierarchicalBinderPostProcessor> processors = this.postProcessors.get();
        for (Integer priority : processors.keySet()) {
            for(HierarchicalBinderPostProcessor processor : processors.get(priority)) {
                processor.process(application, scope, binder);
            }
        }
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
