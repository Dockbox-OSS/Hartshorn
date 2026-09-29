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

package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;

/**
 * Standard execution strategy executing scripts via the AST tree-walking {@link Interpreter}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class InterpretedExecutionStrategy implements ScriptExecutionStrategy {

    private final SimpleScriptRuntime runtime;

    public InterpretedExecutionStrategy(SimpleScriptRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public ExecutionMode mode() {
        return ExecutionMode.INTERPRETED;
    }

    @Override
    public ScriptContext execute(ScriptContext context) {
        Interpreter interpreter = context.interpreter();
        this.runtime.customizePhase(Phase.EXECUTING, context);
        interpreter.state().global(this.runtime.globalVariables());
        interpreter.state().externalClassRegistry().defineClasses(this.runtime.imports());
        interpreter.interpret(context.statements());
        return context;
    }
}
