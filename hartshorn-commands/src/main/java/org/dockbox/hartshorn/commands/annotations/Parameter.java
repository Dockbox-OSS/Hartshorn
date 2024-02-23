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

package org.dockbox.hartshorn.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.commands.arguments.CustomParameterPattern;
import org.dockbox.hartshorn.commands.arguments.HashtagParameterPattern;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * Used to indicate that a type can be provided to command definitions. When a type is decorated with this annotation, it can be automatically
 * constructed using its available constructors.
 *
 * <p>Also see <a href="https://github.com/GuusLieben/Hartshorn/wiki/Command-Arguments">Hartshorn/Command Arguments</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
@Component
public @interface Parameter {

    /**
     * The pattern to use for parsing the parameter. Defaults to {@link HashtagParameterPattern}.
     * @return the pattern
     */
    Class<? extends CustomParameterPattern> pattern() default HashtagParameterPattern.class;

    /**
     * The identifier of the parameter.
     * @return the identifier
     */
    @AttributeAlias(value = "id", target = Component.class)
    String value();

    /**
     * Descriptor of the parameter. This is used for generating help pages.
     *
     * @return the descriptor of the parameter
     *
     * @deprecated since 0.5.0, for removal in 0.6.0. No longer used, should be part of Javadocs of the parameter type or
     *             the given {@link #pattern()} implementation.
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    String usage() default "";

}
