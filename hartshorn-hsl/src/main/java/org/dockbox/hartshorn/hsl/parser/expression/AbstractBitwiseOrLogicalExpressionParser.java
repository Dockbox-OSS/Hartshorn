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
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Abstract base implementation for bitwise or logical expression parsers. This parser handles the
 * common logic for parsing left-associative binary expressions, while leaving the specific operator
 * tokens and expression construction to subclasses.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractBitwiseOrLogicalExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(
        TokenParser parser,
        TokenStepValidator validator,
        ExpressionParserChain chain
    ) {
        Expression expression = chain.next(parser, validator);
        while (parser.match(this.whileMatching())) {
            Token operator = parser.previous();
            // Unlike most, we do not want to call parser.expression() for the right-hand side,
            // as that would allow for operators with a lower precedence to be parsed first.
            // Instead, we want to continue parsing with the next parser in the chain, which
            // should be a parser with the same or higher precedence.
            Expression right = chain.next(parser, validator);
            expression = this.define(expression, operator, right);
        }
        return expression;
    }

    /**
     * Defines the token types that this parser should match while parsing. These token types
     * represent the operators for the bitwise or logical expressions.
     *
     * @return the token types to match
     */
    protected abstract TokenType[] whileMatching();

    /**
     * Defines how to construct the expression for the given left-hand side, operator, and
     * right-hand side expressions.
     *
     * @param expression the left-hand side expression
     * @param operator the operator token
     * @param right the right-hand side expression
     *
     * @return the constructed expression
     */
    protected abstract Expression define(Expression expression, Token operator, Expression right);
}
