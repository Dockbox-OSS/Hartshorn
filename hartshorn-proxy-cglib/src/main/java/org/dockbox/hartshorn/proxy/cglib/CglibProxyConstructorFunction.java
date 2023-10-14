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

package org.dockbox.hartshorn.proxy.cglib;

import java.lang.reflect.Constructor;

import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;

import net.sf.cglib.proxy.Enhancer;

/**
 * @deprecated CGLib is not actively maintained, and commonly causes issues with Java 9+.
 *             It is recommended to use Javassist instead, through the
 *             {@code org.dockbox.hartshorn.proxy.javassist.JavassistProxyConstructorFunction}.
 */
@Deprecated(since = "0.4.13")
public class CglibProxyConstructorFunction<T> implements ProxyConstructorFunction<T> {

    private final Class<T> type;
    private final Enhancer enhancer;

    public CglibProxyConstructorFunction(final Class<T> type, final Enhancer enhancer) {
        this.type = type;
        this.enhancer = enhancer;
    }

    @Override
    public T create() {
        final Object instance = this.enhancer.create();
        return this.type.cast(instance);
    }

    @Override
    public T create(final Constructor<? extends T> constructor, final Object[] args) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object instance = this.enhancer.create(parameterTypes, args);
        return this.type.cast(instance);
    }
}
