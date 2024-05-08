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

import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

/**
 * TODO: #1061 Add documentation
 *
 * @param <Void> ...
 * @param <SwitchStatement>> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SwitchStatementInterpreter implements ASTNodeInterpreter<Void, SwitchStatement> {

    @Override
    public Void interpret(SwitchStatement node, Interpreter interpreter) {
        Object value = interpreter.evaluate(node.expression());
        value = InterpreterUtilities.unwrap(value);
        for (SwitchCase switchCase : node.cases()) {
            if (InterpreterUtilities.isEqual(value, switchCase.expression().value())) {
                interpreter.execute(switchCase);
                return null;
            }
        }
        if (node.defaultCase() != null) {
            interpreter.withNextScope(() -> interpreter.execute(node.defaultCase()));
        }
        return null;
    }
}
