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

package org.dockbox.hartshorn.util.introspect;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import java.util.Set;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Provides access to the methods of a type. A method is a function that is declared within a type. For example, in the
 * following declaration, {@code doBar} is a method:
 * <pre>{@code
 * public class Foo {
 *   public void doBar() {
 *   // ...
 *   }
 * }
 * }</pre>
 *
 * @param <T> the type of the element
 *
 * @see org.dockbox.hartshorn.util.introspect.view.TypeView#methods()
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface TypeMethodsIntrospector<T> {

    /**
     * Returns the method with the provided name, which has no parameters. If no method with the provided name exists,
     * an empty {@link Option} is returned.
     *
     * @param name the name of the method
     * @return the method with the provided name
     */
    default Option<MethodView<T, ?>> named(String name) {
        return this.named(name, List.of());
    }

    /**
     * Returns the method with the provided name and parameter types. If no method with the provided name and parameter
     * types exists, an empty {@link Option} is returned.
     *
     * @param name the name of the method
     * @param parameterTypes the parameter types of the method
     * @return the method with the provided name and parameter types
     */
    default Option<MethodView<T, ?>> named(String name, Class<?>... parameterTypes) {
        return this.named(name, List.of(parameterTypes));
    }

    /**
     * Returns the method with the provided name and parameter types. If no method with the provided name and parameter
     * types exists, an empty {@link Option} is returned.
     *
     * @param name the name of the method
     * @param parameterTypes the parameter types of the method
     * @return the method with the provided name and parameter types
     */
    Option<MethodView<T, ?>> named(String name, Collection<Class<?>> parameterTypes);

    /**
     * Returns all methods for the element. If the element does not declare or inherit any methods, an empty list is
     * returned. This includes all methods declared on the element, as well as all methods declared on all parent types
     * of the element.
     *
     * @return all methods for the element
     */
    List<MethodView<T, ?>> all();

    /**
     * Returns all methods declared on the element. If the element does not declare any methods, an empty list is
     * returned. This does not include any methods declared on parent types of the element.
     *
     * @return all methods declared on the element
     */
    List<MethodView<T, ?>> declared();

    /**
     * Returns all methods annotated with the provided annotation. If no methods are annotated with the provided
     * annotation, an empty list is returned.
     *
     * @param annotation the annotation to filter by
     * @return all methods annotated with the provided annotation
     */
    List<MethodView<T, ?>> annotatedWith(Class<? extends Annotation> annotation);

    /**
     * Returns all methods annotated with any of the provided annotations. If no methods are annotated with any of the
     * provided annotations, an empty list is returned.
     *
     * @param annotations the annotations to filter by
     * @return all methods annotated with any of the provided annotations
     */
    default List<MethodView<T, ?>> annotatedWithAny(Class<? extends Annotation>... annotations) {
        return this.annotatedWithAny(Set.of(annotations));
    }

    /**
     * Returns all methods annotated with any of the provided annotations. If no methods are annotated with any of the
     * provided annotations, an empty list is returned.
     *
     * @param annotations the annotations to filter by
     * @return all methods annotated with any of the provided annotations
     */
    List<MethodView<T, ?>> annotatedWithAny(Set<Class<? extends Annotation>> annotations);

    /**
     * Returns all methods annotated with all of the provided annotations. If no methods are annotated with all of the
     * provided annotations, an empty list is returned.
     *
     * @param annotations the annotations to filter by
     * @return all methods annotated with all of the provided annotations
     */
    default List<MethodView<T, ?>> annotatedWithAll(Class<? extends Annotation>... annotations) {
        return this.annotatedWithAll(Set.of(annotations));
    }

    /**
     * Returns all methods annotated with all of the provided annotations. If no methods are annotated with all of the
     * provided annotations, an empty list is returned.
     *
     * @param annotations the annotations to filter by
     * @return all methods annotated with all of the provided annotations
     */
    List<MethodView<T, ?>> annotatedWithAll(Set<Class<? extends Annotation>> annotations);

    /**
     * Returns all bridge methods for the element. If the element does not declare any bridge methods, an empty list is
     * returned. A bridge method is a method that is declared on a parent type, but is overridden by the element. For
     * example, in the following declaration, {@code Bar#doBar} is a bridge method:
     * <pre>{@code
     * public interface Foo {
     *     void doBar();
     * }
     * public interface Bar extends Foo {
     *    @Override
     *    void doBar();
     * }
     * }</pre>
     *
     * @return all bridge methods for the element
     */
    List<MethodView<T, ?>> bridges();
}
