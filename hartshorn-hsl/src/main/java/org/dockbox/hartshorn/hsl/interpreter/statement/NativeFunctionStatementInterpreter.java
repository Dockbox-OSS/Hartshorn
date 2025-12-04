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

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.modules.NativeModule;

import java.util.List;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class NativeFunctionStatementInterpreter extends AbstractNativeLibraryStatementInterpreter implements StatementInterpreter<NativeFunctionStatement> {

    @Override
    public Void interpret(NativeFunctionStatement node, Interpreter interpreter) {
        String functionName = node.name().lexeme();
        String moduleName = node.moduleName().lexeme();
        NativeModule module = interpreter.state().externalModules().get(moduleName);
        List<NativeFunctionStatement> supportedFunctions = module.supportedFunctions(node.moduleName(), interpreter)
                .stream()
                .filter(func -> func.name().lexeme().equals(functionName))
                .filter(func -> func.params().size() == node.params().size())
                .toList();

        this.registerModuleFunction(
                moduleName,
                functionName,
                interpreter,
                supportedFunctions,
                module
        );
        return null;
    }
}
