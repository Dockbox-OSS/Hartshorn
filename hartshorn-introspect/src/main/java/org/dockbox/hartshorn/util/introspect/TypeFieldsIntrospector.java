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
import java.util.List;

import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Provides access to the fields of a type. A field is a variable that is declared within a type. For example, in the
 * following declaration, {@code foo} is a field:
 * <pre>{@code
 * public class Bar {
 *    private String foo;
 *    // ...
 * }
 * }</pre>
 *
 * @param <T> the type of the element
 *
 * @see org.dockbox.hartshorn.util.introspect.view.TypeView#fields()
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface TypeFieldsIntrospector<T> {

    /**
     * Returns the field with the provided name. If no field with the provided name exists, an empty {@link Option} is
     * returned.
     *
     * @param name the name of the field
     * @return the field with the provided name
     */
    Option<FieldView<T, ?>> named(String name);

    /**
     * Returns all fields for the element. If the element does not declare any fields, an empty list is returned.
     *
     * @return all fields for the element
     */
    List<FieldView<T, ?>> all();

    /**
     * Returns all fields annotated with the provided annotation. If no fields are annotated with the provided
     * annotation, an empty list is returned.
     *
     * @param annotation the annotation to filter by
     * @return all fields annotated with the provided annotation
     */
    List<FieldView<T, ?>> annotatedWith(Class<? extends Annotation> annotation);
}
