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

package org.dockbox.hartshorn.inject.condition;

import org.dockbox.hartshorn.inject.condition.support.RequiresClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A condition that requires conditions to be met based on type references. Unlike
 * {@link RequiresCondition}, this annotation is specifically designed to work with conditions that
 * evaluate type references, such as classes or interfaces, without needing to load the classes into
 * the JVM.
 *
 * <p>This approach has the benefit of avoiding class loading issues, such as
 * {@link ClassNotFoundException ClassNotFoundExceptions}, when checking for the presence of
 * classes that may not be available at runtime. By using type references, conditions can be
 * evaluated based on metadata alone, allowing for safer and more flexible condition checks. The
 * drawback is that the condition implementations cannot rely on loaded classes, and must
 * instead work with type metadata.
 *
 * <p>Due to the nature of type references, meta annotations of this annotation are supported, but
 * lack hierarchy support (i.e. meta-meta annotations and attribute aliases are not supported).
 *
 * <pre>{@code
 * @RequiresReferenceCondition(condition  = SampleCondition.class)
 * public @interface RequiresClass {
 *    ... additional attributes for your condition ...
 * }
 * }</pre>
 *
 * @see RequiresClass
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresReferenceCondition {

    /**
     * The condition that is required to be met. The condition should be a stateless class that
     * implements {@link Condition}.
     *
     * @return the condition that is required to be met
     */
    Class<? extends TypeReferenceCondition> condition();

    /**
     * Whether to fail on no match. If set to {@code true}, the operation will fail if the condition
     * is not met. It remains up to the implementation of the condition to determine what
     * constitutes a match.
     *
     * @return whether to fail on no match
     */
    boolean failOnNoMatch() default false;
}
