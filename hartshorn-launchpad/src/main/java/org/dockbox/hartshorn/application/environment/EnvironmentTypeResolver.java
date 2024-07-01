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

package org.dockbox.hartshorn.application.environment;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.util.Collection;
import java.util.List;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface EnvironmentTypeResolver {

    /**
     * Gets types decorated with a given annotation, both classes and annotations.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     * @return The annotated types
     */
    <A extends Annotation> Collection<TypeView<?>> types(Class<A> annotation);

    /**
     * Gets all sub-types of a given type. The prefix is typically a package. If no sub-types exist for the given type,
     * and empty list is returned.
     *
     * @param parent The parent type to scan for subclasses
     * @param <T> The type of the parent
     * @return The list of sub-types, or an empty list
     *
     * @deprecated Should not be used as it is not a common use-case. Instead, you should use a {@link ComponentPostProcessor}
     * to process managed components to find if they are a sub-type of a given type.
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    <T> Collection<TypeView<? extends T>> children(Class<T> parent);

    /**
     * Gets annotations of the given type, which are decorated with the given annotation. For example, given the
     * annotation and class below, the result of requesting annotations with {@link Documented} for {@code MyClass}
     * would be a list containing {@code MyAnnotation}, but not {@code AnotherAnnotation}.
     *
     * <pre>{@code
     * @Documented
     * public @interface MyAnnotation { }
     *
     * public @interface AnotherAnnotation { }
     *
     * @MyAnnotation
     * public class MyClass { }
     * }</pre>
     *
     * @param type The type to scan for annotations
     * @param annotation The annotation expected to be present on zero or more annotations
     * @return The annotated annotations
     *
     * @deprecated Should not be used, refer to {@link #types(Class)} if you need to find types with a specific annotation, or
     * {@link AnnotationLookup annotation lookups} if you need to find annotations on a specific type.
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    List<Annotation> annotationsWith(TypeView<?> type, Class<? extends Annotation> annotation);

    /**
     * Gets annotations of the given type, which are decorated with the given annotation. For example, given the
     * annotation and class below, the result of requesting annotations with {@link Documented} for {@code MyClass}
     * would be a list containing {@code MyAnnotation}, but not {@code AnotherAnnotation}.
     *
     * <pre>{@code
     * @Documented
     * public @interface MyAnnotation { }
     *
     * public @interface AnotherAnnotation { }
     *
     * @MyAnnotation
     * public class MyClass { }
     * }</pre>
     *
     * @param type The type to scan for annotations
     * @param annotation The annotation expected to be present on zero or more annotations
     * @return The annotated annotations
     *
     * @deprecated Should not be used, refer to {@link #types(Class)} if you need to find types with a specific annotation, or
     * {@link AnnotationLookup annotation lookups} if you need to find annotations on a specific type.
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    List<Annotation> annotationsWith(Class<?> type, Class<? extends Annotation> annotation);
}
