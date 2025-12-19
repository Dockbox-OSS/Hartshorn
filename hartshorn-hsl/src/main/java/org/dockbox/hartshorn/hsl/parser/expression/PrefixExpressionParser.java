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
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * Parser for prefix function (call) expressions. Prefix functions are functions that are called
 * without parentheses, for example {@code not true} instead of {@code not(true)}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class PrefixExpressionParser extends AbstractFunctionOperatorExpressionParser {

    @Override
    public Expression parse(
        TokenParser parser,
        TokenStepValidator validator,
        ExpressionParserChain chain
    ) {
        if (parser.check(parser.tokenRegistry().literals().identifier()) && this.hasPrefixFunction(
            parser,
            parser.peek())) {
            Token prefixFunctionName = parser.advance();
            Expression right = chain.next(parser, validator);
            return new PrefixExpression(prefixFunctionName, right);
        }
        else {
            return chain.next(parser, validator);
        }
    }

    /**
     * Checks whether the given token name is a registered prefix function in the current function
     * context.
     *
     * @param parser the token parser
     * @param name the token name
     *
     * @return true if the name is a registered prefix function, false otherwise
     */
    protected boolean hasPrefixFunction(TokenParser parser, Token name) {
        return this.containedInFunctionContext(parser,
            context -> context.prefixFunctions().contains(name.lexeme()));
    }
}
