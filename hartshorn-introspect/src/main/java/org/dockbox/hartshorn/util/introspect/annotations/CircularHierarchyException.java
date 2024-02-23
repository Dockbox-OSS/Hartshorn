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

package org.dockbox.hartshorn.util.introspect.annotations;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

import java.lang.annotation.Annotation;

/**
 * Thrown when a circular inheritance is detected in an annotation hierarchy. For example, when
 * {@link org.dockbox.hartshorn.util.introspect.annotations.Extends} is used to extend an annotation
 * that already extends the current annotation.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class CircularHierarchyException extends ApplicationRuntimeException {
    public CircularHierarchyException(Class<? extends Annotation> current) {
        super("Annotation hierarchy circular inheritance detected: " + current);
    }
}
