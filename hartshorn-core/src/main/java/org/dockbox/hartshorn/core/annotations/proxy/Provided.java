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

package org.dockbox.hartshorn.core.annotations.proxy;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a method will return a provided object. The underlying method should not be
 * called, but the provided object should be returned instead. The provided object is obtained from the active
 * {@link ApplicationContext}. The return type of the method is used to determine the {@link Key} of the provided
 * object.
 *
 * <p>Example:
 * <pre>{@code
 * public interface MyService {
 *     @Provided
 *     SampleComponent component();
 * }
 * }</pre>
 *
 * @see org.dockbox.hartshorn.core.services.ContextMethodPostProcessor
 * @author Guus Lieben
 * @since 4.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Provided {

    /**
     * The name of the provided object, used to modify the {@link Key} of the provided object.
     *
     * @return The name of the provided object.
     */
    String value() default "";
}
