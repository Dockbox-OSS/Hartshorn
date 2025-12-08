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

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.util.stream.EntryStream;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interpreter for {@link ModuleStatement} nodes.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ModuleStatementInterpreter extends AbstractNativeLibraryStatementInterpreter implements StatementInterpreter<ModuleStatement> {

    @Override
    public Void interpret(ModuleStatement node, Interpreter interpreter) {
        String moduleName = node.name().lexeme();
        NativeModule module = interpreter.state().externalModules().get(moduleName);

        List<NativeFunctionStatement> supportedFunctions = module.supportedFunctions(node.name(), interpreter);
        Map<String, List<NativeFunctionStatement>> functionsByName = supportedFunctions.stream()
                .collect(Collectors.groupingBy(function -> function.name().lexeme()));

        EntryStream.of(functionsByName).forEach((name, functions) -> {
            this.registerModuleFunction(
                    moduleName,
                    name,
                    interpreter,
                    functions,
                    module
            );
        });
        return null;
    }
}
