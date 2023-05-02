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

import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class TernaryExpressionInterpreter implements ASTNodeInterpreter<Object, TernaryExpression> {

    @Override
    public Object interpret(final TernaryExpression node, final InterpreterAdapter adapter) {
        final Object condition = adapter.evaluate(node.condition());
        if (InterpreterUtilities.isTruthy(condition)) {
            return adapter.evaluate(node.firstExpression());
        }
        return adapter.evaluate(node.secondExpression());
    }
}
