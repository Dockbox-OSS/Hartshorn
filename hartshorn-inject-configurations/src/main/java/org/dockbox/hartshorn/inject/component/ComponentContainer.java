/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.inject.component;

import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Comparator;

/**
 * Represents a container for a component. The metadata provided by this container is used to register the component with
 * the IoC container.
 *
 * @param <T> the type of the component
 *
 * @see ComponentRegistry
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public interface ComponentContainer<T> {

    /**
     * A comparator that compares component containers by their id.
     */
    Comparator<ComponentContainer<?>> COMPARE_BY_ID = Comparator.comparing(ComponentContainer::id);

    /**
     * The unique identifier of this component.
     *
     * @return the component id
     */
    String id();

    /**
     * The (human-readable) name of this component.
     *
     * @return the component name
     */
    String name();

    /**
     * The type of the component contained within this container.
     *
     * @return the component type
     */
    TypeView<T> type();

    /**
     * The lifecycle type of this component.
     *
     * @return the lifecycle type
     */
    LifecycleType lifecycle();

    /**
     * Whether this component is to be lazily initialized.
     *
     * @return true if the component is lazy, false otherwise
     */
    boolean lazy();

    /**
     * Whether this component permits proxying.
     *
     * @return true if proxying is permitted, false otherwise
     */
    boolean permitsProxying();

    /**
     * Whether this component permits processing by component processors.
     *
     * @return true if processing is permitted, false otherwise
     */
    boolean permitsProcessing();
}
