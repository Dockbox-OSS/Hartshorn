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

import org.dockbox.hartshorn.hsl.condition.ScriptConditionContext;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.launchpad.context.ApplicationContextCarrier;

/**
 * Represents the runtime of a script, which can be used to execute scripts up to certain phases.
 *
 * <p>Script runtimes are themselves contexts, and can be used to carry context between script
 * executions and to provide context to the script.
 *
 * @see ScriptContext
 * @see Phase
 * @see ScriptConditionContext
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface ScriptRuntime extends ScriptConditionContext, ApplicationContextCarrier {

    /**
     * Executes the given script in its entirety, and returns the context that was created
     * during the execution. Execution includes all phases as defined in {@link Phase}.
     *
     * @param source the source code to execute
     *
     * @return the context that was created during the execution
     */
    ScriptContext interpret(String source);

    /**
     * Executes the script until the given phase (inclusive), and returns the context that
     * was created during the execution.
     *
     * @param source the source code to execute
     * @param until the phase until which the script should be executed
     *
     * @return the context that was created during the execution
     */
    ScriptContext runUntil(String source, Phase until);

    /**
     * Executes the given context until the given phase (inclusive), and returns the context
     * that was created during the execution.
     *
     * @param context the context to execute
     * @param until the phase until which the script should be executed
     *
     * @return the context that was created during the execution
     */
    ScriptContext runUntil(ScriptContext context, Phase until);

    /**
     * Executes only the given phase of the script, and returns the context that was created
     * during the execution. The script will not be executed beyond the given phase. Note that
     * this requires the script to have been executed up to the given phase before.
     *
     * @param source the source code to execute
     * @param only the phase to execute
     *
     * @return the context that was created during the execution
     */
    ScriptContext runOnly(String source, Phase only);

    /**
     * Executes only the given phase of the given context, and returns the context that was
     * created during the execution. The script will not be executed beyond the given phase.
     * Note that this requires the script to have been executed up to the given phase before.
     *
     * @param context the context to execute
     * @param only the phase to execute
     *
     * @return the context that was created during the execution
     */
    ScriptContext runOnly(ScriptContext context, Phase only);

}
