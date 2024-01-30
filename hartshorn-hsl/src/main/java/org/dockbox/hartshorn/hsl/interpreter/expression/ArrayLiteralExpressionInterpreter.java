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

import java.util.List;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;

public class ArrayLiteralExpressionInterpreter implements ASTNodeInterpreter<Object, ArrayLiteralExpression> {

    @Override
    public Object interpret(ArrayLiteralExpression node, Interpreter interpreter) {
        Object[] values = new Object[node.elements().size()];
        List<Expression> elements = node.elements();
        for (int i = 0, elementsSize = elements.size(); i < elementsSize; i++) {
            Expression expression = elements.get(i);
            values[i] = interpreter.evaluate(expression);
        }
        return new Array(values);
    }
}
