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

package org.dockbox.hartshorn.core.inject;

import org.dockbox.hartshorn.core.Key;

import java.util.function.Supplier;

public interface DelegatedBinder extends Binder {

    @Override
    default <C> void bind(final Key<C> contract, final Supplier<C> supplier) {
        this.binder().bind(contract, supplier);
    }

    Binder binder();

    @Override
    default <C, T extends C> void bind(final Key<C> contract, final Class<? extends T> implementation) {
        this.binder().bind(contract, implementation);
    }

    @Override
    default <C, T extends C> void bind(final Key<C> contract, final T instance) {
        this.binder().bind(contract, instance);
    }

    @Override
    default <T, C extends T> void singleton(final Key<T> key, final C instance) {
        this.binder().singleton(key, instance);
    }
}
