/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.inject;

import java.util.Iterator;

import org.dockbox.hartshorn.inject.ConstructorDiscoveryList.DiscoveredComponent;
import org.dockbox.hartshorn.util.ApplicationException;

public class CyclicComponentException extends ApplicationException {

    private static final String CYCLE_TOP     = " ┌───┐\n";
    private static final String CYCLE_NODE    = " ↑  %s\n";
    private static final String CYCLE_BINDING = " |   ↓   ↳ Implemented by %s\n";
    private static final String CYCLE_PATH    = " |   ↓\n";
    private static final String CYCLE_BOTTOM  = " └───┘";

    public CyclicComponentException(ConstructorDiscoveryList path) {
        super(formatMessage(path));
    }

    /**
     * Formats the given path into a human-readable string. A simple example:
     * <pre>{@code
     * Cyclic dependency detected in constructor of ConcreteComponentA. Complete dependency path:
     *  ┌───┐
     *  ↑  com.sample.ConcreteComponentA
     *  |   ↓
     *  ↑  com.sample.ConcreteComponentB
     *  |   ↓
     *  ↑  com.sample.AbstractComponentC
     *  |   ↓   ↳ Implemented by com.sample.ConcreteComponentC
     *  └───┘
     *  }</pre>
     * @param path the path to format
     * @return the formatted string
     */
    private static String formatMessage(ConstructorDiscoveryList path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }

        StringBuilder builder = new StringBuilder();
        builder.append(CYCLE_TOP);

        for(Iterator<DiscoveredComponent> iterator = path.iterator(); iterator.hasNext(); ) {
            DiscoveredComponent node = iterator.next();
            builder.append(CYCLE_NODE.formatted(node.node().qualifiedName()));
            if(node.fromBinding()) {
                builder.append(CYCLE_BINDING.formatted(node.actualType().qualifiedName()));
            }
            else if (iterator.hasNext()) {
                builder.append(CYCLE_PATH);
            }
        }
        builder.append(CYCLE_BOTTOM);

        DiscoveredComponent origin = path.getOrigin();
        String typeName = origin.fromBinding() ? origin.actualType().name() : origin.node().qualifiedName();
        return "Cyclic dependency detected in constructor of %s. Complete dependency path:\n%s".formatted(typeName, builder.toString());
    }
}
