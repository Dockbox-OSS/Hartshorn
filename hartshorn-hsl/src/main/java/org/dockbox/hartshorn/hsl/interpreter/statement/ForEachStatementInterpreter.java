/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.statement.ForEachStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

/**
 * Interpreter for {@link ForEachStatement} nodes.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public class ForEachStatementInterpreter implements StatementInterpreter<ForEachStatement> {

    @Override
    public Void interpret(ForEachStatement node, Interpreter interpreter) {
        interpreter.withNextScope(() -> {
            Object collection = interpreter.evaluate(node.collection());
            Iterable<?> iterable = InterpreterUtilities.checkIterable(node.collection(), collection);
            interpreter.visitingScope().define(node.selector().name().lexeme(), null);

            for (Object item : iterable) {
                interpreter.visitingScope().assign(node.selector().name(), item);
                interpreter.execute(node.body());
            }
        });
        return null;
    }
}
