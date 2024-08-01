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

package org.dockbox.hartshorn.hsl.runtime;

/**
 * Configuration for a {@link org.dockbox.hartshorn.hsl.interpreter.Interpreter interpreter}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ExecutionOptions {

    private boolean permitAmbiguousExternalFunctions = true;
    private boolean enableAssertions = true;

    /**
     * Whether ambiguous external functions should be permitted. If set to {@code false}, the interpreter
     * will throw an exception when it encounters functions with the same name in external modules.
     *
     * @return {@code true} if ambiguous external functions are permitted, {@code false} otherwise.
     */
    public boolean permitAmbiguousExternalFunctions() {
        return this.permitAmbiguousExternalFunctions;
    }

    /**
     * Sets whether ambiguous external functions should be permitted. If set to {@code false}, the interpreter
     * will throw an exception when it encounters functions with the same name in external modules.
     *
     * @param permitAmbiguousModuleFunctions {@code true} if ambiguous external functions are permitted, {@code false} otherwise.
     *
     * @return The current configuration.
     */
    public ExecutionOptions permitAmbiguousExternalFunctions(boolean permitAmbiguousModuleFunctions) {
        this.permitAmbiguousExternalFunctions = permitAmbiguousModuleFunctions;
        return this;
    }

    /**
     * Whether assertions are enabled. If set to {@code false}, the interpreter will not execute any assertions
     * in the script.
     *
     * @return {@code true} if assertions are enabled, {@code false} otherwise.
     */
    public boolean enableAssertions() {
        return this.enableAssertions;
    }

    /**
     * Sets whether assertions are enabled. If set to {@code false}, the interpreter will not execute any assertions
     * in the script.
     *
     * @param enableAssertions {@code true} if assertions are enabled, {@code false} otherwise.
     *
     * @return The current configuration.
     */
    public ExecutionOptions enableAssertions(boolean enableAssertions) {
        this.enableAssertions = enableAssertions;
        return this;
    }
}
