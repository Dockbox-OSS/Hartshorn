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

package org.dockbox.hartshorn.launchpad.condition;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.inject.condition.RequiresCondition;
import org.dockbox.hartshorn.launchpad.activation.ActivatorHolder;
import org.dockbox.hartshorn.launchpad.activation.ServiceActivator;
import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * A condition that requires an activator to be present in the {@link ActivatorHolder}.
 *
 * @see ActivatorCondition
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Extends(RequiresCondition.class)
@RequiresCondition(condition = ActivatorCondition.class)
public @interface RequiresActivator {

    /**
     * The type of the activator that is required to be present. The activator should be an annotation which itself
     * is annotated with {@link ServiceActivator}.
     *
     * @return the type of the activator that is required to be present
     */
    Class<? extends Annotation>[] value();

    /**
     * @see RequiresCondition#failOnNoMatch()
     * @return whether to fail on no match
     */
    @AttributeAlias(value = "failsOnNoMatch", target = RequiresCondition.class)
    boolean failOnNoMatch() default false;
}
