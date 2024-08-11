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

package org.dockbox.hartshorn.proxy.advice.intercept;

import java.util.Objects;

/**
 * A simple functional interface that can be used to intercept method calls. The interceptor is called in series
 * with any other interceptors that have been added to the proxy. The interceptor can modify the method call, or
 * throw an exception to prevent the method call from being executed. The interceptor can also return a value to
 * replace the return value of the method call.
 *
 * <p>A method interceptor is typically unaware of the exact method definition, and can therefore be used to intercept
 * any method call. However, it is not against convention to specifically target a particular method. Typically
 * however, the interceptor will be written to target a specific method signature, for example based on the presence
 * of a specific annotation.
 *
 * @param <T> the type of the proxy. This is only a utility type that is used to allow the interceptor to be
 *           generic.
 *
 * @param <R> the return type of the method call
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface MethodInterceptor<T, R> {

    /**
     * Intercepts a method call. This method is called in series with any other interceptors that have been added to
     * the proxy.
     *
     * @param context the method call context. The context contains the method call, the proxy instance, as well as
     *               the return value of the previous interceptor and utility callables that can be used to call the
     *               underlying method.
     * @return the return value of the method call. This can be modified by the interceptor.
     * @throws Throwable if the interceptor fails to complete. This will cancel any further interceptors and the
     *                  method call and throw the exception to the caller.
     */
    R intercept(MethodInterceptorContext<T, R> context) throws Throwable;

    /**
     * A utility method to chain the given interceptor after the current interceptor. This method is typically used
     * to construct a linked series of interceptors.
     *
     * @param after the interceptor to chain after the current interceptor.
     * @return the chained interceptor.
     */
    default MethodInterceptor<T, R> andThen(MethodInterceptor<T, R> after) {
        Objects.requireNonNull(after);
        return context -> {
            R previous = this.intercept(context);
            return after.intercept(MethodInterceptorContext.copyWithResult(context, previous));
        };
    }
}
