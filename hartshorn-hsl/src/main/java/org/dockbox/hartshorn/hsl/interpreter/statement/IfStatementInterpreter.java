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

import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;

public class IfStatementInterpreter implements ASTNodeInterpreter<Void, IfStatement> {

    @Override
    public Void interpret(IfStatement node, InterpreterAdapter adapter) {
        Object conditionResult = adapter.evaluate(node.condition());
        VariableScope previous = adapter.visitingScope();

        if (InterpreterUtilities.isTruthy(conditionResult)) {
            VariableScope thenVariableScope = new VariableScope(previous);
            adapter.enterScope(thenVariableScope);
            adapter.execute(node.thenBranch(), thenVariableScope);
        }
        else if (node.elseBranch() != null) {
            VariableScope elseVariableScope = new VariableScope(previous);
            adapter.enterScope(elseVariableScope);
            adapter.execute(node.elseBranch(), elseVariableScope);
        }
        adapter.enterScope(previous);
        return null;
    }
}
