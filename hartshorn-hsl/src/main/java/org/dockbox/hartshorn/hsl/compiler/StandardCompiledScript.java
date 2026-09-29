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

import java.util.Map;

import org.dockbox.hartshorn.hsl.CompiledScript;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.compiler.runtime.CompiledScriptExecutable;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;

/**
 * Standard implementation of {@link CompiledScript} wrapping generated JVM bytecode and an
 * executable instance.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StandardCompiledScript extends CompiledScript {

    private final CompiledScriptExecutable executable;
    private final byte[] bytecode;

    public StandardCompiledScript(ScriptRuntime runtime, CompiledScriptExecutable executable, byte[] bytecode) {
        super(runtime.applicationContext(), "");
        this.runtime(runtime);
        this.executable = executable;
        this.bytecode = bytecode;
    }

    @Override
    public CompiledScript compile() {
        return this;
    }

    @Override
    public ScriptContext execute() {
        ScriptContext context = this.runtime().createScriptContext("");
        return this.execute(context);
    }

    @Override
    public ScriptContext execute(ScriptContext context) {
        try {
            Object result = this.executable.execute(context);
            if (result != null && context.result().absent()) {
                context.addResult(result);
            }
            context.compiledScript(this);
            return context;
        }
        catch (ScriptEvaluationError e) {
            throw e;
        }
        catch (Throwable t) {
            throw ScriptEvaluationError.builder(Phase.EXECUTING)
                    .cause(t)
                    .virtualPosition()
                    .build();
        }
    }

    @Override
    public ScriptContext execute(Map<String, Object> parameters) {
        ScriptContext context = this.runtime().createScriptContext("");
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (context.interpreter() != null) {
                    context.interpreter().visitingScope().define(entry.getKey(), entry.getValue());
                }
                context.addResult(entry.getKey(), entry.getValue());
            }
        }
        return this.execute(context);
    }

    @Override
    public byte[] bytecode() {
        return this.bytecode;
    }

    @Override
    public ScriptContext evaluate() {
        return this.execute();
    }
}
