/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;

public class UnaryExpressionInterpreter implements ASTNodeInterpreter<Object, UnaryExpression> {

    @Override
    public Object interpret(final UnaryExpression node, final InterpreterAdapter adapter) {
        TokenType type = node.operator().type();
        final Object right = adapter.evaluate(node.rightExpression());

        final Object newValue;
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
            final int value = ((Double) right).intValue();
            // Cast to int is redundant, but required to suppress false-positive inspections.
            //noinspection RedundantCast
            newValue = (int) ~value;
        }
        else {
            throw new RuntimeException("Unsupported unary operator.");
        }

        if (node.rightExpression() instanceof VariableExpression variable) {
            adapter.visitingScope().assign(variable.name(), newValue);
        }
        return newValue;
    }
}
