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

package org.dockbox.hartshorn.util.reflect;

import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.application.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * A context element that represents a constructor. This context element can be used to instantiate a component, as well as provide information
 * about the executable element properties as defined in {@link ExecutableElementContext}.
 *
 * @param <T> The type of the component that is instantiated by this constructor.
 * @see Constructor
 * @author Guus Lieben
 * @since 21.5
 */
public final class ConstructorContext<T> extends ExecutableElementContext<Constructor<T>, T> implements TypedElementContext<T> {

    private final Constructor<T> constructor;
    private Function<Object[], Result<T>> invoker;

    private ConstructorContext(final Constructor<T> constructor) {
        this.constructor = constructor;
        this.constructor.setAccessible(true);
    }

    public Constructor<T> constructor() {
        return this.constructor;
    }

    /**
     * Creates a new {@link ConstructorContext} instance from the given {@link Constructor}.
     *
     * @param constructor The constructor to create the context from.
     * @param <T> The type of the component that is instantiated by this constructor.
     * @return A new {@link ConstructorContext} instance.
     */
    public static <T> ConstructorContext<T> of(final Constructor<T> constructor) {
        return new ConstructorContext<>(constructor);
    }

    /**
     * Invokes the constructor with the given arguments. This may be equal to calling {@link Constructor#newInstance(Object...)}, however it
     * may also be a more efficient way of invoking the constructor depending on the active invoker function.
     *
     * @param args The arguments to pass to the constructor.
     * @return The result of the invocation.
     */
    public Result<T> createInstance(final Object... args) {
        this.prepareHandle();
        return this.invoker.apply(args);
    }

    /**
     * Invokes the constructor with the required arguments. The arguments are determined by the provided {@link ApplicationContext}, and may be
     * {@code null} if the required parameter type cannot be provided.
     *
     * @param context The application context to use for resolving the required arguments.
     * @return The result of the invocation.
     */
    public Result<T> createInstance(final ApplicationContext context) {
        this.prepareHandle();
        try {
            final Object[] args = this.arguments(context);
            return this.invoker.apply(args);
        } catch (final StackOverflowError e) {
            // When the stack overflows, it typically indicates that there is a cycling dependency between the current component and one of its dependencies.
            // To provide a more meaningful error message, we need to find the cycle and provide a more specific error message. We do this by traversing the
            // dependency graph to find the cycle. If there is only one dependency, we can skip traversing and directly yield that as the cycling dependency.
            // TODO: This is a very naive implementation, and it is possible that there are more than one cycle. In that case, we need to find the longest
            //       cycle and provide a more specific error message.
            final LinkedList<TypeContext<?>> parameterTypes = this.parameterTypes();
            if (parameterTypes.size() == 1) {
                return Result.of(new CyclicComponentException(this, parameterTypes.get(0)));
            }
            else {
                for (final TypeContext<?> parameterType : parameterTypes) {
                    for (final ConstructorContext<?> constructor : parameterType.injectConstructors()) {
                        if (constructor.parameterTypes().contains(this.type())) {
                            return Result.of(new CyclicComponentException(this, parameterType));
                        }
                    }
                }
                return Result.of(new CyclicComponentException(this, null));
            }
        }
    }

    @Override
    public TypeContext<T> type() {
        return this.parent();
    }

    @Override
    public TypeContext<T> genericType() {
        return this.type();
    }

    @Override
    public String name() {
        return this.qualifiedName();
    }

    private void prepareHandle() {
        if (this.invoker == null) {
            this.invoker = args -> Result.of(() -> {
                try {
                    return this.constructor.newInstance(args);
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof Exception ex) throw ex;
                    throw e;
                }
            });
        }
    }

    @Override
    protected Constructor<T> element() {
        return this.constructor();
    }

    @Override
    public String qualifiedName() {
        return "Constructor[%s]".formatted(this.type().qualifiedName());
    }
}
