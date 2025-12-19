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
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;

/**
 * Parser for unary expressions. Unary expressions are expressions that operate on a single operand,
 * for example {@code !true} or {@code -5}.
 *
 * @since 0.7.0
 * 
 * @author Guus Lieben
 */
public class UnaryExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(
        TokenParser parser,
        TokenStepValidator validator,
        ExpressionParserChain chain
    ) {
        if (parser.match(BaseTokenType.BANG,
            ArithmeticTokenType.MINUS,
            ArithmeticTokenType.PLUS_PLUS,
            ArithmeticTokenType.MINUS_MINUS,
            BitwiseTokenType.COMPLEMENT)) {
            Token operator = parser.previous();
            Expression right = chain.next(parser, validator);
            return new UnaryExpression(operator, right);
        }
        else {
            return chain.next(parser, validator);
        }
    }
}
