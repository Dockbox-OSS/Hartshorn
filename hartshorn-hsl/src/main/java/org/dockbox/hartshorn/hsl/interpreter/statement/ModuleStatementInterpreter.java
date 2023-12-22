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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.modules.AmbiguousLibraryFunction;
import org.dockbox.hartshorn.hsl.modules.NativeLibrary;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;

public class ModuleStatementInterpreter implements ASTNodeInterpreter<Void, ModuleStatement> {

    @Override
    public Void interpret(ModuleStatement node, InterpreterAdapter adapter) {
        String moduleName = node.name().lexeme();
        NativeModule module = adapter.interpreter().state().externalModules().get(moduleName);

        List<NativeFunctionStatement> supportedFunctions = module.supportedFunctions(node.name());
        Map<String, List<NativeFunctionStatement>> functionsByName = supportedFunctions.stream()
                .collect(Collectors.groupingBy(function -> function.name().lexeme()));

        for(List<NativeFunctionStatement> functions : functionsByName.values()) {
            registerModuleFunction(node, adapter, functions, moduleName, module);
        }

        return null;
    }

    private void registerModuleFunction(ModuleStatement node, InterpreterAdapter adapter, List<NativeFunctionStatement> supportedFunctions,
            String moduleName, NativeModule module) {
        boolean ambiguousFunction = supportedFunctions.size() > 1;
        if (ambiguousFunction) {
            if (!adapter.interpreter().executionOptions().permitAmbiguousExternalFunctions()) {
                throw new ScriptEvaluationError("Module '" + moduleName + "' contains ambiguous function '" + node.name().lexeme() + "' which is already defined in the global scope.", Phase.INTERPRETING, supportedFunctions.getFirst().name());
            }
            else {
                Set<NativeLibrary> libraries = supportedFunctions.stream()
                        .map(function -> new NativeLibrary(function, moduleName, module))
                        .collect(Collectors.toSet());
                adapter.global().define(supportedFunctions.getFirst().name().lexeme(), new AmbiguousLibraryFunction(libraries));
            }
        }
        else {
            NativeFunctionStatement supportedFunction = supportedFunctions.getFirst();
            NativeLibrary library = new NativeLibrary(supportedFunction, moduleName, module);
            adapter.global().define(supportedFunction.name().lexeme(), library);
        }
    }
}
