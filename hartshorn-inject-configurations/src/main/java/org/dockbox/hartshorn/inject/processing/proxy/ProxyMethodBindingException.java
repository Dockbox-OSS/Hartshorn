/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.inject.processing.proxy;

import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.lang.reflect.Method;

/**
 * Thrown by {@link MethodInterceptorPostProcessor} when a method does meet compatibility
 * requirements for binding to a proxy, but does not pass the preconditions. For example, if the
 * method is annotated with a specific annotation that does not allow the method to return values,
 * yet the method does have a non-void return signature.
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
public class ProxyMethodBindingException extends RuntimeException {

    public ProxyMethodBindingException(MethodProxyContext<?> context) {
        this(context.method());
    }

    public ProxyMethodBindingException(MethodView<?, ?> method) {
        super("Could not bind proxy to " + method.name() + " because preconditions failed");
    }

    public ProxyMethodBindingException(Method method) {
        super("Could not bind proxy to " + method.getName() + " because preconditions failed");
    }
}
