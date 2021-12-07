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

import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a service method can be used as a factory method, capable of
 * creating instances of bound types through constructors annotated with {@link Bound}. This
 * allows you to manually inject values into a dependency, without knowing the exact type of the
 * dependency.
 *
 * <p>Factory methods are validated on startup, ensuring the parameters of the method match
 * the parameters of the bound type's constructor.
 *
 * <pre>{@code
 * @Service
 * interface CustomFactory {
 *     @Factory
 *     CustomType customType(String name);
 * }
 * }</pre>
 *
 * The above example will create a factory method for the {@code CustomType} type. The
 * implementation of this type can be configured through the active {@link ApplicationContext}.
 *
 * <pre>{@code
 * @Binds(CustomType.class)
 * class CustomTypeImpl implements CustomType {
 *     @Bound
 *     public CustomTypeImpl(String name) {
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * @author Guus Lieben
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface Factory {

    /**
     * If the binding of a factory method is based on a named dependency, this method returns the
     * name of the {@link org.dockbox.hartshorn.core.Key} to use.
     * @return The name of the {@link org.dockbox.hartshorn.core.Key} to use.
     */
    String value() default "";
}
