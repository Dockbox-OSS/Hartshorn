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

import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

public class LogicalExpressionInterpreter extends BitwiseInterpreter<Object, LogicalExpression> {

    @Override
    public Object interpret(final LogicalExpression node, final InterpreterAdapter adapter) {
        final Object left = adapter.evaluate(node.leftExpression());
        switch (node.operator().type()) {
            case AND -> {
                if (!InterpreterUtilities.isTruthy(left)) {
                    return false;
                }
                // Don't evaluate right if left is not truthy
                final Object right = adapter.evaluate(node.rightExpression());
                return InterpreterUtilities.isTruthy(right);
            }
            case OR -> {
                if (InterpreterUtilities.isTruthy(left)) {
                    return true;
                }
                // No need to evaluate right if left is already truthy
                final Object right = adapter.evaluate(node.rightExpression());
                return InterpreterUtilities.isTruthy(right);
            }
            case XOR -> {
                final Object right = adapter.evaluate(node.rightExpression());
                return this.xor(left, right);
            }
            default -> throw new RuntimeError(node.operator(), "Unsupported logical operator.");
        }
    }
}
