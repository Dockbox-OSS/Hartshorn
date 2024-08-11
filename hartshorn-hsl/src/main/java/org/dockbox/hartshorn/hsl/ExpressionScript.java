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
import java.nio.file.Path;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;

/**
 * Specialization of {@link ExecutableScript} for {@link ValidateExpressionRuntime expression validation runtimes}.
 *
 * @see ExecutableScript
 * @see ValidateExpressionRuntime
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ExpressionScript extends ExecutableScript {

    protected ExpressionScript(ApplicationContext context, String source) {
        super(context, source);
    }

    /**
     * Creates a new {@link ExpressionScript} from the given source and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param source the source of the script
     * @return the created script
     */
    public static ExpressionScript of(ApplicationContext context, String source) {
        return new ExpressionScript(context, source);
    }

    /**
     * Creates a new {@link ExpressionScript} from the given source and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param path the path to the source of the script
     * @return the created script
     * @throws IOException if the source cannot be read
     */
    public static ExpressionScript of(ApplicationContext context, Path path) throws IOException {
        return of(context, sourceFromPath(path));
    }

    /**
     * Creates a new {@link ExpressionScript} from the given source and {@link ApplicationContext}.
     *
     * @param context the application context
     * @param file the file containing the source of the script
     * @return the created script
     * @throws IOException if the source cannot be read
     */
    public static ExpressionScript of(ApplicationContext context, File file) throws IOException {
        return of(context, file.toPath());
    }

    /**
     * Evaluates the script and returns whether the result is valid.
     *
     * @return whether the result is valid
     */
    public boolean valid() {
        ScriptContext context = this.evaluate();
        return valid(context);
    }

    /**
     * Returns whether the result of the given {@link ScriptContext} is valid.
     *
     * @param collector the result collector
     * @return whether the result is valid
     */
    public static boolean valid(ResultCollector collector) {
        return ValidateExpressionRuntime.valid(collector);
    }

    @Override
    protected ScriptRuntime createRuntime() {
        return this.applicationContext().get(ValidateExpressionRuntime.class);
    }
}
