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

import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ExecutionMode;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.launchpad.ApplicationContext;

/**
 * Represents an interpreted HSL script that is evaluated using the AST interpreter.
 *
 * @see ExecutableScript
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class InterpretedScript extends ExecutableScript {

    protected InterpretedScript(ApplicationContext context, String source) {
        super(context, source);
    }

    @Override
    public ExecutionMode mode() {
        return ExecutionMode.INTERPRETED;
    }

    /**
     * Creates a new {@link InterpretedScript} from the given source and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param source the source of the script
     *
     * @return the created interpreted script
     */
    public static InterpretedScript of(ApplicationContext context, String source) {
        return new InterpretedScript(context, source);
    }

    /**
     * Creates a new {@link InterpretedScript} from the given file path and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param path the path to the file containing the source
     *
     * @return the created interpreted script
     *
     * @throws IOException if the file cannot be read
     */
    public static InterpretedScript of(ApplicationContext context, Path path) throws IOException {
        return of(context, sourceFromPath(path));
    }

    /**
     * Creates a new {@link InterpretedScript} from the given file and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param file the file containing the source
     *
     * @return the created interpreted script
     *
     * @throws IOException if the file cannot be read
     */
    public static InterpretedScript of(ApplicationContext context, File file) throws IOException {
        return of(context, file.toPath());
    }

    @Override
    public ScriptContext evaluate() {
        if (this.scriptContext() != null) {
            ScriptContext context = this.getOrCreateRuntime().runOnly(this.scriptContext(), Phase.EXECUTING);
            this.scriptContext(context);
        }
        else {
            this.resolve();
            this.evaluate();
        }
        return this.scriptContext();
    }
}
