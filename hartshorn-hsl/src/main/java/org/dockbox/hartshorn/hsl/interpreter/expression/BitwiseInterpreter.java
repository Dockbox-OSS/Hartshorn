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
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;

public abstract class BitwiseInterpreter<R, T extends ASTNode> implements ASTNodeInterpreter<R, T> {

    protected Object getBitwiseResult(Token operator, Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            int iLeft = ((Number) left).intValue();
            int iRight = ((Number) right).intValue();

            return switch (operator.type()) {
                case BitwiseTokenType.SHIFT_RIGHT -> iLeft >> iRight;
                case BitwiseTokenType.SHIFT_LEFT -> iLeft << iRight;
                case BitwiseTokenType.LOGICAL_SHIFT_RIGHT -> iLeft >>> iRight;
                case BitwiseTokenType.BITWISE_AND -> iLeft & iRight;
                case BitwiseTokenType.BITWISE_OR -> iLeft | iRight;
                case BitwiseTokenType.XOR -> this.xor(iLeft, iRight);
                default -> throw new ScriptEvaluationError(
                        "Unsupported operator type " + operator.type() + " for bitwise operation",
                        Phase.INTERPRETING, operator
                );
            };
        }
        String leftType = left != null ? left.getClass().getSimpleName() : null;
        String rightType = right != null ? right.getClass().getSimpleName() : null;
        throw new ScriptEvaluationError(
                "Bitwise left and right must be a numbers, but got %s (%s) and %s (%s)".formatted(left, leftType, right, rightType),
                Phase.INTERPRETING, operator
        );
    }

    protected Object xor(Object left, Object right) {
        if (left instanceof Number nleft && right instanceof Number nright) {
            int iLeft = nleft.intValue();
            int iRight = nright.intValue();
            return iLeft ^ iRight;
        }
        return InterpreterUtilities.isTruthy(left) ^ InterpreterUtilities.isTruthy(right);
    }
}
