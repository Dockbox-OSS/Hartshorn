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

package org.dockbox.hartshorn.proxy.advice.stub;

/**
 * A {@link MethodStub} is a functional interface that can be used to stub a method invocation. It is used by the
 * {@link org.dockbox.hartshorn.proxy.ProxyFactory} to intercept method invocations if no explicit advisor was
 * registered for the method.
 *
 * @param <T> The type of the proxy instance as defined by the owning {@link org.dockbox.hartshorn.proxy.ProxyFactory}
 *
 * @since 0.4.9
 * @author Guus Lieben
 */
@FunctionalInterface
public interface MethodStub<T> {

    /**
     * Invokes the stubbed method. The {@link MethodStubContext} provides access to the method arguments, as well as
     * the proxy instance on which the method was invoked. The return value of this method is returned to the caller of
     * the stubbed method. If the stubbed method is void, the return value of this method is ignored.
     *
     * @param stubContext The context of the stubbed method invocation
     * @return The return value of the stubbed method invocation
     * @throws Throwable Any exception that is thrown by the stubbed method invocation
     */
    Object invoke(MethodStubContext<T> stubContext) throws Throwable;

}
