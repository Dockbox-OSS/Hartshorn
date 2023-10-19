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

package org.dockbox.hartshorn.util.introspect.view;

import org.dockbox.hartshorn.util.introspect.ElementModifiersIntrospector;
import org.dockbox.hartshorn.util.introspect.IllegalIntrospectionException;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents a view of a {@link Method} instance. This view provides access various properties of
 * the method, such as its name, return type, and parameter types. It also provides the ability to
 * invoke the method.
 *
 * @param <Parent> the type of the method's parent
 * @param <ReturnType> the type of the method's return type
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
public interface MethodView<Parent, ReturnType> extends ExecutableElementView<Parent>, GenericTypeView<ReturnType> {

    /**
     * Returns the {@link Method} instance represented by this view, if it exists.
     *
     * @return the method instance, if it exists
     */
    Option<Method> method();

    /**
     * Invokes the method represented by this view on the given instance with the given arguments. If
     * the method is static, the instance may be {@code null}. Any exceptions thrown by the method
     * will be caught and returned as a failed {@link Attempt}.
     *
     * @param instance the instance to invoke the method on
     * @param arguments the arguments to pass to the method
     * @return the result of the method invocation
     */
    default Attempt<ReturnType, Throwable> invoke(Parent instance, Object... arguments) {
        return this.invoke(instance, Arrays.asList(arguments));
    }

    /**
     * Invokes the method represented by this view on the given instance with the given arguments. If
     * the method is static, the instance may be {@code null}. Any exceptions thrown by the method
     * will be caught and returned as a failed {@link Attempt}.
     *
     * @param instance the instance to invoke the method on
     * @param arguments the arguments to pass to the method
     * @return the result of the method invocation
     */
    Attempt<ReturnType, Throwable> invoke(Parent instance, Collection<?> arguments);

    /**
     * Invokes the method represented by this view as a static method call with the given arguments.
     * If the method is not static, a {@link IllegalIntrospectionException} will be thrown. Any
     * exceptions thrown by the method will be caught and returned as a failed {@link Attempt}.
     *
     * @param arguments the arguments to pass to the method
     * @throws IllegalIntrospectionException if the method is not static
     * @return the result of the method invocation
     */
    default Attempt<ReturnType, Throwable> invokeStatic(Object... arguments) {
        return this.invokeStatic(Arrays.asList(arguments));
    }

    /**
     * Invokes the method represented by this view as a static method call with the given arguments.
     * If the method is not static, a {@link IllegalIntrospectionException} will be thrown. Any
     * exceptions thrown by the method will be caught and returned as a failed {@link Attempt}.
     *
     * @param arguments the arguments to pass to the method
     * @throws IllegalIntrospectionException if the method is not static
     * @return the result of the method invocation
     */
    Attempt<ReturnType, Throwable> invokeStatic(Collection<?> arguments);

    /**
     * Returns a {@link TypeView} representing the non-generic return type of the method.
     *
     * @return a view of the method's return type
     * @see #type()
     */
    TypeView<ReturnType> returnType();

    /**
     * Returns a {@link TypeView} representing the generic return type of the method. If the method
     * is not generic, this will return the same value as {@link #returnType()}.
     *
     * @return a view of the method's generic return type
     * @see #genericType()
     */
    TypeView<ReturnType> genericReturnType();

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isStatic()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    boolean isStatic();

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isFinal()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    boolean isFinal();

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isAbstract()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    boolean isAbstract();

    /**
     * @deprecated use {@link #modifiers()} and {@link ElementModifiersIntrospector#isDefault()} instead
     * @return true if the modifier is present
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    boolean isDefault();

}
