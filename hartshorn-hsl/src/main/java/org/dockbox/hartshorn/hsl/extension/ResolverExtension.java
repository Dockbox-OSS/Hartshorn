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
import org.dockbox.hartshorn.hsl.semantic.Resolver;

/**
 * Functional interface for custom resolver extensions, specific to custom AST nodes.
 * This interface is used to provide custom resolver logic for custom AST nodes, which
 * is used during the semantic analysis phase of the compilation process.
 *
 * @param <T> The type of the custom AST node.
 */
@FunctionalInterface
public interface ResolverExtension<T extends ASTNode & CustomASTNode<?, ?>> {

    /**
     * Resolves the provided node using the provided resolver. This is used to resolve any references
     * that are present in the node.
     *
     * @param node The node to resolve.
     * @param resolver The resolver to use to resolve the node.
     */
    void resolve(T node, Resolver resolver);
}
