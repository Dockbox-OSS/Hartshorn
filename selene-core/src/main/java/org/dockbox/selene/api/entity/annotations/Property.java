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

package org.dockbox.selene.api.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface to provide custom information to a object field when creating a object dynamically.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {
    /**
     * The alternative identifier for the field. For example a field called {@code firstName} which is
     * being injected into by a property called {@code fn} could like like the following example:
     *
     * <pre>{@code
     * \@Property("fn")
     * private String firstName;
     * }</pre>
     *
     * @return the alternative identifier for the field
     */
    String value();

    /**
     * The alternative setter for the field. When defined dynamic instance creators are able to
     * attempt to find a method which accepts a value of the type of the provided value. This
     * means it is possible to directly apply a given {@link String} value to {@link Integer}
     * field. Note that type differences are only accepted when defined with {@link Property#accepts()}.
     *
     * <pre>{@code
     * \@Property(setter = "setValueString")
     * private Integer id;
     *
     * public void setValueString(String value) {
     * this.id = Integer.parseInt(value);
     * }
     * }</pre>
     *
     * @return the name of the setter
     */
    String setter() default "";

    /**
     * Marks the accepted value of the field. When defined this allows {@link Property#setter()} to
     * accept a type different from the field type.
     *
     * @return the type to accept
     */
    Class<?> accepts() default Void.class;
}
