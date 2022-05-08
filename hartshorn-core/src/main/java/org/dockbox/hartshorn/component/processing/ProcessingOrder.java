/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.component.processing;

public class ProcessingOrder {

    public static final int FIRST = -256;
    public static final int EARLY = -128;
    public static final int NORMAL = 0;
    public static final int LATE = 128;
    public static final int LAST = 256;

    /**
     * Indicates which service orders can be performed during phase 1. During this phase, component
     * processors are allowed to discard existing instances and return new ones. This can be used to
     * create proxy instances.
     */
    public static final ProcessingPhase INITIALIZING = new ProcessingPhase("Initializing", i -> i < 0, true);

    /**
     * Indicates which service orders can be performed during phase 2. During this phase, component
     * processors are not allowed to discard existing instances and return new ones. This limits the
     * behavior of these processors to only return the same instance, albeit with different state.
     */
    public static final ProcessingPhase MODIFYING = new ProcessingPhase("Modifying", i -> i >= 0, false);

}
