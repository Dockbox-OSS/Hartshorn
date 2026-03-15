/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;

/**
 * Parser for Elvis expressions. An elvis expression is a shorthand for a conditional expression
 * that returns the left-hand side if it is truthy, and the right-hand side otherwise.
 *
 * <p>For example, the expression {@code a ?: b} will return {@code a} if it is truthy,
 * or
 * {@code b} if {@code a} is falsy.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ElvisExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(
        TokenParser parser,
        TokenStepValidator validator,
        ExpressionParserChain chain
    ) {
        Expression expression = chain.next(parser, validator);
        if (parser.match(ConditionTokenType.ELVIS)) {
            Token elvis = parser.previous();
            Expression rightExp = parser.expression();
            return new ElvisExpression(expression, elvis, rightExp);
        }
        return expression;
    }
}
