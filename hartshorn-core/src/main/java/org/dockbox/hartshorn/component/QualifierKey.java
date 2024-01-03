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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.inject.Named;
import org.dockbox.hartshorn.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record QualifierKey<T>(Class<T> type, Map<String, Object> meta) {

    public static <T extends Annotation> QualifierKey<T> of(T annotation) {
        Class<T> annotationType = TypeUtils.adjustWildcards(annotation.annotationType(), Class.class);
        Map<String, Object> annotationValues = TypeUtils.getAttributes(annotation);
        return of(annotationType, annotationValues);
    }

    public static <T extends Annotation> QualifierKey<T> of(Class<T> type, Map<String, Object> meta) {
        checkValidMetadata(type, meta);
        return new QualifierKey<>(type, meta);
    }

    public static QualifierKey<Named> of(String qualifier) {
        return of(Named.class, Map.of("value", qualifier));
    }

    private static <T> void checkValidMetadata(Class<T> type, Map<String, Object> meta) {
        Method[] methods = type.getDeclaredMethods();
        if (methods.length != meta.size()) {
            throw new IllegalArgumentException("Meta size does not match method count on type '" + type.getSimpleName() + "'");
        }

        Set<String> methodNames = Arrays.stream(methods)
                .map(Method::getName)
                .collect(Collectors.toSet());
        for (String metaName : meta.keySet()) {
            if (!methodNames.contains(metaName)) {
                throw new IllegalArgumentException("Meta key '" + metaName + "' is not a valid method name on type '" + type.getSimpleName() + "'");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QualifierKey<?> qualifierKey = (QualifierKey<?>) o;
        return Objects.equals(this.type, qualifierKey.type) && Objects.equals(this.meta, qualifierKey.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.meta);
    }

    @Override
    public String toString() {
        String metaString = this.meta.entrySet().stream()
                .map(entry -> "%s=%s".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));
        return "%s{%s}".formatted(this.type.getSimpleName(), metaString);
    }
}
