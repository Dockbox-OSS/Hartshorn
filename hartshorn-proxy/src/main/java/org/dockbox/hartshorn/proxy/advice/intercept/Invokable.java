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

package org.dockbox.hartshorn.proxy.advice.intercept;

/**
 * Represents a member that can be invoked, typically a method. This interface is used to decouple
 * the {@link MethodInterceptor} from the underlying implementation of the method, allowing for
 * different implementations to be used.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public interface Invokable {

    /**
     * Invoke the member with the given arguments. The provided instance will be used as the
     * {@code this} instance when invoking the member, assuming it is not static.
     *
     * @param obj the instance to use as {@code this}
     * @param args the arguments to use
     * @return the result of the invocation
     * @throws Exception if the invocation fails
     */
    Object invoke(Object obj, Object... args) throws Exception;

    /**
     * Ensures that this member is accessible. This is typically used to allow for invoking
     * non-public members.
     *
     * @param accessible whether or not the member should be accessible
     */
    void setAccessible(boolean accessible);

    /**
     * Returns the declaring class of this member.
     *
     * @return the declaring class
     */
    Class<?> declaringClass();

    /**
     * Returns the name of this member. For example, for methods this will return the method name.
     *
     * @return the name of this member
     */
    String name();

    /**
     * Returns whether this member is {@code default}, as defined by the Java language. This is
     * typically used to determine if a method is a default method on an interface.
     *
     * @return whether this member is {@code default}
     */
    boolean isDefault();

    /**
     * Returns the return type of this member.
     *
     * @return the return type
     */
    Class<?> returnType();

    /**
     * Returns the parameter types of this member. If this member has no parameters, an empty array
     * will be returned. Parameters will always be returned in the order they are declared.
     *
     * @return the parameter types
     */
    Class<?>[] parameterTypes();

    /**
     * Returns the qualified name of this member. This is typically used to uniquely identify a
     * member. For example, for methods this will return the fully qualified method name, including
     * the declaring class name.
     *
     * @return the qualified name
     */
    String qualifiedName();
}
