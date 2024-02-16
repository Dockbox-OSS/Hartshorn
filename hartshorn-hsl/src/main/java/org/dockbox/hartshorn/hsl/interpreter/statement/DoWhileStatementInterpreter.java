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

import org.dockbox.hartshorn.hsl.ast.FlowControlKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class DoWhileStatementInterpreter implements ASTNodeInterpreter<Void, DoWhileStatement> {

    @Override
    public Void interpret(DoWhileStatement node, Interpreter interpreter) {
        interpreter.withNextScope(() -> {
            do {
                try {
                    interpreter.execute(node.body());
                }
                catch (FlowControlKeyword keyword) {
                    if (keyword.moveType() == FlowControlKeyword.MoveType.BREAK) {
                        break;
                    }
                }
            }
            while (InterpreterUtilities.isTruthy(interpreter.evaluate(node.condition())));
        });
        return null;
    }
}
