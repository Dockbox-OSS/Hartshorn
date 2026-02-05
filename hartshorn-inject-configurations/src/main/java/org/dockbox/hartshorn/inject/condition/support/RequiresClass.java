/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.inject.condition.support;

import org.dockbox.hartshorn.inject.condition.RequiresReferenceCondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A condition that requires classes to be present in the classpath. As this condition is evaluated
 * early during the injection process, it does not require the classes to be loaded by the JVM, and
 * can therefore be used to conditionally load configurations based on optional dependencies.
 *
 * <p><b>Note, for binding methods</b>, take into account that the JVM will have loaded the
 * enclosing class, as well as the return type and any method references, before evaluating this
 * condition. As such, this condition is best applied to configuration classes. If used on binding
 * methods, ensure the return type and references do not depend on the checked classes. For such
 * cases, use separate conditionally loaded configuration classes.
 *
 * @see ClassCondition
 * 
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequiresReferenceCondition(condition = ClassCondition.class)
public @interface RequiresClass {

    /**
     * The classes that are required to be present.
     *
     * @return the classes that are required to be present
     */
    Class<?>[] classes() default {};

    /**
     * The fully qualified name of the classes that are required to be present.
     *
     * @return the fully qualified name of the classes that are required to be present
     */
    String[] classNames() default {};
}
