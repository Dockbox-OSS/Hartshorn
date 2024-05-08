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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the value of a field or method should be processed by the specific
 * {@link ComponentPostConstructor}.
 *
 * <p>If the annotated element is a field, the value of the field will be enabled,
 * if it is not {@code null}.
 *
 * <p>If the annotated element is a method, the behavior is different depending on the
 * responsible {@link ComponentPostProcessor} which handles the method. Typically,
 * this will indicate that the result of the method will be enabled.
 *
 * <p>If the value of {@link #value()} is {@code true}, the annotated element will be
 * enabled. If the value is {@code false}, the annotated element will be not be enabled
 * automatically.
 *
 * @author Guus Lieben
 * @since 0.4.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Initialize {

    /**
     * Whether the annotated element should be enabled. Defaults to {@code true}.
     *
     * @return {@code true} if the annotated element should be enabled, {@code false} otherwise.
     */
    boolean value() default true;
}

