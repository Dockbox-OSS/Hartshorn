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

import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;

public class SwitchCaseInterpreter implements ASTNodeInterpreter<Void, SwitchCase> {

    @Override
    public Void interpret(SwitchCase node, InterpreterAdapter adapter) {
        adapter.withNextScope(() -> {
            try {
                adapter.execute(node.body());
            } catch (MoveKeyword moveKeyword) {
                if (moveKeyword.moveType() != MoveKeyword.MoveType.BREAK) {
                    throw new RuntimeException("Unexpected move keyword " + moveKeyword.moveType());
                }
            }
        });
        return null;
    }
}
