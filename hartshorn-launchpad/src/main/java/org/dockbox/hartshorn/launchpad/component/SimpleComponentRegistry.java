/*
 * Copyright 2019-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.launchpad.component;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentKeyMatcher;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple implementation of {@link ComponentRegistry} which stores components in a thread-safe
 * list.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
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
