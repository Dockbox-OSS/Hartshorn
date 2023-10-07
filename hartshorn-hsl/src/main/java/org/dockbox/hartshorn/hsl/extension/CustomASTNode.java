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

package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.semantic.Resolver;

/**
 * Represents a custom AST node, which can be interpreted and resolved. This is the base interface for
 * {@link CustomExpression} and {@link CustomStatement}, which limit the types of nodes that can be
 * added to a language runtime.
 *
 * @param <T> The type of the node.
 * @param <R> The type of the result of interpreting the node.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public sealed interface CustomASTNode<T extends ASTNode & CustomASTNode<T, R>, R> permits CustomExpression, CustomStatement {

    /**
     * Returns the module that is responsible for interpreting and resolving this node. This is used
     * to gain access to the appropriate token type, parser, interpreter, and resolver for the node.
     *
     * @return The module that is responsible for interpreting and resolving this node.
     */
    ASTExtensionModule<T, R> module();

    /**
     * Interprets this node using the provided interpreter. The result of interpreting this node is
     * returned.
     *
     * @param interpreter The interpreter to use to interpret this node.
     * @return The result of interpreting this node.
     */
    default R interpret(Interpreter interpreter) {
        return this.module().interpreter().interpret((T) this, interpreter);
    }

    /**
     * Resolves this node using the provided resolver. This is used to resolve any references that
     * are present in the node.
     *
     * @param resolver The resolver to use to resolve this node.
     */
    default void resolve(Resolver resolver) {
        this.module().resolver().resolve((T) this, resolver);
    }
}
