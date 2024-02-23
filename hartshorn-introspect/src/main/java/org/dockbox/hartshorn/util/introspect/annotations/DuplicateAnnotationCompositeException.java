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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Thrown when a duplicate annotation is found on a target. This can happen when an annotation is
 * extended by multiple annotations, and both of those annotations are used on the same target.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class DuplicateAnnotationCompositeException extends ApplicationRuntimeException {
    public DuplicateAnnotationCompositeException(Object target, List<? extends Annotation> annotations) {
        super("Found more than one annotation on " + target + ":\n"
                + annotations.stream().map(Annotation::toString).collect(Collectors.joining("\n")));
    }
}
