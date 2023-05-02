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

import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

public class BinaryExpressionInterpreter implements ASTNodeInterpreter<Object, BinaryExpression> {

    @Override
    public Object interpret(final BinaryExpression node, final InterpreterAdapter adapter) {
        Object left = adapter.evaluate(node.leftExpression());
        Object right = adapter.evaluate(node.rightExpression());

        left = InterpreterUtilities.unwrap(left);
        right = InterpreterUtilities.unwrap(right);

        switch (node.operator().type()) {
            case PLUS -> {
                // Math plus
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                // String Addition
                if (left instanceof String || right instanceof String) {
                    // String.valueOf to handle nulls
                    return String.valueOf(left) + right;
                }

                // Special cases
                if ((left instanceof Character && right instanceof Character)) {
                    return String.valueOf(left) + right;
                }
                if ((left instanceof Character) && (right instanceof Double)) {
                    final int value = (Character) left;
                    return (double) right + value;
                }
                if ((left instanceof Double) && (right instanceof Character)) {
                    final int value = (Character) right;
                    return (double) left + value;
                }
                throw new RuntimeError(node.operator(), "Unsupported child for PLUS.\n");
            }
            case MINUS -> {
                InterpreterUtilities.checkNumberOperands(node.operator(), left, right);
                return (double) left - (double) right;
            }
            case STAR -> {
                if ((left instanceof String || left instanceof Character) && right instanceof Double) {
                    final int times = (int) ((double) right);
                    final int finalLen = left.toString().length() * times;
                    final StringBuilder result = new StringBuilder(finalLen);
                    final String strValue = left.toString();
                    result.append(strValue.repeat(Math.max(0, times)));
                    return result.toString();
                }
                else if (left instanceof Array array && right instanceof Double) {
                    final int times = (int) ((double) right);
                    final int finalLen = array.length() * times;
                    final Array result = new Array(finalLen);
                    for (int i = 0; i < times; i++) {
                        final int originalIndex = times % array.length();
                        result.value(array.value(originalIndex), i);
                    }
                    return result;
                }
                InterpreterUtilities.checkNumberOperands(node.operator(), left, right);
                return (double) left * (double) right;
            }
            case MODULO -> {
                InterpreterUtilities.checkNumberOperands(node.operator(), left, right);
                return (double) left % (double) right;
            }
            case SLASH -> {
                InterpreterUtilities.checkNumberOperands(node.operator(), left, right);
                if ((double) right == 0) {
                    throw new RuntimeError(node.operator(), "Can't use slash with zero double.");
                }
                return (double) left / (double) right;
            }
            case GREATER -> {
                InterpreterUtilities.checkNumberOperands(node.operator(), left, right);
                return Double.parseDouble(left.toString()) > Double.parseDouble(right.toString());
            }
            case GREATER_EQUAL -> {
                InterpreterUtilities.checkNumberOperands(node.operator(), left, right);
                return Double.parseDouble(left.toString()) >= Double.parseDouble(right.toString());
            }
            case LESS -> {
                InterpreterUtilities.checkNumberOperands(node.operator(), left, right);
                return Double.parseDouble(left.toString()) < Double.parseDouble(right.toString());
            }
            case LESS_EQUAL -> {
                InterpreterUtilities.checkNumberOperands(node.operator(), left, right);
                return Double.parseDouble(left.toString()) <= Double.parseDouble(right.toString());
            }
            case BANG_EQUAL -> {
                return !InterpreterUtilities.isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                return InterpreterUtilities.isEqual(left, right);
            }
        }
        return null;
    }
}
