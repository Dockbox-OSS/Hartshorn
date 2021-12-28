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
 * Marks an annotation attribute to be alias for another attribute in the annotation hierarchy. For
 * example, if you have an annotation {@code @Foo(bar)}, and you have an extension annotation
 * {@code @Bar(baz)}, where {@code baz} is an alias for {@code bar}, then the annotations would look
 * like the following example.
 *
 * <pre>{@code
 * public @interface Foo {
 *     String bar();
 * }
 * }</pre>
 *
 * <pre>{@code
 * @Extends(Foo.class)
 * public @interface Bar {
 *     @AliasFor("bar")
 *     String baz();
 * }
 * }</pre>
 *
 * <p>If {@code Bar} extends other annotations with an attribute called {@code baz}, then the alias
 * will default to alias for all annotations in the hierarchy. If you want to alias for a specific
 * annotation, then you can use the {@link AliasFor#target()} attribute.
 *
 * @author Guus Lieben
 * @since 21.2
 * @see Extends
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AliasFor {

    /**
     * The name of the attribute to be aliased.
     * @return the name of the attribute to be aliased.
     */
    String value();

    /**
     * The target annotation for which the alias is defined.
     * @return the target annotation for which the alias is defined.
     */
    Class<?> target() default DefaultThis.class;

    final class DefaultThis {
    }
}
