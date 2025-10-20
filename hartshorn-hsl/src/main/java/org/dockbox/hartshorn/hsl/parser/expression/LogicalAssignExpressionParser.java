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
import org.dockbox.hartshorn.hsl.ast.expression.LogicalAssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Parser for logical assignment expressions. Handles the parsing of expressions involving
 * logical assignment operators such as <code>&=</code> and <code>|=</code>.
 *
 * @see org.dockbox.hartshorn.hsl.token.type.BitwiseAssignmentTokenType
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class LogicalAssignExpressionParser extends AbstractBitwiseOrLogicalExpressionParser {

    private final TokenType[] assignmentTokens;

    public LogicalAssignExpressionParser(TokenRegistry registry) {
        this.assignmentTokens = registry
                .tokenTypes(token -> token.assignsWith() != null)
                .toArray(TokenType[]::new);
    }

    @Override
    protected TokenType[] whileMatching() {
        return this.assignmentTokens;
    }

    @Override
    protected Expression define(Expression expression, Token operator, Expression right) {
        if (expression instanceof VariableExpression variable) {
            return new LogicalAssignExpression(variable.name(), operator, right);
        }
        else {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                    .message(DiagnosticMessage.INVALID_ASSIGNMENT_TARGET, expression)
                    .at(operator)
                    .build();
        }
    }
}
