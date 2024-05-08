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
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;

/**
 * Marks a method as a method that should be invoked after the component has been initialized. A component is
 * considered initialized if it has been fully populated and processed by any applicable {@link ComponentPostProcessor}s
 * The method may have any parameters which match injectable components that can be provided through the current {@link
 * ComponentProvider}.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnInitialized {
}
