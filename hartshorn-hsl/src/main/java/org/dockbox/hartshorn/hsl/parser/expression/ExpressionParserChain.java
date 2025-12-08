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
 * Chain interface for expression parsers. As expressions can be nested and have different
 * precedence levels, this interface allows expression parsers to delegate parsing to the next
 * parser in the chain, while retaining the correct order of operations.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ExpressionParserChain {

    /**
     * Delegates parsing to the next expression parser in the chain.
     *
     * @param parser the token parser owning this chain
     * @param validator the token step validator
     *
     * @return the parsed expression, or null if no expression could be parsed
     */
    Expression next(TokenParser parser, TokenStepValidator validator);
}
