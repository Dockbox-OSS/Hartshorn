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

package org.dockbox.hartshorn.inject.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.inject.populate.ComponentPopulator;
import org.dockbox.hartshorn.inject.populate.ComponentRequiredException;

/**
 * Indicates that a field or parameter is required. If the output of a binding is {@code null} after a
 * {@link ComponentPopulator} has attempted to populate it, a
 * {@link ComponentRequiredException} will be thrown.
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface Required {

    /**
     * If {@code true}, the annotated element will be required. If {@code false}, the annotated element will not be
     * required.
     *
     * @return {@code true} if the annotated element is required, {@code false} otherwise.
     */
    boolean value() default true;
}
