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

package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * A module that can be used to extend the HSL language. This module is responsible for combining the required definitions
 * for a specific type of AST node, including a parser, interpreter, and resolver. The module is also responsible for
 * defining the token type that the parser should use to identify the node.
 *
 * <p>Extensions are not automatically enabled, and must be registered through a {@link RuntimeExtensionCodeCustomizer} in
 * order for them to become available.
 *
 * <p>Custom AST nodes are used to extend the HSL language with custom syntax. This can be used to add new expressions,
 * statements, or even entire new languages. All custom nodes must implement the {@link CustomASTNode} interface, which
 * ensures that the node is associated with a module that defines the required definitions for the node.
 *
 * <p>Custom nodes can be used in the same way as any other node, and can be used in any context where the node type is
 * accepted. This includes expressions and statements. Note that custom nodes are not automatically resolved, if this is
 * required the {@link ASTExtensionModule#resolver()} method must be overridden to provide a resolver.
 *
 * <p>To ensure that custom nodes are correctly interpreted, implementations are limited to valid
 * {@link org.dockbox.hartshorn.hsl.ast.expression.Expression expressions} and {@link org.dockbox.hartshorn.hsl.ast.statement.Statement statements}.
 * You can use the {@link ExpressionModule} and {@link StatementModule} interfaces to ensure that the correct type of
 * node is returned by the parser.
 *
 * @param <T> The type of AST node that this module is responsible for.
 * @param <R> The return type of the interpreter for this module.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public sealed interface ASTExtensionModule<T extends ASTNode & CustomASTNode<T, R>, R> permits ExpressionModule, StatementModule {

    /**
     * The token type that the parser should use to identify the node. This is used during
     * tokenization and parsing to identify the node.
     *
     * @return The token type that the parser should use to identify the node.
     */
    TokenType tokenType();

    /**
     * The parser that is responsible for parsing the node.
     *
     * @return The parser that is responsible for parsing the node.
     */
    ASTNodeParser<T> parser();

    /**
     * The resolver that is responsible for resolving the node, if required. If the node does not require
     * any resolution, the default implementation can be used.
     *
     * @return The resolver that is responsible for resolving the node, if required.
     */
    default ResolverExtension<T> resolver() {
        return (node, resolver) -> {};
    }

    /**
     * The interpreter that is responsible for interpreting the node. The return type of the interpreter
     * must match the return type of the node (either {@link Object} for expressions, or {@link Void} for
     * statements).
     *
     * @return The interpreter that is responsible for interpreting the node.
     */
    ASTNodeInterpreter<R, T> interpreter();
}
