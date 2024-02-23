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

package org.dockbox.hartshorn.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * The annotation used to mark a method or class as a command holder.
 *
 * @see <a href="https://github.com/GuusLieben/Hartshorn/wiki/Commands">Commands</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Extends(Service.class)
public @interface Command {

    /**
     * The aliases for the command.
     *
     * @return the aliases
     */
    String[] value() default "";

    /**
     * The argument context for the command. If the default value is used no arguments will be validated,
     * delivering the same result as making it equal to the primary alias.
     *
     * @return the argument context for the command.
     * @see <a href="https://github.com/GuusLieben/Hartshorn/wiki/Commands#defining-command-usage">Commands#defining-command-arguments</a>
     */
    String arguments() default "";

    /**
     * The parent command for the command. When specified, the command will be executed as a sub-command.
     *
     * @return the parent command for the command.
     */
    @AttributeAlias("owner")
    Class<?> parent() default Void.class;

    /**
     * @return whether the command should be lazy loaded or not.
     *
     * @see Service#lazy()
     */
    boolean lazy() default false;
}
