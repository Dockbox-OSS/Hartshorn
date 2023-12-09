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

package org.dockbox.hartshorn.component.populate.inject;

import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Represents an injection point, which is a combination of a type and an annotated element. Typically,
 * the annotated element is a field or parameter, and the type is the type of the field or parameter.
 *
 * @param type the type of the injection point
 * @param injectionPoint the annotated element of the injection point
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public record InjectionPoint(
        TypeView<?> type,
        AnnotatedElementView injectionPoint
) { }
