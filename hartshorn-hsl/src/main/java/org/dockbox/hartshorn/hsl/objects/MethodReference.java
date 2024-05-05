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

package org.dockbox.hartshorn.hsl.objects;

/**
 * Represents a reference to a method. This could represent e.g. a method defined in a script, or a
 * method loaded from a Java class file. Exact semantics are defined by the implementation.
 *
 * <p>Method references are {@link CallableNode}s, and can be called with a bound instance, or by
 * providing an instance as an argument. Both are equivalent to calling the method on the
 * instance. Implementations may choose to provide a default instance, or require an instance to be
 * provided. If an implementation provides a default instance, it may choose to ignore the given
 * instance when calling the method.
 *
 * <p>Method references are {@link BindableNode}s, and can be bound to an instance. This allows quick
 * access to the method, without having to provide the instance as an argument.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface MethodReference extends CallableNode, BindableNode<MethodReference>, Finalizable {
}
