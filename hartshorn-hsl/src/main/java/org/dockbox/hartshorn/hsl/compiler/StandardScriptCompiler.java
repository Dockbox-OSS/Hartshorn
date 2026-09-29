/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.hsl.compiler;

import org.dockbox.hartshorn.hsl.CompiledScript;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.compiler.runtime.CompiledScriptExecutable;
import org.dockbox.hartshorn.hsl.compiler.runtime.DynamicScriptClassLoader;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Standard implementation of {@link ScriptCompiler} that emits and defines JVM bytecode classes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StandardScriptCompiler implements ScriptCompiler {

    private static final AtomicLong SCRIPT_COUNTER = new AtomicLong();

    @Override
    public CompiledScript compile(ScriptContext context, List<Statement> statements) {
        String className = "org.dockbox.hartshorn.hsl.generated.HslCompiledScript_" + SCRIPT_COUNTER.incrementAndGet();
        CompilationContext compilationContext = new CompilationContext(context, context.symbolTable(), className);
        BytecodeCompilerVisitor visitor = new BytecodeCompilerVisitor(compilationContext);
        byte[] bytecode = visitor.compile(statements);

        Class<? extends CompiledScriptExecutable> executableClass = DynamicScriptClassLoader.loadExecutableClass(className, bytecode);
        try {
            CompiledScriptExecutable executable = executableClass.getDeclaredConstructor().newInstance();
            return new StandardCompiledScript(context.runtime(), executable, bytecode);
        }
        catch (Exception e) {
            throw ScriptEvaluationError.builder(Phase.COMPILING)
                    .virtualPosition()
                    .cause(e)
                    .message(DiagnosticMessage.SCRIPT_INSTANTIATION_FAILED, className)
                    .build();
        }
    }
}
