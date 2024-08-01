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

import java.util.ArrayList;
import java.util.List;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.BindableNode;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class FunctionCallExpressionInterpreter implements ASTNodeInterpreter<Object, FunctionCallExpression> {

    @Override
    public Object interpret(FunctionCallExpression node, Interpreter interpreter) {
        Object callee = interpreter.evaluate(node.callee());

        List<Object> arguments = new ArrayList<>();
        for (Expression argument : node.arguments()) {
            Object evaluated = interpreter.evaluate(argument);
            if (evaluated instanceof ExternalObjectReference external) {
                evaluated = external.externalObject();
            }
            arguments.add(evaluated);
        }

        // Can't call non-callable nodes..
        Token openParenthesis = node.openParenthesis();
        if (!(callee instanceof CallableNode function)) {
            throw new ScriptEvaluationError("Can only call functions and classes, but received " + callee + ".", Phase.INTERPRETING, openParenthesis);
        }

        try {
            if (callee instanceof InstanceReference instance) {
                return function.call(openParenthesis, interpreter, instance, arguments);
            }
            else if (callee instanceof BindableNode<?> bindable){
                return function.call(openParenthesis, interpreter, bindable.bound(), arguments);
            }
            else {
                return function.call(openParenthesis, interpreter, null, arguments);
            }
        }
        catch (ApplicationException e) {
            throw new ScriptEvaluationError(e, Phase.INTERPRETING, openParenthesis);
        }
    }
}
