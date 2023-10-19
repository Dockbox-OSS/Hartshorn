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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface ElementAnnotationsIntrospector {

    Set<Annotation> all();

    Set<Annotation> annotedWith(Class<? extends Annotation> annotation);

    boolean has(Class<? extends Annotation> annotation);

    default boolean hasAny(Class<? extends Annotation>... annotations) {
        return this.hasAny(Set.of(annotations));
    }

    boolean hasAny(Set<Class<? extends Annotation>> annotations);

    default boolean hasAll(Class<? extends Annotation>... annotations) {
        return this.hasAll(Set.of(annotations));
    }

    boolean hasAll(Set<Class<? extends Annotation>> annotations);

    <T extends Annotation> Option<T> get(Class<T> annotation);

    <T extends Annotation> Set<T> all(Class<T> annotation);

    int count();

}
