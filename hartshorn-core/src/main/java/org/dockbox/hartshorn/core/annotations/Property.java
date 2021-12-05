/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface to provide custom information to an object field when creating an object dynamically.
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
     * \@Property("fn")
     * private String firstName;
     * }</pre>
     *
     * @return the alternative identifier for the field
     */
    String value() default "";

    /**
     * The alternative setter for the field. When defined dynamic instance creators are able to
     * attempt to find a method which accepts a value of the type of the provided value.
     *
     * <pre>{@code
     * \@Property(setter = "valueString")
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
     * \@Property(getter = "getId")
     * private Integer id;
     *
     * public Integer getId() {
     *     return this.id;
     * }
     * }</pre>
     */
    String getter() default "";

    /**
     * Whether the field should be ignored when creating an object.
     */
    boolean ignore() default false;
}
