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

import org.dockbox.hartshorn.hsl.ast.expression.RangeExpression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class RangeExpressionInterpreter implements ASTNodeInterpreter<Object, RangeExpression> {

    @Override
    public Object interpret(RangeExpression node, Interpreter interpreter) {
        Object start = InterpreterUtilities.unwrap(interpreter.evaluate(node.leftExpression()));
        Object end = InterpreterUtilities.unwrap(interpreter.evaluate(node.rightExpression()));

        InterpreterUtilities.checkNumberOperands(node.operator(), start, end);

        int min = ((Number) start).intValue();
        int max = ((Number) end).intValue();

        int length = max - min + 1;
        Object[] result = new Object[length];
        for (int i = 0; i < length; i++) {
            result[i] = (double) min + i;
        }
        return new Array(result);
    }
}
