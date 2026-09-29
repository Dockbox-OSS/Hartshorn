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

/**
 * Strategy contract for executing scripts across different execution backends (interpreted, compiled).
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ScriptExecutionStrategy {

    /**
     * The execution mode supported by this strategy.
     *
     * @return the execution mode
     */
    ExecutionMode mode();

    /**
     * Executes the script described in the given context.
     *
     * @param context the script context to execute
     * @return the executed context with results populated
     */
    ScriptContext execute(ScriptContext context);
}
