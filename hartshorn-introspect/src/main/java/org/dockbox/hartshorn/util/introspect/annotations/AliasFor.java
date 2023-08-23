/*
 * Copyright 2019-2023 the original author or authors.
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
 * @since 0.4.1
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
