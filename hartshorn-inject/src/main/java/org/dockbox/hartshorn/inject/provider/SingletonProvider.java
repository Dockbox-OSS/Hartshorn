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

package org.dockbox.hartshorn.inject.provider;

import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.util.ObjectDescriber;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A provider that always returns the same instance. While the instance is available, this
 * provider is not type-aware, as the instance may be {@code null}, or deviate from the
 * binding key.
 *
 * @param <T> the type of the instance
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class SingletonProvider<T> implements NonTypeAwareProvider<T> {

    private final ObjectContainer<T> container;

    public SingletonProvider(T instance) {
        this.container = ComponentObjectContainer.ofSingleton(instance);
    }

    @Override
    public Option<ObjectContainer<T>> provide(ComponentRequestContext requestContext) {
        return Option.of(this.container);
    }

    @Override
    public LifecycleType defaultLifecycle() {
        return LifecycleType.SINGLETON;
    }

    @Override
    public Tristate defaultLazy() {
        return Tristate.FALSE;
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("container", this.container)
                .describe();
    }
}
