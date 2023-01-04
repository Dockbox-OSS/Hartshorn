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
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;

import java.lang.reflect.InvocationTargetException;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class JavassistProxyConstructorFunction<T> implements ProxyConstructorFunction<T> {

    private final Class<T> type;
    private final ProxyFactory factory;
    private final MethodHandler methodHandler;

    public JavassistProxyConstructorFunction(final Class<T> type, final ProxyFactory factory, final MethodHandler methodHandler) {
        this.type = type;
        this.factory = factory;
        this.methodHandler = methodHandler;
    }

    @Override
    public T create() throws ApplicationException {
        try {
            return this.type.cast(this.factory.create(new Class<?>[0], new Object[0], this.methodHandler));
        } catch (final RuntimeException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public T create(final ConstructorView<T> constructor, final Object[] args) throws ApplicationException {
        try {
            final Class<?>[] parameterTypes = constructor.constructor().getParameterTypes();
            return this.type.cast(this.factory.create(parameterTypes, args, this.methodHandler));
        } catch (final RuntimeException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }
}
