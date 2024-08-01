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

import java.util.List;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Represents a node that can be called, producing a result. This is the base class for all types
 * of executable nodes, including functions and constructors.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface CallableNode {

    /**
     * Executes the node, producing a result. The arguments may or may not be used, depending on
     * the type of node.
     *
     * @param at The token at which the node is being executed. This is used for error reporting.
     * @param interpreter The interpreter that is executing the node.
     * @param instance The instance on which the node is being executed. This is used for method calls.
     * @param arguments The arguments that are passed to the node.
     * @return The result of the node.
     * @throws ApplicationException If an error occurs while executing the node.
     */
    Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments) throws ApplicationException;
}
