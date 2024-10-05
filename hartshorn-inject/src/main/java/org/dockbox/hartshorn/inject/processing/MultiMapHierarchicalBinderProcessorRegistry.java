package org.dockbox.hartshorn.inject.processing;

import java.util.Map;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.collections.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

public class MultiMapHierarchicalBinderProcessorRegistry implements HierarchicalBinderProcessorRegistry {

    private final MultiMap<Integer, HierarchicalBinderPostProcessor> globalProcessors = new ConcurrentSetTreeMultiMap<>();

    @Override
    public void register(HierarchicalBinderPostProcessor processor) {
        this.globalProcessors.put(processor.priority(), processor);
    }

    @Override
    public void register(ScopeKey scope, HierarchicalBinderPostProcessor processor) {
        // TODO: #1113 Implement scoped registration
    }

    @Override
    public void unregister(HierarchicalBinderPostProcessor processor) {
        this.globalProcessors.remove(processor.priority(), processor);
    }

    @Override
    public void unregister(ScopeKey scope, HierarchicalBinderPostProcessor processor) {
        // TODO: #1113 Implement scoped registration
    }

    @Override
    public boolean isRegistered(Class<? extends HierarchicalBinderPostProcessor> componentProcessor) {
        return this.globalProcessors.allValues().stream()
                .anyMatch(processor -> processor.getClass().equals(componentProcessor));
    }

    @Override
    public boolean isRegistered(ScopeKey scope, Class<? extends HierarchicalBinderPostProcessor> componentProcessor) {
        // TODO: #1113 Implement scoped registration
        return false;
    }

    @Override
    public <T extends HierarchicalBinderPostProcessor> Option<T> lookup(Class<T> componentProcessor) {
        return Option.of(this.globalProcessors.allValues().stream()
                .filter(processor -> processor.getClass().equals(componentProcessor))
                .map(componentProcessor::cast)
                .findFirst());
    }

    @Override
    public <T extends HierarchicalBinderPostProcessor> Option<T> lookup(ScopeKey scope, Class<T> componentProcessor) {
        // TODO: #1113 Implement scoped registration
        return null;
    }

    @Override
    public MultiMap<Integer, HierarchicalBinderPostProcessor> processors() {
        return this.globalProcessors;
    }

    @Override
    public MultiMap<Integer, HierarchicalBinderPostProcessor> processors(ScopeKey scope) {
        // TODO: #1113 Implement scoped registration
        return null;
    }

    @Override
    public void process(InjectionCapableApplication application, HierarchicalBinder binder) {
        MultiMap<Integer, HierarchicalBinderPostProcessor> processors = this.processors();
        for (Integer priority : processors.keySet()) {
            for(HierarchicalBinderPostProcessor processor : processors.get(priority)) {
                processor.process(application, binder);
            }
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
