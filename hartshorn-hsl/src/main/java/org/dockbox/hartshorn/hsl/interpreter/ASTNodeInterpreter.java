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

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.ast.ASTNode;

/**
 * Interface for interpreting AST nodes. Interpreters execute the logic represented by the AST
 * nodes. In the case of expressions, this typically involves evaluating the expression and
 * returning a result. For statements, this may involve executing the statement's logic without
 * returning a value.
 *
 * @param <R> the return type of the interpretation, which can vary based on the node type
 * @param <T> the specific type of AST node this interpreter can handle
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ASTNodeInterpreter<R, T extends ASTNode> {

     /**
      * Interprets the given AST node using the provided interpreter context.
      *
      * @param node the AST node to interpret
      * @param interpreter the interpreter context used for interpreting the node
      * @return the result of the interpretation, which can vary based on the node type
      */
     R interpret(T node, Interpreter interpreter);
}
