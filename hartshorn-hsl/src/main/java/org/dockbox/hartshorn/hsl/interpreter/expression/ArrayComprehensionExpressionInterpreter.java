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

import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

import java.util.ArrayList;
import java.util.List;

public class ArrayComprehensionExpressionInterpreter implements ASTNodeInterpreter<Object, ArrayComprehensionExpression> {

    @Override
    public Object interpret(final ArrayComprehensionExpression node, final Interpreter interpreter) {
        final List<Object> values = new ArrayList<>();
        final Object collection = interpreter.evaluate(node.collection());
        if (collection instanceof Iterable<?> iterable) {

            interpreter.withNextScope(() -> {
                interpreter.visitingScope().define(node.selector().lexeme(), null);
                interpreter.withNextScope(() -> visitIterable(node, interpreter, values, iterable));
            });
        }
        else {
            throw new RuntimeError(node.open(), "Collection must be iterable");
        }
        return new Array(values.toArray());
    }

    private static void visitIterable(final ArrayComprehensionExpression node, final Interpreter interpreter,
                                      final List<Object> values, final Iterable<?> iterable) {
        for (final Object element : iterable) {
            interpreter.visitingScope().assign(node.selector(), element);

            if (node.condition() != null) {
                final Object condition = interpreter.evaluate(node.condition());
                if (!InterpreterUtilities.isTruthy(condition)) {
                    if (node.elseExpression() != null) {
                        final Object elseValue = interpreter.evaluate(node.elseExpression());
                        values.add(elseValue);
                    }
                    continue;
                }
            }

            final Object result = interpreter.evaluate(node.expression());
            values.add(result);
        }
    }
}
