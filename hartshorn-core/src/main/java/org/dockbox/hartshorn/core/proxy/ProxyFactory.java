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

package org.dockbox.hartshorn.core.proxy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.TypeMap;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.lang.reflect.Method;
import java.util.Map;

public interface ProxyFactory<T, F extends ProxyFactory<T, F>> {

    F delegate(T delegate);

    <S> F delegate(Class<S> type, S delegate);

    F delegate(MethodContext<?, T> method, T delegate);

    F delegate(Method method, T delegate);

    F intercept(MethodContext<?, T> method, MethodInterceptor<T> interceptor);

    F intercept(Method method, MethodInterceptor<T> interceptor);

    F intercept(MethodContext<?, T> method, MethodWrapper<T> wrapper);

    F intercept(Method method, MethodWrapper<T> wrapper);

    Exceptional<T> proxy() throws ApplicationException;

    Class<T> type();

    @Nullable
    T typeDelegate();

    Map<Method, Object> delegates();

    Map<Method, MethodInterceptor<T>> interceptors();

    MultiMap<Method, MethodWrapper<T>> wrappers();

    TypeMap<Object> typeDelegates();
}
