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

package org.dockbox.hartshorn.component.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * A condition that requires classes to be present in the classpath.
 *
 * @see ClassCondition
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Extends(RequiresCondition.class)
@RequiresCondition(condition = ClassCondition.class)
public @interface RequiresClass {

    /**
     * The fully qualified name of the classes that are required to be present.
     *
     * @return the fully qualified name of the classes that are required to be present
     */
    @AttributeAlias(value = "failOnNoMatch", target = RequiresCondition.class)
    String[] value();

    /**
     * @see RequiresCondition#failOnNoMatch()
     * @return whether to fail on no match
     */
    @AttributeAlias(value = "failsOnNoMatch", target = RequiresCondition.class)
    boolean failOnNoMatch() default false;
}
