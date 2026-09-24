/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.ClassMemberStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClassBuilder;

import java.util.Set;

/**
 * An interpreter for a specific type of {@link ClassMemberStatement}, responsible for contributing
 * methods, fields, constructors, or custom behavior to the {@link VirtualClassBuilder}.
 *
 * @param <T> the type of class member statement this interpreter can interpret
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ClassMemberInterpreter<T extends ClassMemberStatement> {

    /**
     * Interprets the given class member node and contributes its behavior to the
     * {@link VirtualClassBuilder}.
     *
     * @param node the class member node to interpret
     * @param interpreter the current interpreter
     * @param builder the virtual class builder
     */
    void interpret(T node, Interpreter interpreter, VirtualClassBuilder builder);

    /**
     * Returns the set of class member statement types that this interpreter can handle.
     *
     * @return the set of handled class member statement types
     */
    Set<Class<? extends T>> types();
}
