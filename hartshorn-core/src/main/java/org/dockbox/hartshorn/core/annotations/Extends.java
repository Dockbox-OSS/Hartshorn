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

import org.dockbox.hartshorn.core.annotations.stereotype.Component;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that the annotated annotation extends another annotation, inheriting its attributes.
 * Similar to class inheritance, if annotation X extends annotation Y, when searching Y
 * annotation, X annotation will also be returned.
 *
 * <p>A common example of this inheritence is {@link Service}, which extends {@link Component}.
 *
 * <p>If an attribute in the extended (Component) annotation is also present in the extending
 * (Service) annotation, the extending annotation will override the attribute value of the
 * extended annotation.
 *
 * @author Guus Lieben
 * @since 4.1.0
 * @see AliasFor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Extends {
    Class<? extends Annotation> value();
}
