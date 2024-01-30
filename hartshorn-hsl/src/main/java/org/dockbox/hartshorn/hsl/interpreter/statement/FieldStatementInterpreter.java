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

package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;

public class FieldStatementInterpreter implements ASTNodeInterpreter<Void, FieldStatement> {

    @Override
    public Void interpret(FieldStatement node, Interpreter interpreter) {
        Object value = interpreter.evaluate(node.initializer());
        int distance = interpreter.distance(node.initializer());
        PropertyContainer object = (PropertyContainer) interpreter.visitingScope().getAt(node.name(), distance - 1, ObjectTokenType.THIS.representation());
        object.set(node.name(), value, interpreter.visitingScope(), interpreter.executionOptions());
        return null;
    }
}
