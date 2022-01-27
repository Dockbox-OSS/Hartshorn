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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import lombok.Getter;

/**
 * Context type for {@link org.dockbox.hartshorn.core.proxy.ProxyHandler}s to use when storing backing implementation
 * instances. Each backing implementation is stored based on its class, for example an instance of
 * {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessorImpl} will be stored under the key
 * {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessor}.
 */
@Getter
@AutoCreating
public class BackingImplementationContext extends DefaultContext {

    private final Map<Class<?>, Object> implementations = new ConcurrentHashMap<>();

    /**
     * Gets the backing implementation instance for the given class, if it exists.
     * @param type The class to get the backing implementation instance for.
     * @param <P> The type of the backing implementation instance.
     * @return The backing implementation instance for the given class, if it exists.
     */
    public <P> Exceptional<P> get(final Class<P> type) {
        return Exceptional.of(() -> (P) this.implementations.get(type));
    }

    /**
     * Gets the backing implementation instance for the given class, if it exists. If it doesn't exist, it will be
     * created using the given function.
     * @param key The class to get the backing implementation instance for.
     * @param mappingFunction The function to create the backing implementation instance if it doesn't exist.
     * @param <P> The type of the backing implementation instance.
     * @return The backing implementation instance for the given class.
     */
    public <P> P computeIfAbsent(final Class<P> key, @NonNull final Function<? super Class<P>, P> mappingFunction) {
        return (P) this.implementations.computeIfAbsent(key, (Function<? super Class<?>, ?>) mappingFunction);
    }
}
