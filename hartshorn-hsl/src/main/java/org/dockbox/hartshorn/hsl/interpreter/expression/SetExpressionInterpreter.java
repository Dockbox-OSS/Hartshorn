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
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * TODO: #1061 Add documentation
 *
 * @param <Object> ...
 * @param <SetExpression>> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SetExpressionInterpreter implements ASTNodeInterpreter<Object, SetExpression> {

    @Override
    public Object interpret(SetExpression node, Interpreter interpreter) {
        Object object = interpreter.evaluate(node.object());

        if (object instanceof PropertyContainer instance) {
            Object value = interpreter.evaluate(node.value());
            instance.set(node.name(), value, interpreter.visitingScope(), interpreter.executionOptions());
            return value;
        }
        throw new ScriptEvaluationError("Only instances have properties.", Phase.INTERPRETING, node.name());
    }
}
