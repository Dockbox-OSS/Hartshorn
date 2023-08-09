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

package org.dockbox.hartshorn.proxy.advice;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.proxy.advice.registry.AdvisorRegistry;
import org.dockbox.hartshorn.proxy.advice.stub.MethodStub;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * An immutable resolver to look up advisors which are attached to a proxy instance. Advisors can be present for
 * specific methods or for all methods of a specific type. Note that the resolver is immutable, and that it is not
 * possible to add advisors to the resolver. Instead, the resolver is created from a {@link AdvisorRegistry} which
 * is mutable.
 *
 * @param <T> the type of the proxy instance
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public interface ProxyAdvisorResolver<T> {

    /**
     * Returns a resolver step for the given method. The method does not have to be a method of the advised type, but
     * it must be a method of a type which is assignable to the advised type. If the method is not present in the
     * registry, the method resolver will still be returned, but it will not resolve any advisors.
     *
     * @param method the method to get a resolver step for
     * @return the resolver step for the given method
     * @param <R> the return type of the method
     */
    <R> MethodAdvisorResolver<T, R> method(MethodView<T, R> method);

    /**
     * Returns a resolver step for the given method. The method does not have to be a method of the advised type, but
     * it must be a method of a type which is assignable to the advised type. If the method is not present in the
     * registry, the method resolver will still be returned, but it will not resolve any advisors.
     *
     * @param method the method to get a resolver step for
     * @return the resolver step for the given method
     */
    MethodAdvisorResolver<T, Object> method(Method method);

    /**
     * Returns a resolver step for the given type. If the type is not present in the registry, the type resolver will
     * still be returned, but it will not resolve any advisors.
     *
     * @param type the type to get a resolver step for
     * @return the resolver step for the given type
     * @param <S> the type of the type
     */
    <S> TypeAdvisorResolver<S> type(Class<S> type);

    /**
     * Returns a resolver step for the given type. If the type is not present in the registry, the type resolver will
     * still be returned, but it will not resolve any advisors.
     *
     * @param type the type to get a resolver step for
     * @return the resolver step for the given type
     * @param <S> the type of the type
     */
    <S> TypeAdvisorResolver<S> type(TypeView<S> type);

    /**
     * Returns the type resolver for the advised type. As it is possible to create a proxy without advisors for the
     * advised type, the type resolver may not resolve any advisors.
     *
     * @return the type resolver for the advised type
     */
    TypeAdvisorResolver<T> type();

    /**
     * Returns the default stub for the advised type. The default stub is used when no advisors are present for a
     * method. The default stub is guaranteed to be present, and will never return {@code null}.
     *
     * @return the default stub for the advised type
     */
    @NonNull
    Supplier<MethodStub<T>> defaultStub();
}
