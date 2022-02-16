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

import org.dockbox.hartshorn.core.annotations.inject.InjectConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a class can be used as activation source by providing the
 * required metadata.
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Activator {

    /**
     * Whether to include the base package of the activator class explicitely, or to only
     * use the {@link #scanPackages() custom defined packages}.
     */
    boolean includeBasePackage() default true;

    /**
     * @return The default prefix for the activator. If this is left empty the package of the activation source is used
     */
    String[] scanPackages() default {};

    /**
     * @return The applicable {@link InjectConfig configurations} which should be used for this activator
     */
    InjectConfig[] configs() default {};
}
