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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.ComponentKey;

/**
 * A binder that is capable of binding aliases. This allows for the binding of components to
 * multiple keys, which can be useful when a component is defined in multiple modules, and you want
 * to reference the same component using different keys.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface AliasCapableBinder extends Binder {

    @Override
    default <C> AliasBindingFunction<C> bind(Class<C> type) {
        return (AliasBindingFunction<C>) Binder.super.bind(type);
    }

    @Override
    <C> AliasBindingFunction<C> bind(ComponentKey<C> key);
}
