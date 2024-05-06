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

import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;

/**
 * TODO: #1061 Add documentation
 *
 * @param <Void> ...
 * @param <ConstructorStatement>> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ConstructorStatementInterpreter implements ASTNodeInterpreter<Void, ConstructorStatement> {

    @Override
    public Void interpret(ConstructorStatement node, Interpreter interpreter) {
        VirtualFunction function = new VirtualFunction(node, interpreter.visitingScope(), true);
        interpreter.visitingScope().define(node.initializerIdentifier().lexeme(), function);
        return null;
    }
}
