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

package org.dockbox.hartshorn.proxy;

import java.util.concurrent.Callable;

/**
 * A simple functional interface for custom invocation. This is used to provide custom arguments to the
 * invocation. When used in conjunction with {@link MethodInterceptorContext}, this allows for fine-grained
 * control over the invocation.
 *
 * @author Guus Lieben
 * @since 22.2
 */
@FunctionalInterface
public interface CustomInvocation {
    /**
     * Invoke the default method with the given arguments.
     *
     * @param args the arguments to use
     * @return the result of the invocation
     * @throws Exception if the invocation fails
     */
    Object call(Object... args) throws Exception;

    /**
     * Converts this invocation to a {@link Callable}. This is useful for the {@link MethodInterceptorContext}
     * to allow for the invocation to be executed both using the default arguments, and
     * custom arguments.
     *
     * @param args the default arguments to use
     * @return the invocation as a {@link Callable}
     */
    default Callable<Object> toCallable(final Object... args) {
        return () -> this.call(args);
    }
}
