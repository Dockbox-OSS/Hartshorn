/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ProcessingPhase;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;

public class IllegalPhaseModificationException extends ApplicationRuntimeException {
    public IllegalPhaseModificationException(final ComponentPostProcessor processor, final ProcessingPhase previous, final ProcessingPhase current) {
        super("Post-processor %s changed the processing phase from %s to %s".formatted(processor.getClass().getSimpleName(), previous, current));
    }
}

