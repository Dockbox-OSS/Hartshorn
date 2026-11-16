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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ExecutionMode;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.context.DefaultApplicationAwareContext;

/**
 * Base representation of a single executable HSL script. This is a wrapper around the HSL runtime
 * and provides a {@link ScriptContext} tracking the state of the execution.
 *
 * <p>Specific execution modes are separated into {@link CompiledScript} and {@link InterpretedScript}.
 *
 * @see ScriptRuntime
 * @see ScriptContext
 * @see CompiledScript
 * @see InterpretedScript
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public abstract class ExecutableScript extends DefaultApplicationAwareContext {

    private final String source;

    private ScriptContext context;
    private ScriptRuntime runtime;

    protected ExecutableScript(ApplicationContext context, String source) {
        super(context);
        this.source = source;
    }

    /**
     * Returns the execution mode for this script.
     *
     * @return the execution mode
     */
    public abstract ExecutionMode mode();

    /**
     * Reads the source from the given {@link Path} and returns it as a string.
     *
     * @param path The path to the file containing the source
     *
     * @return The source of the file as a string
     *
     * @throws IOException If the file cannot be read
     */
    public static String sourceFromPath(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        return String.join("\n", lines);
    }

    /**
     * Creates a new {@link ScriptRuntime} instance. This method is called when the runtime is not
     * yet created.
     *
     * @return A new {@link ScriptRuntime} instance
     */
    protected ScriptRuntime createRuntime() {
        return this.applicationContext().get(ScriptRuntime.class);
    }

    /**
     * Returns the runtime, creating it if it does not yet exist.
     *
     * @return The runtime
     */
    protected ScriptRuntime getOrCreateRuntime() {
        if (this.runtime == null) {
            this.runtime = this.createRuntime();
            this.runtime.executionMode(this.mode());
        }
        return this.runtime;
    }

    /**
     * Sets the script runtime.
     *
     * @param runtime the script runtime
     */
    protected void runtime(ScriptRuntime runtime) {
        this.runtime = runtime;
        if (runtime != null) {
            runtime.executionMode(this.mode());
        }
    }

    /**
     * Resolves the script. This method will run the script until semantics have been resolved.
     *
     * @return The resolved {@link ScriptContext}
     */
    public ScriptContext resolve() {
        this.context = this.getOrCreateRuntime().runUntil(this.source, Phase.SEMANTIC_ANALYSIS);
        return this.context;
    }

    /**
     * Evaluates the script according to the configured execution mode.
     *
     * @return The evaluated {@link ScriptContext}
     */
    public abstract ScriptContext evaluate();

    /**
     * Returns the source of the script.
     *
     * @return The source of the script
     */
    public String source() {
        return this.source;
    }

    /**
     * Returns the context of the script. This method will return {@code null} if the script has not
     * yet been resolved or evaluated.
     *
     * @return The context of the script
     */
    public ScriptContext scriptContext() {
        return this.context;
    }

    /**
     * Sets the context of the script.
     *
     * @param context the context of the script
     */
    protected void scriptContext(ScriptContext context) {
        this.context = context;
    }

    /**
     * Returns the runtime of the script. This method will create the runtime if it does not yet
     * exist.
     *
     * @return The runtime of the script
     */
    public ScriptRuntime runtime() {
        return this.getOrCreateRuntime();
    }
}
