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
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class CallExpressionParser implements ExpressionParser {

    private static final int MAX_NUM_OF_ARGUMENTS = 8;

    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        Expression expression = chain.next(parser, validator);
        if (expression != null) {
            TokenType identifier = parser.tokenRegistry().literals().identifier();
            while(true) {
                if(parser.match(parser.tokenRegistry().tokenPairs().parameters().open())) {
                    expression = this.finishCall(parser, validator, expression);
                }
                else if(parser.match(BaseTokenType.DOT)) {
                    Token name = parser.consume(identifier, "Expected property name after '.'.");
                    expression = new GetExpression(name, expression);
                }
                else if(parser.match(ArithmeticTokenType.PLUS_PLUS, ArithmeticTokenType.MINUS_MINUS)) {
                    Token operator = parser.previous();
                    expression = new PostfixExpression(operator, expression);
                }
                else {
                    break;
                }
            }
        }
        return expression;
    }

    private Expression finishCall(TokenParser parser, TokenStepValidator validator, Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        Token parenOpen = parser.previous();
        // For zero arguments
        if (!parser.check(parser.tokenRegistry().tokenPairs().parameters().close())) {
            do {
                if (arguments.size() >= MAX_NUM_OF_ARGUMENTS) {
                    throw ScriptEvaluationError.builder(Phase.PARSING)
                            .message(DiagnosticMessage.TOO_MANY_PARAMETERS, MAX_NUM_OF_ARGUMENTS)
                            .at(parser.peek())
                            .build();
                }
                arguments.add(parser.expression());
            }
            while (parser.match(BaseTokenType.COMMA));
        }
        Token parenClose = validator.expectAfter(parser.tokenRegistry().tokenPairs().parameters().close(), "arguments");
        return new FunctionCallExpression(callee, parenOpen, parenClose, arguments);
    }
}
