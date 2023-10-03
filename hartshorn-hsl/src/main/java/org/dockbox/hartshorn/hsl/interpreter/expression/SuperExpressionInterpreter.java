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

import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;

public class SuperExpressionInterpreter implements ASTNodeInterpreter<Object, SuperExpression> {

    @Override
    public Object interpret(final SuperExpression node, final InterpreterAdapter adapter) {
        final int distance = adapter.distance(node);
        final ClassReference superClass = (ClassReference) adapter.visitingScope().getAt(node.method(), distance, ObjectTokenType.SUPER.representation());
        final InstanceReference object = (InstanceReference) adapter.visitingScope().getAt(node.method(), distance - 1, ObjectTokenType.THIS.representation());
        final MethodReference method = superClass.method(node.method().lexeme());

        if (method == null) {
            throw new RuntimeError(node.method(), "Undefined property '" + node.method().lexeme() + "'.");
        }
        return method.bind(object);
    }
}
