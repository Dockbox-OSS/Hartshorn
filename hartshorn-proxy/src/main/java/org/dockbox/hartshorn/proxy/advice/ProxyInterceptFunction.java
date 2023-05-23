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

/**
 * A function that is invoked by a {@link org.dockbox.hartshorn.proxy.advice.ProxyAdvisor} to handle the interception of
 * a method invocation. The function is expected to return the result of the method invocation, or throw an exception.
 *
 * @param <T> The type of the proxy instance on which the method is invoked.
 *
 * @since 23.1
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ProxyInterceptFunction<T> {

    /**
     * Handles the interception of a method invocation.
     *
     * @return The result of the method invocation.
     * @throws Throwable If an error occurs during the method invocation.
     */
    T handleInterception() throws Throwable;
}
