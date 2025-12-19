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
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Parser for logical expressions. Logical expressions combine boolean values using logical
 * operators such as AND ({@code &&}), OR ({@code ||}), and XOR ({@code ^}).
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class LogicalExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {

    @Override
    protected TokenType[] whileMatching() {
        return new TokenType[] {
            BitwiseTokenType.XOR,
            ConditionTokenType.OR,
            ConditionTokenType.AND,
        };
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        return new LogicalExpression(expression, operator, right);
    }
}
