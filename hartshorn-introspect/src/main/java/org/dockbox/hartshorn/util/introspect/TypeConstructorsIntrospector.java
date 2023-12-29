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

import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.List;

import jakarta.inject.Inject;

/**
 * A type introspector that provides access to the constructors of a type. This is typically provided by a
 * {@link org.dockbox.hartshorn.util.introspect.view.TypeView} implementation.
 *
 * @param <T> the type to introspect
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface TypeConstructorsIntrospector<T> {

    /**
     * Returns the default constructor of the type, if available. The default constructor is the constructor
     * that has no parameters. If no constructors are explicitly defined, the default constructor is the
     * constructor that is provided by the compiler.
     *
     * @return the default constructor, if available
     */
    Option<ConstructorView<T>> defaultConstructor();

    /**
     * Returns all constructors that are annotated with the provided annotation. If no constructors are
     * annotated with the provided annotation, or if the annotation is not retained at whichever retention
     * policy is in effect, an empty list is returned.
     *
     * @param annotation the annotation to find constructors for
     * @return all constructors that are annotated with the provided annotation
     */
    List<ConstructorView<T>> annotatedWith(Class<? extends Annotation> annotation);

    /**
     * Returns all constructors that are annotated with the {@link Inject} annotation. If no constructors are
     * annotated with the {@link Inject} annotation, or if the annotation is not retained at whichever
     * retention policy is in effect, an empty list is returned.
     *
     * @return all constructors that are annotated with the {@link Inject} annotation
     */
    default List<ConstructorView<T>> injectable() {
        return this.annotatedWith(Inject.class);
    }

    /**
     * Returns the constructor that has the provided parameters. If no constructor is found, an empty option
     * is returned. The parameters are matched by type, in order of declaration.
     *
     * @param parameters the parameters to match
     * @return the constructor that has the provided parameters
     */
    default Option<ConstructorView<T>> withParameters(Class<?>... parameters) {
        return this.withParameters(List.of(parameters));
    }

    /**
     * Returns the constructor that has the provided parameters. If no constructor is found, an empty option
     * is returned. The parameters are matched by type, in order of declaration.
     *
     * @param parameters the parameters to match
     * @return the constructor that has the provided parameters
     */
    Option<ConstructorView<T>> withParameters(List<Class<?>> parameters);

    /**
     * Returns all constructors that are available on the type.
     *
     * @return all constructors that are available on the type
     */
    List<ConstructorView<T>> all();

    /**
     * Returns the number of constructors that are available on the type.
     *
     * @return the number of constructors that are available on the type
     */
    int count();

}
