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

import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.runtime.Return;

public class TestStatementInterpreter implements ASTNodeInterpreter<Void, TestStatement> {

    @Override
    public Void interpret(TestStatement node, InterpreterAdapter adapter) {
        String name = String.valueOf(node.name().literal());
        VariableScope variableScope = new VariableScope(adapter.global());
        try {
            adapter.execute(node.body(), variableScope);
        }
        catch (Return r) {
            Object value = r.value();
            boolean val = InterpreterUtilities.isTruthy(value);
            adapter.interpreter().resultCollector().addResult(name, val);
        }
        return null;
    }
}
