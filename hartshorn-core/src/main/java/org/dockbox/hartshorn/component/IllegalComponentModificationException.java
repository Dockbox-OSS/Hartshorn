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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.reflect.TypeContext;

public class IllegalComponentModificationException extends ApplicationRuntimeException {

    public IllegalComponentModificationException(final String name, final int priority, final ComponentPostProcessor processor) {
        super("""
              Component %s was modified during phase with priority %s by %s.
              Component processors are only able to discard existing instances in phases with priority < 0.
              """.formatted(name, priority, TypeContext.of(processor).name()));

    }
}
