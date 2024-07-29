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

package org.dockbox.hartshorn.inject.annotations.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.inject.condition.RequiresCondition;
import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * Indicates that a component is a configuration. Configurations provide declarations of component
 * bindings which should be made available to the application. For example:
 * <pre>{@code
 * @Configuration
 * public class ApplicationConfiguration {
 *    @Prototype
 *    public ComplexComponent complexComponent(...) {
 *       // ...
 *    }
 * }
 * }</pre>
 *
 * <p>Configurations should often be conditional, so that they are only loaded when the application
 * meets certain requirements. This can be achieved by annotating the configuration with {@link
 * RequiresCondition} or appropriate meta-annotations. For example:
 *
 * <pre>{@code
 * @RequiresActivator(UseBootstrap.class)
 * public class ApplicationConfiguration {
 *    // ...
 * }
 * }</pre>
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
@Component(
        lifecycle = LifecycleType.SINGLETON,
        permitProxying = false,
        lazy = false
)
public @interface Configuration {

    /**
     * @see Component#id()
     * @return The id of the configuration.
     */
    @AttributeAlias(value = "id", target = Component.class)
    String id() default "";

    /**
     * @see Component#name()
     * @return The name of the configuration.
     */
    @AttributeAlias(value = "name", target = Component.class)
    String name() default "";
}
