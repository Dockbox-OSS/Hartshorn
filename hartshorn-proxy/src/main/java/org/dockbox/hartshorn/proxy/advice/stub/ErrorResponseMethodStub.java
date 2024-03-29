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

package org.dockbox.hartshorn.proxy.advice.stub;

/**
 * A {@link MethodStub} that throws an {@link AbstractMethodError} when invoked. This is used to indicate that a method
 * is abstract, and that no advisor was found for it.
 *
 * @param <T> The type of the proxy instance
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class ErrorResponseMethodStub<T> implements MethodStub<T> {

    @Override
    public Object invoke(MethodStubContext<T> stubContext) {
        Class<T> targetClass = stubContext.manager().targetClass();
        String className = targetClass == null ? "" : targetClass.getSimpleName() + ".";
        String name = stubContext.target().name();
        throw new AbstractMethodError("Cannot invoke method '" + className + name + "' because it is abstract. This type is proxied, but no advisor was found for the method.");
    }
}
