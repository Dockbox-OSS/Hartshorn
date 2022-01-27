/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.annotations.activate;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a {@link org.dockbox.hartshorn.core.services.ComponentProcessor} can be registered
 * automatically when detected on the classpath. This indicates the processor does not need to be registered manually.
 *
 * @see org.dockbox.hartshorn.core.boot.ApplicationFactory#serviceActivator(Annotation)
 * @see org.dockbox.hartshorn.core.services.ComponentProcessor
 * @author Guus Lieben
 * @since 21.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutomaticActivation {
    boolean value() default true;
}
