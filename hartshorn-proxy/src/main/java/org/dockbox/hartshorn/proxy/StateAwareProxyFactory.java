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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A specific {@link ProxyFactory} that is aware of its own state, and exposes it to the outside world.
 *
 * @param <T> the type of the proxy
 * @author Guus Lieben
 * @since 22.2
 */
public interface StateAwareProxyFactory<T> extends ProxyFactory<T> {
    /**
     * Sets whether the current factory should continue tracking changes. If set to false, the factory will not track
     * changes.
     *
     * @param trackState whether the factory should track changes
     * @return the current factory
     */
    StateAwareProxyFactory<T> trackState(boolean trackState);

    /**
     * Returns whether the current factory was modified since its creation. If {@link #trackState(boolean)}
     * was previously set to {@code false}, this method will always return {@code false}. Otherwise, it will return
     * {@code true} if the factory was modified since its creation. If the factory was not modified since its
     * creation, it will return {@code false}.
     *
     * @return whether the factory was modified since its creation
     */
    boolean modified();

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> delegateAbstract(T delegate);

    /**
     * @inheritDoc
     */
    @Override
    <S> StateAwareProxyFactory<T> delegate(Class<S> type, S delegate);

    /**
     * @inheritDoc
     */
    @Override
    <S> StateAwareProxyFactory<T> delegateAbstract(Class<S> type, S delegate);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> delegate(MethodView<T, ?> method, T delegate);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> delegate(Method method, T delegate);

    /**
     * @inheritDoc
     */
    @Override
    <R> StateAwareProxyFactory<T> intercept(MethodView<T, R> method, MethodInterceptor<T, R> interceptor);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> intercept(Method method, MethodInterceptor<T, ?> interceptor);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> wrapAround(MethodView<T, ?> method, MethodWrapper<T> wrapper);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> wrapAround(Method method, MethodWrapper<T> wrapper);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> wrapAround(Method method, Consumer<MethodWrapperFactory<T>> wrapper);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> wrapAround(MethodView<T, ?> method, Consumer<MethodWrapperFactory<T>> wrapper);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> implement(Class<?>... interfaces);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> defaultStub(MethodStub<T> stub);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> defaultStub(Supplier<MethodStub<T>> stub);

    /**
     * @inheritDoc
     */
    @Override
    StateAwareProxyFactory<T> delegate(T delegate);
}
