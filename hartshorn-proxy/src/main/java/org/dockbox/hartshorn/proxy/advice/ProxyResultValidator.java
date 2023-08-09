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

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;

/**
 * A validator that can be used to validate the result of a method invocation. This validator is invoked by a
 * {@link org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor} after the method invocation has been
 * performed. The validator is expected to return the valid result of the method invocation, or throw an exception.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ProxyResultValidator {

    /**
     * Validates the result of a method invocation. If the result is valid, or can be transformed into a valid result,
     * the result is returned. If the result is not valid, an exception is thrown.
     *
     * @param source the method that is invoked
     * @param result the result of the method invocation
     * @return the valid result of the method invocation
     */
    Object validateResult(final MethodInvokable source, final Object result);
}
