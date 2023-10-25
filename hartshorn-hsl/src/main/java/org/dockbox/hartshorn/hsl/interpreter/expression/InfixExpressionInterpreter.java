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

import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class InfixExpressionInterpreter implements ASTNodeInterpreter<Object, InfixExpression> {

    @Override
    public Object interpret(InfixExpression node, InterpreterAdapter adapter) {
        CallableNode value = (CallableNode) adapter.visitingScope().get(node.infixOperatorName());
        List<Object> args = new ArrayList<>();
        args.add(adapter.evaluate(node.leftExpression()));
        args.add(adapter.evaluate(node.rightExpression()));

        try {
            return value.call(node.infixOperatorName(), adapter.interpreter(), null, args);
        }
        catch (ApplicationException e) {
            throw new RuntimeError(node.infixOperatorName(), e.getMessage());
        }
    }
}
