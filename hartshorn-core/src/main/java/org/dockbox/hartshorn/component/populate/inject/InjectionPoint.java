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

package org.dockbox.hartshorn.component.populate.inject;

import org.dockbox.hartshorn.util.introspect.view.AnnotatedGenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Represents an injection point, which is a combination of a type and an annotated element. Typically,
 * the annotated element is a field or parameter, and the type is the type of the field or parameter.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public final class InjectionPoint {

    private final TypeView<?> type;
    private final AnnotatedGenericTypeView<?> injectionPoint;

    public InjectionPoint(AnnotatedGenericTypeView<?> injectionPoint) {
        this.type = injectionPoint.genericType();
        this.injectionPoint = injectionPoint;
    }

    /**
     * Returns the type of the injection point. This is typically the type of the {@link #injectionPoint()}.
     *
     * @return the type of the injection point
     */
    public TypeView<?> type() {
        return type;
    }

    /**
     * Returns the annotated element of the injection point. This is typically the field or parameter
     * that is being injected into.
     *
     * @return the annotated element of the injection point
     */
    public AnnotatedGenericTypeView<?> injectionPoint() {
        return this.injectionPoint;
    }
}
