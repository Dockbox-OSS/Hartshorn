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

public class ProviderContext<T> {

    private final Key<T> key;
    private final boolean singleton;
    private final int priority;
    private final Supplier<T> provider;
    private final boolean lazy;

    public ProviderContext(final Key<T> key, final boolean singleton, final int priority, final Supplier<T> provider, final boolean lazy) {
        this.key = key;
        this.singleton = singleton;
        this.priority = priority;
        this.provider = provider;
        this.lazy = lazy;
    }

    public Key<T> key() {
        return this.key;
    }

    public boolean singleton() {
        return this.singleton;
    }

    public int priority() {
        return this.priority;
    }

    public Supplier<T> provider() {
        return this.provider;
    }

    public boolean lazy() {
        return this.lazy;
    }
}
