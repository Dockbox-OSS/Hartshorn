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

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;

/**
 * A resolver to look up advisors for a given method. This resolver can be used by {@link ProxyMethodInterceptHandler}s
 * to determine which advisors should be applied to a given method. This resolver is inherently immutable, and can not
 * be used to add or remove advisors.
 *
 * @param <T> The type of the proxy instance
 * @param <R> The type of the return value of the method
 *
 * @since 23.1
 * @author Guus Lieben
 */
public interface MethodAdvisorResolver<T, R> {

    /**
     * Returns the delegate instance to which the method invocation should be delegated. If no delegate is available,
     * an empty {@link Option} is returned.
     *
     * @return The delegate instance, if available
     */
    Option<T> delegate();

    /**
     * Returns the {@link MethodInterceptor} that should be applied to the method invocation. If no interceptor is
     * available, an empty {@link Option} is returned.
     *
     * @return The interceptor, if available
     */
    Option<MethodInterceptor<T, R>> interceptor();

    /**
     * Returns the {@link MethodWrapper}s that should be applied to the method invocation. If no wrappers are available,
     * an empty {@link Collection} is returned.
     *
     * @return The wrappers, if available
     */
    Collection<MethodWrapper<T>> wrappers();
}
