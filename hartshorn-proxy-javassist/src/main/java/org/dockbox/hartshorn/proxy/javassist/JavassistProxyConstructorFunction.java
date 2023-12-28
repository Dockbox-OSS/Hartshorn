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

package org.dockbox.hartshorn.proxy.javassist;

import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.util.ApplicationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

/**
 * A proxy constructor function that uses Javassist to create a proxy instance.
 *
 * @param <T> the type of the proxy
 *
 * @see ProxyFactory
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public class JavassistProxyConstructorFunction<T> implements ProxyConstructorFunction<T> {

    private final Class<T> type;
    private final ProxyFactory factory;
    private final MethodHandler methodHandler;

    public JavassistProxyConstructorFunction(Class<T> type, ProxyFactory factory, MethodHandler methodHandler) {
        this.type = type;
        this.factory = factory;
        this.methodHandler = methodHandler;
    }

    @Override
    public T create() throws ApplicationException {
        try {
            return this.type.cast(this.factory.create(new Class<?>[0], new Object[0], this.methodHandler));
        } catch (RuntimeException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public T create(Constructor<? extends T> constructor, Object[] args) throws ApplicationException {
        try {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            return this.type.cast(this.factory.create(parameterTypes, args, this.methodHandler));
        } catch (RuntimeException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }
}
