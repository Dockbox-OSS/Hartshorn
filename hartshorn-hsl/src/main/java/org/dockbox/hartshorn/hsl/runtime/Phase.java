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

package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.lexer.SimpleTokenRegistryLexer;

/**
 * Represents each of the primary runtime phases which are performed when evaluating
 * a single HSL source.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public enum Phase {
    /**
     * Performed by the {@link SimpleTokenRegistryLexer}, to convert the
     * source content into {@link org.dockbox.hartshorn.hsl.token.Token}s. This is the
     * first step of the script evaluation process.
     */
    TOKENIZING,
    /**
     * Performed by the {@link org.dockbox.hartshorn.hsl.parser.TokenParser}, to convert
     * the token output of the {@link SimpleTokenRegistryLexer} into
     * {@link org.dockbox.hartshorn.hsl.ast.statement.Statement}s. This is the second
     * step of the script evaluation process.
     */
    PARSING,
    /**
     * Performed by the {@link org.dockbox.hartshorn.hsl.semantic.Resolver}, to look
     * up required identifiers before proceeding to runtime interpretation. During
     * this phase any duplicate variable declarations, invalid accessors, and unknown
     * identifiers are located and reported. This is the third step of the script
     * evaluation process.
     */
    RESOLVING,
    /**
     * Performed by the {@link org.dockbox.hartshorn.hsl.interpreter.Interpreter},
     * to execute the {@link org.dockbox.hartshorn.hsl.ast.statement.Statement} output
     * of the {@link org.dockbox.hartshorn.hsl.parser.TokenParser}. This is the phase in
     * which the script gets executed, and results are generated. This is the fourth
     * and last step of the script evaluation process.
     */
    INTERPRETING,
}
