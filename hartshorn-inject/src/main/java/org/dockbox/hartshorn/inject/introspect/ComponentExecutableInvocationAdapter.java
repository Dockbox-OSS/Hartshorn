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

package org.dockbox.hartshorn.inject.introspect;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Adapter interface for invoking element views within an application context. This allows all elements
 * to be invoked and/or populated using the application context, without the need for manually looking
 * up dependencies.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ComponentExecutableInvocationAdapter extends Context {

    /**
     * Returns a new {@link ComponentExecutableInvocationAdapter} instance that is scoped to the given scope.
     *
     * @param scope the scope to scope the context to
     * @return a new {@link ComponentExecutableInvocationAdapter} instance scoped to the given scope
     */
    ComponentExecutableInvocationAdapter scope(Scope scope);

    /**
     * Returns a new {@link ComponentExecutableInvocationAdapter} instance that is bound to the given
     * {@link ComponentRequestContext}.
     *
     * @param componentRequestContext the component request context to bind to
     * @return a new {@link ComponentExecutableInvocationAdapter} instance bound to the given component request context
     */
    ComponentExecutableInvocationAdapter requestContext(ComponentRequestContext componentRequestContext);

    /**
     * Creates an instance of type {@link T} using the given constructor view.
     *
     * @param constructor the constructor view to use for creating the instance
     * @param <T> the type of the instance to create
     *
     * @return an {@link Option} containing the created instance, or empty if the creation failed
     *
     * @throws Throwable if an error occurs during the creation of the instance
     */
    <T> Option<T> create(ConstructorView<T> constructor) throws Throwable;

    /**
     * Loads the parameters for the given executable element view. This method is typically used to
     * retrieve the parameters that should be passed to the executable element when it is invoked.
     *
     * <p>In most cases, it is preferred to use {@link #invoke(MethodView, Object)} or {@link
     * #create(ConstructorView)} directly, as these methods will automatically load the parameters for
     * you. However, in cases where manual parameter loading is required, this method can be used.
     *
     * @param element the executable element view for which to load the parameters
     *
     * @return an array of objects representing the parameters to be passed to the executable element
     */
    Object[] loadParameters(ExecutableElementView<?> element);

    /**
     * Invokes a method represented by the given {@link MethodView} on the provided instance. This method
     * retrieves the parameters for the method from the method view, and invokes the method with these
     * parameters.
     *
     * @param method the method view representing the method to invoke
     * @param instance the instance on which to invoke the method
     * @param <P> the type of the parameters for the method
     * @param <R> the return type of the method
     *
     * @return an {@link Option} containing the result of the method invocation, or empty if the invocation yielded no
     * result (e.g. if the method returns void, or simply returned null)
     *
     * @throws Throwable if an error occurs during the invocation of the method
     */
    <P, R> Option<R> invoke(MethodView<P, R> method, P instance) throws Throwable;

    /**
     * Invokes a static method represented by the given {@link MethodView}. This method does not
     * require an instance of the class, as it is a static method. The parameters for the method
     * are loaded from the method view, and the method is invoked with these parameters.
     *
     * @param method the method view representing the static method to invoke
     * @param <P> the type of the parameters for the static method
     * @param <R> the return type of the static method
     *
     * @return an {@link Option} containing the result of the method invocation, or empty if the invocation yielded no
     * result (e.g. if the method returns void, or simply returned null)
     *
     * @throws Throwable if an error occurs during the invocation of the static method
     */
    <P, R> Option<R> invokeStatic(MethodView<P, R> method) throws Throwable;
}
