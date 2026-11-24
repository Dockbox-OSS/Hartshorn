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

package org.dockbox.hartshorn.hsl.modules;

import org.dockbox.hartshorn.hsl.customizer.ScriptContext;

/**
 * Standard library granting access to environment variables.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class SystemLibrary {

    private final ScriptContext context;

    public SystemLibrary(ScriptContext context) {
        this.context = context;
    }

    /**
     * @param program The name of the environment variable to retrieve.
     *
     * @return The value of the environment variable, or {@code null} if the variable is not
     * defined.
     *
     * @see System#getenv(String)
     */
    public String env(String program) {
        return System.getenv(program);
    }

    /**
     * Prints the given object to the logger associated with the current script.
     *
     * @param object The object to print.
     */
    public void print(Object object) {
        this.context.outputCapture().print(object);
    }
}
