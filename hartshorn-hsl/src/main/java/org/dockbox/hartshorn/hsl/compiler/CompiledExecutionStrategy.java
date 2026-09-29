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
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ExecutionMode;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.ScriptExecutionStrategy;
import org.dockbox.hartshorn.hsl.runtime.SimpleScriptRuntime;

/**
 * Execution strategy that compiles HSL AST statements to JVM bytecode and executes them.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class CompiledExecutionStrategy implements ScriptExecutionStrategy {

    private final SimpleScriptRuntime runtime;
    private final ScriptCompiler compiler;

    public CompiledExecutionStrategy(SimpleScriptRuntime runtime, ScriptCompiler compiler) {
        this.runtime = runtime;
        this.compiler = compiler;
    }

    public CompiledExecutionStrategy(SimpleScriptRuntime runtime) {
        this(runtime, new StandardScriptCompiler());
    }

    @Override
    public ExecutionMode mode() {
        return ExecutionMode.COMPILED;
    }

    @Override
    public ScriptContext execute(ScriptContext context) {
        this.runtime.customizePhase(Phase.COMPILING, context);
        CompiledScript compiled = this.compiler.compile(context, context.statements());
        context.compiledScript(compiled);
        return compiled.execute(context);
    }
}
