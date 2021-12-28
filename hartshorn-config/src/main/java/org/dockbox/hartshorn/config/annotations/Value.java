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

package org.dockbox.hartshorn.config.annotations;

import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a field should be populated with the value obtained
 * from {@link org.dockbox.hartshorn.core.context.ApplicationPropertyHolder#property(String)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Value {

    /**
     * The key of the configuration value. Nested values are separated by a single
     * period symbol. For example, in the configuration (JSON) below the deepest
     * value is accessed with <code>config.nested.value</code>, returning the value 'A'
     * <pre><code>
     *     { "config": {     "nested": {         "value": "A"     } }
     *     }
     * </code></pre>
     *
     * @return The configuration key.
     */
    String value();

    /**
     * The default value for the field, used when the result of {@link org.dockbox.hartshorn.core.context.ApplicationPropertyHolder#property(String)}
     * returned <code>null</code>. Supports native types through {@link org.dockbox.hartshorn.core.context.element.TypeContext#toPrimitive(TypeContext, String)}.
     *
     * @return The string-based default value.
     */
    @Deprecated(since = "22.1", forRemoval = true)
    String or() default "";
}
