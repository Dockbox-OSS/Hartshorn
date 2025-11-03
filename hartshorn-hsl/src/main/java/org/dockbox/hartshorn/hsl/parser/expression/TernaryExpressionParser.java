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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;

/**
 * Parser for ternary expressions. A ternary expression is a shorthand conditional expression
 * that evaluates to one of two values based on a condition. It follows the syntax:
 * <code>condition ? expressionIfTrue : expressionIfFalse</code>.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class TernaryExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);

        if (parser.match(BaseTokenType.QUESTION_MARK)) {
            Token question = parser.previous();
            Expression firstExp = parser.expression();
            Token colon = parser.peek();
            if (parser.match(BaseTokenType.COLON)) {
                Expression secondExp = parser.expression();
                return new TernaryExpression(expression, question, firstExp, colon, secondExp);
            }
            throw ScriptEvaluationError.builder(Phase.PARSING)
                    .message(DiagnosticMessage.EXPECTED_EXPRESSION_AFTER_X, BaseTokenType.COLON.representation())
                    .at(colon)
                    .build();
        }
        return expression;
    }

}
