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
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class LogicalExpressionInterpreter extends BitwiseInterpreter<Object, LogicalExpression> {

    @Override
    public Object interpret(LogicalExpression node, Interpreter interpreter) {
        Object left = interpreter.evaluate(node.leftExpression());
        TokenType type = node.operator().type();
        if (type == ConditionTokenType.AND) {
            if (!InterpreterUtilities.isTruthy(left)) {
                return false;
            }
            // Don't evaluate right if left is not truthy
            Object right = interpreter.evaluate(node.rightExpression());
            return InterpreterUtilities.isTruthy(right);
        }
        else if (type == ConditionTokenType.OR) {
            if (InterpreterUtilities.isTruthy(left)) {
                return true;
            }
            // No need to evaluate right if left is already truthy
            Object right = interpreter.evaluate(node.rightExpression());
            return InterpreterUtilities.isTruthy(right);
        }
        else if (type == BitwiseTokenType.XOR) {
            Object right = interpreter.evaluate(node.rightExpression());
            return this.xor(left, right);
        }
        throw new ScriptEvaluationError("Unsupported logical operator.", Phase.INTERPRETING, node.operator());
    }
}
