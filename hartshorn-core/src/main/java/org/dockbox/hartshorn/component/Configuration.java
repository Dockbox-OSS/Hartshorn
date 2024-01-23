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

package org.dockbox.hartshorn.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * Indicates that a component is a configuration. Configurations are components that are used to configure other
 * components. Typically, this is done by offering {@link Binds binding providers}.
 *
 * <p>Configurations are not expected to have any dependencies, other than components that are provided by the
 * container during the configuration process. As such, configurations are never proxied or processed.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
@Component(
        singleton = true,
        permitProxying = false,
        permitProcessing = false
)
public @interface Configuration {

    /**
     * @see Component#id()
     * @return The id of the configuration.
     */
    String id() default "";

    /**
     * @see Component#name()
     * @return The name of the configuration.
     */
    String name() default "";
}
