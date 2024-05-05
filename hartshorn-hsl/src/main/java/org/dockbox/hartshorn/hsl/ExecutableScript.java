/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;

/**
 * Represents a single executable HSL script. This is a wrapper around the HSL runtime and provides
 * a simple {@link ScriptContext} tracking the state of the execution. This class is ensured to be
 * thread-safe and should not be shared between threads.
 *
 * <p>It is recommended to use this class to execute HSL scripts, as it provides a simple and
 * consistent API for executing HSL scripts. The exposed {@link #runtime() script runtime} should
 * not be configured through this class, and should instead be configured through a {@link
 * org.dockbox.hartshorn.component.Configuration configuration class}.
 *
 * @see ScriptRuntime
 * @see ScriptContext
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ExecutableScript extends DefaultApplicationAwareContext {

    private final String source;

    private ScriptContext context;
    private ScriptRuntime runtime;

    protected ExecutableScript(ApplicationContext context, String source) {
        super(context);
        this.source = source;
    }

    /**
     * Creates a new {@link ExecutableScript} from the given source and the given {@link
     * ApplicationContext}.
     *
     * @param context The application context to use for execution
     * @param source The source of the script
     * @return A new {@link ExecutableScript} instance
     */
    public static ExecutableScript of(ApplicationContext context, String source) {
        return new ExecutableScript(context, source);
    }

    /**
     * Creates a new {@link ExecutableScript} from the given source and the given {@link Path}.
     * The source is read from the file. This method will throw an {@link IOException} if the file
     * cannot be read.
     *
     * @param context The application context to use for execution
     * @param path The path to the file containing the source
     * @return A new {@link ExecutableScript} instance
     * @throws IOException If the file cannot be read
     */
    public static ExecutableScript of(ApplicationContext context, Path path) throws IOException {
        return of(context, sourceFromPath(path));
    }

    /**
     * Creates a new {@link ExecutableScript} from the given source and the given {@link File}.
     * The source is read from the file. This method will throw an {@link IOException} if the file
     * cannot be read.
     *
     * @param context The application context to use for execution
     * @param file The file containing the source
     * @return A new {@link ExecutableScript} instance
     * @throws IOException If the file cannot be read
     */
    public static ExecutableScript of(ApplicationContext context, File file) throws IOException {
        return of(context, file.toPath());
    }

    /**
     * Reads the source from the given {@link Path} and returns it as a string. This method will
     * throw an {@link IOException} if the file cannot be read.
     *
     * @param path The path to the file containing the source
     * @return The source of the file as a string
     * @throws IOException If the file cannot be read
     */
    public static String sourceFromPath(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        return String.join("\n", lines);
    }

    /**
     * Creates a new {@link ScriptRuntime} instance. This method is called when the runtime is not
     * yet created. This method can be overridden to provide a custom runtime, but should not be
     * called by any method other than {@link #getOrCreateRuntime()}.
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
        }
        return this.runtime;
    }

    /**
     * Resolves the script. This method will run the script until semantics have been resolved. The
     * script will not be interpreted. This method will return the resolved {@link ScriptContext}.
     *
     * @return The resolved {@link ScriptContext}
     */
    public ScriptContext resolve() {
        this.context = this.getOrCreateRuntime().runUntil(this.source, Phase.RESOLVING);
        return this.context;
    }

    /**
     * Evaluates the script. This method will run the script until it has been interpreted. If the
     * script has not yet been resolved, this method will first resolve the script. This method will
     * immediately proceed to interpretation if the script has already been resolved.
     *
     * @return The evaluated {@link ScriptContext}
     */
    public ScriptContext evaluate() {
        if (this.context != null) {
            this.context = this.getOrCreateRuntime().runOnly(this.context, Phase.INTERPRETING);
        }
        else {
            this.context = this.resolve();
            this.context = this.evaluate();
        }
        return this.context;
    }

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
     * Returns the runtime of the script. This method will create the runtime if it does not yet
     * exist.
     *
     * @return The runtime of the script
     */
    public ScriptRuntime runtime() {
        return this.getOrCreateRuntime();
    }
}
