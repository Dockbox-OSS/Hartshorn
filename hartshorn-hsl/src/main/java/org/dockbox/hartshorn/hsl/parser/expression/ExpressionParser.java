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

package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;

/**
 * Base interface for expression parsers. Implementations of this interface are responsible for
 * parsing specific types of expressions from a stream of tokens.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ExpressionParser {

    /**
     * Parses an expression from the given {@link TokenParser}, using the provided
     * {@link TokenStepValidator} to validate each step of the parsing process. If this parser
     * cannot handle the current tokens, or requires further parsing of sub-expressions, it should
     * delegate to the next parser in the {@link ExpressionParserChain}.
     *
     * @param parser the token parser calling this method
     * @param validator the token step validator
     * @param chain the expression parser chain for delegation
     *
     * @return the parsed expression
     */
    Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain);
}
