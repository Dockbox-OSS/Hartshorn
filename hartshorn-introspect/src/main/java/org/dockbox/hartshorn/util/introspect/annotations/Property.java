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

package org.dockbox.hartshorn.util.introspect.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Map;

/**
 * The interface to provide custom introspection information to an object field when creating
 * an object dynamically.
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

    /**
     * The alternative identifier for the field. For example a field called {@code firstName} which is
     * being injected into with a value of a property called {@code fn} could look like the following
     * example:
     *
     * <pre>{@code
     * @Property("fn")
     * private String firstName;
     * }</pre>
     *
     * @return the alternative identifier for the field
     */
    String name() default "";

    /**
     * The alternative setter for the field. When defined dynamic instance creators are able to
     * attempt to find a method which accepts a value of the type of the provided value.
     *
     * <pre>{@code
     * @Property(setter = "valueString")
     * private Integer id;
     *
     * public void setId(Integer id) {
     *     this.id = id;
     * }
     * }</pre>
     *
     * @return the name of the setter
     */
    String setter() default "";

    /**
     * The alternative getter for the field. When defined dynamic instance creators are able to
     * attempt to find this method which returns a value of the type of the field.
     *
     * <pre>{@code
     * @Property(getter = "getId")
     * private Integer id;
     *
     * public Integer getId() {
     *     return this.id;
     * }
     * }</pre>
     *
     * @return the name of the getter
     */
    String getter() default "";

    /**
     * Whether the field should be ignored when creating an object.
     *
     * @return whether the field should be ignored
     */
    boolean ignore() default false;

    /**
     * If the field is a {@link Map}, this value will be used as the key type if defined.
     *
     * @return the key type
     */
    Class<?> key() default Void.class;

    /**
     * If the field is a wrapper type (array, {@link Collection}, or {@link Map}), this value will be used as the
     * content type if defined.
     *
     * @return the content type
     */
    Class<?> content() default Void.class;

    /**
     * The type of the field. This can be used to narrow down the type of the field.
     *
     * @return the type of the field
     */
    Class<?> type() default Void.class;
}
