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

import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;
import org.dockbox.hartshorn.launchpad.ApplicationContext;

/**
 * Specialization of {@link ExecutableScript} for
 * {@link ValidateExpressionRuntime expression validation runtimes}.
 *
 * @see ExecutableScript
 * @see ValidateExpressionRuntime
 * @see CompiledExpressionScript
 * @see InterpretedExpressionScript
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public abstract class ExpressionScript extends ExecutableScript {

    protected ExpressionScript(ApplicationContext context, String source) {
        super(context, source);
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
     *
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
