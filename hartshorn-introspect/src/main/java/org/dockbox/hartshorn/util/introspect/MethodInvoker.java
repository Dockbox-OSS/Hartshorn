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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A method invoker is used to invoke a method on an instance. The method is provided as a {@link MethodView} instance,
 * which allows for easy access to the method's parameters and return type.
 *
 * <p>Method invokers are expected to be stateless and thread-safe, so they can be cached and reused.
 *
 * @param <T> the return type of the method
 * @param <P> the type of the instance on which the method is invoked
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface MethodInvoker<T, P> {

    /**
     * Invokes the provided method on the provided instance, with the provided arguments.
     *
     * @param method the method to invoke
     * @param instance the instance on which to invoke the method
     * @param args the arguments to pass to the method
     * @return the result of the method invocation
     * @throws Throwable if the method invocation fails
     */
    Option<T> invoke(MethodView<P, T> method, P instance, Object[] args) throws Throwable;
}
