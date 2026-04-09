package org.dockbox.hartshorn.launchpad.component;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentKeyMatcher;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleComponentRegistry implements ComponentRegistry {

    private final List<ComponentContainer<?>> containers = new CopyOnWriteArrayList<>();
    private final ComponentKeyMatcher matcher;

    public SimpleComponentRegistry(ComponentKeyMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public Collection<ComponentContainer<?>> containers() {
        return List.copyOf(containers);
    }

    @Override
    public Option<ComponentContainer<?>> container(Class<?> type) {
        return this.container(ComponentKey.of(type));
    }

    @Override
    public Option<ComponentContainer<?>> container(ComponentKey<?> key) {
        return Option.of(containers().stream()
                .filter(container -> matcher.matches(key, ComponentKey.of(container.type())))
                .findFirst());
    }

    @Override
    public boolean addCustomContainer(ComponentContainer<?> container) {
        return containers.add(container);
    }
}
