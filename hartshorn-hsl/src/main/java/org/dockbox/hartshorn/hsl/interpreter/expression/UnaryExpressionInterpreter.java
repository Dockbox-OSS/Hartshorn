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

package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;

/**
 * Interpreter for {@link UnaryExpression} nodes.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class UnaryExpressionInterpreter implements ASTNodeInterpreter<Object, UnaryExpression> {

    @Override
    public Object interpret(UnaryExpression node, Interpreter interpreter) {
        TokenType type = node.operator().type();
        Object right = interpreter.evaluate(node.rightExpression());

        Object newValue;
        if (type instanceof ArithmeticTokenType arithmeticTokenType) {
             newValue = switch (arithmeticTokenType) {
                case MINUS -> {
                    Number rightNumber = InterpreterUtilities.checkNumberOperand(node.operator(), right);
                    yield -rightNumber.doubleValue();
                }
                case PLUS_PLUS -> {
                    Number rightNumber = InterpreterUtilities.checkNumberOperand(node.operator(), right);
                    yield rightNumber.doubleValue() + 1;
                }
                case MINUS_MINUS -> {
                    Number rightNumber = InterpreterUtilities.checkNumberOperand(node.operator(), right);
                    yield rightNumber.doubleValue() - 1;
                }
                default -> null;
            };
        }
        else if (type == BaseTokenType.BANG) {
            newValue = !InterpreterUtilities.isTruthy(right);
        }
        else if (type == BitwiseTokenType.COMPLEMENT) {
            Number rightNumber = InterpreterUtilities.checkNumberOperand(node.operator(), right);
            int value = rightNumber.intValue();
            newValue = (double) ~value;
        }
        else {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.UNSUPPORTED_UNARY, node.operator().lexeme())
                    .at(node.operator())
                    .build();
        }

        if (node.rightExpression() instanceof VariableExpression variable) {
            interpreter.visitingScope().assign(variable.name(), newValue);
        }
        return newValue;
    }
}
