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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.modules.AmbiguousNativeLibraryFunction;
import org.dockbox.hartshorn.hsl.modules.NativeLibrary;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract base class for interpreters that handle native library statements, which may contain
 * multiple functions with the same name. When such ambiguity arises, this class provides the logic
 * to either throw an error or register an ambiguous function handler based on the current
 * interpreter's settings.
 *
 * @since 0.5.0
 * 
 * @author Guus Lieben
 */
public abstract class AbstractNativeLibraryStatementInterpreter {

    protected void registerModuleFunction(
        String moduleName,
        String functionName,
        Interpreter interpreter,
        List<NativeFunctionStatement> supportedFunctions,
        NativeModule module
    ) {
        boolean ambiguousFunction = supportedFunctions.size() > 1;
        if (ambiguousFunction) {
            if (!interpreter.executionOptions().permitAmbiguousExternalFunctions()) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.AMBIGUOUS_FUNCTION_IN_MODULE,
                        moduleName,
                        functionName)
                    .at(supportedFunctions.getFirst().name())
                    .build();
            }
            else {
                Set<NativeLibrary> libraries = supportedFunctions.stream()
                    .map(function -> new NativeLibrary(function, moduleName, module))
                    .collect(Collectors.toSet());
                interpreter.global()
                    .define(supportedFunctions.getFirst().name().lexeme(),
                        new AmbiguousNativeLibraryFunction(libraries));
            }
        }
        else {
            NativeFunctionStatement supportedFunction = supportedFunctions.getFirst();
            NativeLibrary library = new NativeLibrary(supportedFunction, moduleName, module);
            interpreter.global().define(supportedFunction.name().lexeme(), library);
        }
    }
}
