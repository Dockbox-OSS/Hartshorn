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
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Parser for infix function (call) expressions. Infix functions are functions that are called using
 * an operator-like syntax, such as <code>a add b</code> instead of
 * <code>add(a, b)</code>.
 *
 * @since 0.7.0
 * 
 * @author Guus Lieben
 */
public class InfixExpressionParser extends AbstractFunctionOperatorExpressionParser {

    @Override
    public Expression parse(
        TokenParser parser,
        TokenStepValidator validator,
        ExpressionParserChain chain
    ) {
        Expression expression = chain.next(parser, validator);

        TokenType identifier = parser.tokenRegistry().literals().identifier();
        while (parser.check(identifier) && this.hasInfixFunction(parser, parser.peek())) {
            Token operator = parser.advance();
            Expression right = parser.expression();
            expression = new InfixExpression(expression, operator, right);
        }
        return expression;
    }

    private boolean hasInfixFunction(TokenParser parser, Token name) {
        return this.containedInFunctionContext(parser,
            context -> context.infixFunctions().contains(name.lexeme()));
    }
}
