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
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Standard implementation of {@link ProxyResultValidator} that uses the {@link Introspector} to validate the result
 * type of a method invocation. This will ensure that the result type is compatible with the return type of the method
 * that was invoked, and will throw an {@link IllegalArgumentException} if this is not the case.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class IntrospectionProxyResultValidator implements ProxyResultValidator {

    private final Introspector introspector;

    public IntrospectionProxyResultValidator(final Introspector introspector) {
        this.introspector = introspector;
    }

    @Override
    public Object validateResult(final MethodInvokable source, final Object result) {
        final TypeView<?> returnType = source.toIntrospector().returnType();
        if (returnType.isVoid()) {
            return null;
        }
        else if (returnType.isPrimitive()) {
            if (result == null) {
                return returnType.defaultOrNull();
            }
            else {
                final TypeView<Object> resultView = this.introspector.introspect(result);
                if (resultView.isPrimitive() || resultView.isChildOf(returnType.type())) {
                    return result;
                }
                else throw new IllegalArgumentException("Invalid return type: " + resultView.name() + " for " + source.qualifiedName());
            }
        }
        else if (result == null) {
            return null;
        }
        else {
            final TypeView<Object> resultView = this.introspector.introspect(result);
            if (resultView.isChildOf(returnType.type())) {
                return result;
            }
            else throw new IllegalArgumentException("Invalid return type: " + resultView.name() + " for " + source.qualifiedName());
        }
    }
}
