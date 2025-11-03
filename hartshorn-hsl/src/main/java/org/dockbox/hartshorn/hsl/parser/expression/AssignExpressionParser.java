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
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;

/**
 * Assign expression parser, responsible for parsing various assignment expressions. This includes
 * {@link AssignExpression standard assignments}, {@link ArraySetExpression array element assignments}, and
 * {@link SetExpression object property assignments}.
 *
 * <p>The parser first attempts to parse an expression using the next parser in the chain. If an assignment
 * operator ({@code =}) is encountered, it checks the type of the parsed expression to determine the appropriate
 * assignment type. If the expression is a {@link VariableExpression}, it creates an {@link AssignExpression}.
 * If it's an {@link ArrayGetExpression}, it creates an {@link ArraySetExpression}. And if it's a {@link GetExpression},
 * it creates a {@link SetExpression}. In all other cases the expression is not considered a valid assignment target,
 * and a {@link ScriptEvaluationError} is thrown.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class AssignExpressionParser implements ExpressionParser {

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);

        if (parser.match(BaseTokenType.EQUAL)) {
            Token equals = parser.previous();
            Expression value = parser.expression();

            if (expression instanceof VariableExpression variableExpression) {
                Token name = variableExpression.name();
                return new AssignExpression(name, value);
            }
            else if (expression instanceof ArrayGetExpression arrayGetExpression) {
                Token name = arrayGetExpression.name();
                return new ArraySetExpression(name, arrayGetExpression.index(), value);
            }
            else if (expression instanceof GetExpression getExpression) {
                return new SetExpression(getExpression.object(), getExpression.name(), value);
            }
            throw ScriptEvaluationError.builder(Phase.PARSING)
                    .message(DiagnosticMessage.INVALID_ASSIGNMENT_TARGET, expression)
                    .at(equals)
                    .build();
        }
        return expression;
    }

}
