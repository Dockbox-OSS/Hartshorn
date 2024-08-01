/*
 * Copyright 2019-2024 the original author or authors.
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
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;

/**
 * TODO: #1061 Add documentation
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
                    InterpreterUtilities.checkNumberOperand(node.operator(), right);
                    yield -(double) right;
                }
                case PLUS_PLUS -> {
                    InterpreterUtilities.checkNumberOperand(node.operator(), right);
                    yield (double) right + 1;
                }
                case MINUS_MINUS -> {
                    InterpreterUtilities.checkNumberOperand(node.operator(), right);
                    yield (double) right - 1;
                }
                default -> null;
            };
        }
        else if (type == BaseTokenType.BANG) {
            newValue = !InterpreterUtilities.isTruthy(right);
        }
        else if (type == BitwiseTokenType.COMPLEMENT) {

            InterpreterUtilities.checkNumberOperand(node.operator(), right);
            int value = ((Double) right).intValue();
            // Cast to int is redundant, but required to suppress false-positive inspections.
            //noinspection RedundantCast
            newValue = (int) ~value;
        }
        else {
            throw new ScriptEvaluationError("Unsupported unary operator.", Phase.INTERPRETING, node.operator());
        }

        if (node.rightExpression() instanceof VariableExpression variable) {
            interpreter.visitingScope().assign(variable.name(), newValue);
        }
        return newValue;
    }
}
