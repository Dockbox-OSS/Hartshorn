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

package org.dockbox.hartshorn.inject.binding;

import java.util.Map;
import java.util.TreeMap;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.Provider;

/**
 * The default implementation of the {@link BindingHierarchy} interface. This uses a specified {@link ComponentKey}
 * to identify the binding hierarchy, and stores the bindings in a {@link TreeMap}.
 *
 * @param <C> The type of type to provide.
 *
 * @see BindingHierarchy
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class NativePrunableBindingHierarchy<C> extends AbstractBindingHierarchy<C> implements PrunableBindingHierarchy<C> {

    public NativePrunableBindingHierarchy(ComponentKey<C> key, ApplicationContext applicationContext) {
        super(key, applicationContext);
    }

    @Override
    public boolean prune(int priority) {
        return this.priorityProviders().remove(priority) != null;
    }

    @Override
    public int pruneAbove(int priority) {
        int count = 0;
        for (Map.Entry<Integer, Provider<C>> entry : this.priorityProviders().entrySet()) {
            if (entry.getKey() > priority && this.prune(entry.getKey())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int pruneBelow(int priority) {
        int count = 0;
        for (Map.Entry<Integer, Provider<C>> entry : this.priorityProviders().entrySet()) {
            if (entry.getKey() < priority && this.prune(entry.getKey())) {
                count++;
            }
        }
        return count;
    }
}
