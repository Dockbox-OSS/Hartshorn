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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.modules.HslLibrary;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;

public class ModuleStatementInterpreter implements ASTNodeInterpreter<Void, ModuleStatement> {
    @Override
    public Void interpret(final ModuleStatement node, final InterpreterAdapter adapter) {
        final String moduleName = node.name().lexeme();
        final NativeModule module = adapter.interpreter().state().externalModules().get(moduleName);
        for (final NativeFunctionStatement supportedFunction : module.supportedFunctions(node.name())) {
            final HslLibrary library = new HslLibrary(supportedFunction, moduleName, module);

            if (adapter.global().contains(supportedFunction.name().lexeme()) && !adapter.interpreter().executionOptions().permitAmbiguousExternalFunctions()) {
                throw new ScriptEvaluationError("Module '" + moduleName + "' contains ambiguous function '" + supportedFunction.name().lexeme() + "' which is already defined in the global scope.", Phase.INTERPRETING, supportedFunction.name());
            }

            adapter.global().define(supportedFunction.name().lexeme(), library);
        }
        return null;
    }
}
