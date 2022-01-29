/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.binding.BindingHierarchy;
import org.dockbox.hartshorn.core.inject.Binder;

import java.util.function.Consumer;

public interface HierarchicalComponentProvider extends ComponentProvider, Binder {

    <C> void inHierarchy(final Key<C> key, final Consumer<BindingHierarchy<C>> consumer);

    <T> BindingHierarchy<T> hierarchy(Key<T> key);
}
