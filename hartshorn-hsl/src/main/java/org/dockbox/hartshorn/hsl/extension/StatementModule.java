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

import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

/**
 * Base module for custom statements. This class is non-sealed to allow for custom implementations
 * for various use cases. The {@link StatementModule} is used to provide access to the required
 * token type, parser, interpreter, and resolver for a custom statement type.
 *
 * @param <T> The type of the custom statement.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public non-sealed interface StatementModule<T extends Statement & CustomASTNode<T, Void>> extends ASTExtensionModule<T, Void> {

    <U> U accept(StatementVisitor<U> visitor);
}
