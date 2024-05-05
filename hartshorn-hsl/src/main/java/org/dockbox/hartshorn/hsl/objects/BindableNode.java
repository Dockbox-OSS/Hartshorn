/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.hsl.objects;

/**
 * Represents a node that can be bound to an instance. This can be used to bind properties
 * to specific holders or executors.
 *
 * @param <T> the type of the bound object
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface BindableNode<T> {

    /**
     * Creates a new instance of the node, and binds it to the given instance. The returned
     * instance may be the same as the instance on which this method is invoked, if the current
     * node is already bound to the given instance.
     *
     * @param instance the instance to bind to
     * @return the bound instance
     */
    T bind(InstanceReference instance);

    /**
     * Returns the instance to which this node is bound. If this node is not bound, {@code null}
     * is returned.
     *
     * @return the bound instance
     */
    InstanceReference bound();
}
