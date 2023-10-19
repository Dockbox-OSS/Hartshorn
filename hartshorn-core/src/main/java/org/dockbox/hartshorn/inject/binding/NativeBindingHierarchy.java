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

package org.dockbox.hartshorn.inject.binding;

import java.util.TreeMap;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;

/**
 * The default implementation of the {@link BindingHierarchy} interface. This uses a specified {@link ComponentKey}
 * to identify the binding hierarchy, and stores the bindings in a {@link TreeMap}.
 *
 * @param <C> The type of type to provide.
 * @author Guus Lieben
 * @since 0.4.3
 * @see BindingHierarchy
 */
public class NativeBindingHierarchy<C> extends AbstractPrunableBindingHierarchy<C> {

    private final ComponentKey<C> key;
    private final ApplicationContext applicationContext;

    public NativeBindingHierarchy(ComponentKey<C> key, ApplicationContext applicationContext) {
        this.key = key;
        this.applicationContext = applicationContext;
    }

    @Override
    public ComponentKey<C> key() {
        return this.key;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
