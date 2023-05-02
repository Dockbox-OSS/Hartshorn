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

package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.token.TokenType;

public class FieldStatementInterpreter implements ASTNodeInterpreter<Void, FieldStatement> {

    @Override
    public Void interpret(final FieldStatement node, final InterpreterAdapter adapter) {
        final Object value = adapter.evaluate(node.initializer());
        final int distance = adapter.distance(node.initializer());
        final PropertyContainer object = (PropertyContainer) adapter.visitingScope().getAt(node.name(), distance - 1, TokenType.THIS.representation());
        object.set(node.name(), value, adapter.visitingScope(), adapter.interpreter().executionOptions());
        return null;
    }
}
