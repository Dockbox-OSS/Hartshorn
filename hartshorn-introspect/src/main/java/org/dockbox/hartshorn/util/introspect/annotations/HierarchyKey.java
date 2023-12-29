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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

/**
 * A key that is used to identify a specific annotation on a specific element. This key is used
 * to cache annotation proxies in {@link VirtualHierarchyAnnotationLookup}s.
 *
 * @param element The element on which the annotation is present
 * @param annotationType The type of annotation that is present on the element
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public record HierarchyKey(AnnotatedElement element, Class<? extends Annotation> annotationType) {

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        HierarchyKey key = (HierarchyKey) other;
        return Objects.equals(this.element, key.element) && Objects.equals(this.annotationType, key.annotationType);
    }

}
