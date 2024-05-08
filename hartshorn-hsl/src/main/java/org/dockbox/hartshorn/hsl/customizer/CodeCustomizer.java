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

package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * Code customizers run during a specific {@link Phase phase} of a script's execution.
 * During the customization, the customizer can modify the script runtime's behavior,
 * either by modifying the script's AST or by modifying the various executors for each
 * phase.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface CodeCustomizer {

    /**
     * The phase of the script's execution during which the customizer should be run.
     * @return the phase of the script's execution during which the customizer should be run.
     */
    Phase phase();

    /**
     * Customize the script runtime's behavior during the given phase.
     * @param context the script context.
     */
    void call(ScriptContext context);

}
