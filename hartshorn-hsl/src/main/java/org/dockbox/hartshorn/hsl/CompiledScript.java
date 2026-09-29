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

package org.dockbox.hartshorn.hsl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.dockbox.hartshorn.hsl.compiler.ScriptCompiler;
import org.dockbox.hartshorn.hsl.compiler.StandardScriptCompiler;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ExecutionMode;
import org.dockbox.hartshorn.launchpad.ApplicationContext;

/**
 * Represents a compiled HSL script that can be compiled to JVM bytecode and executed.
 *
 * @see ExecutableScript
 * @see ScriptCompiler
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class CompiledScript extends ExecutableScript {

    private CompiledScript compiledDelegate;

    protected CompiledScript(ApplicationContext context, String source) {
        super(context, source);
    }

    @Override
    public ExecutionMode mode() {
        return ExecutionMode.COMPILED;
    }

    /**
     * Creates a new {@link CompiledScript} from the given source and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param source the source of the script
     *
     * @return the created compiled script
     */
    public static CompiledScript of(ApplicationContext context, String source) {
        return new CompiledScript(context, source);
    }

    /**
     * Creates a new {@link CompiledScript} from the given file path and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param path the path to the file containing the source
     *
     * @return the created compiled script
     *
     * @throws IOException if the file cannot be read
     */
    public static CompiledScript of(ApplicationContext context, Path path) throws IOException {
        return of(context, sourceFromPath(path));
    }

    /**
     * Creates a new {@link CompiledScript} from the given file and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param file the file containing the source
     *
     * @return the created compiled script
     *
     * @throws IOException if the file cannot be read
     */
    public static CompiledScript of(ApplicationContext context, File file) throws IOException {
        return of(context, file.toPath());
    }

    /**
     * Compiles the script into a compiled representation containing standard JVM bytecode.
     *
     * @return the compiled script instance
     */
    public CompiledScript compile() {
        if (this.compiledDelegate == null) {
            this.compiledDelegate = CompilationHelper.compile(this);
        }
        return this.compiledDelegate;
    }

    /**
     * Executes the compiled script with a newly created default context.
     *
     * @return the resulting script context
     */
    public ScriptContext execute() {
        return this.compile().execute();
    }

    /**
     * Executes the compiled script using the provided script context.
     *
     * @param context the context in which the script is executed
     *
     * @return the resulting script context
     */
    public ScriptContext execute(ScriptContext context) {
        this.scriptContext(context);
        return this.compile().execute(context);
    }

    /**
     * Executes the compiled script with the provided parameters as global variables.
     *
     * @param parameters variables to expose to the script
     *
     * @return the resulting script context
     */
    public ScriptContext execute(Map<String, Object> parameters) {
        return this.compile().execute(parameters);
    }

    /**
     * Returns the raw JVM bytecode for this compiled script class.
     *
     * @return the bytecode array
     */
    public byte[] bytecode() {
        return this.compile().bytecode();
    }

    @Override
    public ScriptContext evaluate() {
        if (this.scriptContext() == null) {
            this.resolve();
        }
        return this.compile().execute(this.scriptContext());
    }
}
