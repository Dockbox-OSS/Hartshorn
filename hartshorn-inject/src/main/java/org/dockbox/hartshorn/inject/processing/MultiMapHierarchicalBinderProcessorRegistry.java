package org.dockbox.hartshorn.inject.processing;

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
    public void unregister(HierarchicalBinderPostProcessor processor) {
        this.globalProcessors.remove(processor.priority(), processor);
    }

    @Override
    public boolean isRegistered(Class<? extends HierarchicalBinderPostProcessor> componentProcessor) {
        return this.globalProcessors.allValues().stream()
                .anyMatch(processor -> processor.getClass().equals(componentProcessor));
    }

    @Override
    public <T extends HierarchicalBinderPostProcessor> Option<T> lookup(Class<T> componentProcessor) {
        return Option.of(this.globalProcessors.allValues().stream()
                .filter(processor -> processor.getClass().equals(componentProcessor))
                .map(componentProcessor::cast)
                .findFirst());
    }

    @Override
    public MultiMap<Integer, HierarchicalBinderPostProcessor> processors() {
        return this.globalProcessors;
    }
}
