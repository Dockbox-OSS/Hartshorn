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

package org.dockbox.hartshorn.context;

import org.dockbox.hartshorn.util.Result;

import java.util.List;

@FunctionalInterface
public interface DelegatingContext<D extends Context> extends Context {

    @Override
    default <C extends Context> void add(final C context) {
        this.get().add(context);
    }

    D get();

    @Override
    default <N extends NamedContext> void add(final N context) {
        this.get().add(context);
    }

    @Override
    default <C extends Context> void add(final String name, final C context) {
        this.get().add(name, context);
    }

    @Override
    default Result<Context> first(final String name) {
        return this.get().first(name);
    }

    @Override
    default <N extends Context> Result<N> first(final String name, final Class<N> context) {
        return this.get().first(name, context);
    }

    @Override
    default <C extends Context> List<C> all(final Class<C> context) {
        return this.get().all(context);
    }

    @Override
    default List<Context> all(final String name) {
        return this.get().all(name);
    }

    @Override
    default <N extends Context> List<N> all(final String name, final Class<N> context) {
        return this.get().all(name, context);
    }
}
