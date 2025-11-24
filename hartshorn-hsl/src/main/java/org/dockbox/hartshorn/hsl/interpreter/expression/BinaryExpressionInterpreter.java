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
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.interpreter.expression.bitwise.BitwiseAdditionStrategy;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.util.Tuple;

import java.util.Set;
import java.util.function.BiPredicate;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class BinaryExpressionInterpreter implements ASTNodeInterpreter<Object, BinaryExpression> {

    private final Set<BitwiseAdditionStrategy> additionStrategies;

    public BinaryExpressionInterpreter(BitwiseAdditionStrategy... additionStrategies) {
        this(Set.of(additionStrategies));
    }

    public BinaryExpressionInterpreter(Set<BitwiseAdditionStrategy> additionStrategies) {
        this.additionStrategies = additionStrategies;
    }

    @Override
    public Object interpret(BinaryExpression node, Interpreter interpreter) {
        Object left = interpreter.evaluate(node.leftExpression());
        Object right = interpreter.evaluate(node.rightExpression());

        left = InterpreterUtilities.unwrap(left);
        right = InterpreterUtilities.unwrap(right);

        Token operator = node.operator();
        return switch (operator.type()) {
            case ArithmeticTokenType.PLUS -> {
                for (BitwiseAdditionStrategy strategy : this.additionStrategies) {
                    if (strategy.supports(left, right)) {
                        yield strategy.add(left, right);
                    }
                }
                // Otherwise, unsupported
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                        .message(DiagnosticMessage.UNSUPPORTED_CHILD, ArithmeticTokenType.PLUS.representation(), left, right)
                        .at(operator)
                        .build();
            }
            case ArithmeticTokenType.MINUS -> {
                Tuple<Number, Number> tuple = InterpreterUtilities.checkNumberOperands(
                        operator,
                        left,
                        right
                );
                yield tuple.left().doubleValue() - tuple.right().doubleValue();
            }
            case ArithmeticTokenType.STAR -> {
                if ((left instanceof String || left instanceof Character) && right instanceof Number number) {
                    int times = number.intValue();
                    int length = left.toString().length() * times;
                    StringBuilder result = new StringBuilder(length);
                    String value = left.toString();
                    result.append(value.repeat(Math.max(0, times)));
                    yield result.toString();
                }
                else if (left instanceof Array array && right instanceof Number number) {
                    int times = number.intValue();
                    int length = array.length() * times;
                    Array result = new Array(length);
                    for (int i = 0; i < times; i++) {
                        int originalIndex = times % array.length();
                        result.value(array.value(originalIndex), i);
                    }
                    yield result;
                }
                Tuple<Number, Number> tuple = InterpreterUtilities.checkNumberOperands(
                        operator,
                        left,
                        right
                );
                yield tuple.left().doubleValue() * tuple.right().doubleValue();
            }
            case ArithmeticTokenType.MODULO -> {
                Tuple<Number, Number> tuple = InterpreterUtilities.checkNumberOperands(
                        operator,
                        left,
                        right
                );
                yield tuple.left().doubleValue() % tuple.right().doubleValue();
            }
            case ArithmeticTokenType.SLASH -> {
                Tuple<Number, Number> tuple = InterpreterUtilities.checkNumberOperands(
                        operator,
                        left,
                        right
                );
                if (tuple.right().doubleValue() == 0) {
                    throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                            .message(DiagnosticMessage.ILLEGAL_ZERO_DIVISION)
                            .at(operator)
                            .build();
                }
                yield tuple.left().doubleValue() / tuple.right().doubleValue();
            }
            case ConditionTokenType.GREATER -> this.compareNumbers(node, left, right, (l, r) -> l > r);
            case ConditionTokenType.GREATER_EQUAL -> this.compareNumbers(node, left, right, (l, r) -> l >= r);
            case ConditionTokenType.LESS -> this.compareNumbers(node, left, right, (l, r) -> l < r);
            case ConditionTokenType.LESS_EQUAL -> this.compareNumbers(node, left, right, (l, r) -> l <= r);
            case ConditionTokenType.BANG_EQUAL -> !InterpreterUtilities.isEqual(left, right);
            case ConditionTokenType.EQUAL_EQUAL -> InterpreterUtilities.isEqual(left, right);
            default -> null;
        };
    }

    private boolean compareNumbers(BinaryExpression expression, Object left, Object right, BiPredicate<Double, Double> predicate) {
        Tuple<Number, Number> tuple = InterpreterUtilities.checkNumberOperands(
                expression.operator(),
                left,
                right
        );
        return predicate.test(tuple.left().doubleValue(), tuple.right().doubleValue());
    }
}
