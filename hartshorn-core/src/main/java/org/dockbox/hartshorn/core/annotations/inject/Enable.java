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

package org.dockbox.hartshorn.core.annotations.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the value of a field or method should be enabled through
 * {@link org.dockbox.hartshorn.core.context.ApplicationContext#enable(Object)}.
 *
 * <p>If the annotated element is a field, the value of the field will be enabled,
 * if it is not {@code null}.
 *
 * <p>If the annotated element is a method, the behavior is different depending on the
 * responsible {@link org.dockbox.hartshorn.core.services.ComponentPostProcessor} which
 * handles the method. Typically, this will indicate that the result of the method will
 * be enabled.
 *
 * <p>If the value of {@link #value()} is {@code true}, the annotated element will be
 * enabled. If the value is {@code false}, the annotated element will be not be enabled
 * automatically.
 *
 * @author Guus Lieben
 * @since 21.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Enable {
    boolean value() default true;
}

