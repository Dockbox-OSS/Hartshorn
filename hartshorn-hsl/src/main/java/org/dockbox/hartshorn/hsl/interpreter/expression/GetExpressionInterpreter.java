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

import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.objects.external.ExternalFunction;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;

public class GetExpressionInterpreter implements ASTNodeInterpreter<Object, GetExpression> {

    @Override
    public Object interpret(final GetExpression node, final Interpreter interpreter) {
        final Object object = interpreter.evaluate(node.object());
        if (object instanceof PropertyContainer container) {
            Object result = container.get(node.name(), interpreter.visitingScope(), interpreter.executionOptions());
            if (result instanceof ExternalObjectReference objectReference) {
                result = objectReference.externalObject();
            }
            if (result instanceof ExternalFunction bindableNode && object instanceof InstanceReference instance) {
                return bindableNode.bind(instance);
            }
            return result;
        }
        throw new RuntimeError(node.name(), "Only instances have properties.");
    }
}
