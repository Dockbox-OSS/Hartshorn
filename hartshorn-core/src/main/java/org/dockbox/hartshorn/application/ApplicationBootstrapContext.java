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

package org.dockbox.hartshorn.application;

import java.util.List;

import org.slf4j.Logger;

/**
 * Basic context for the application bootstrap process. This context is used to determine the main class and arguments
 * passed to the application, as well as whether or not to include the base packages of the main class. The amount of
 * context provided is limited at this stage, as the application bootstrap process is the first step in the application
 * lifecycle.
 *
 * @see ApplicationBuildContext
 * @see StandardApplicationContextConstructor
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ApplicationBootstrapContext extends ApplicationBuildContext {

    private final boolean includeBasePackages;

    public ApplicationBootstrapContext(Class<?> mainClass, List<String> arguments, Logger logger, boolean includeBasePackages) {
        super(mainClass, arguments, logger);
        this.includeBasePackages = includeBasePackages;
    }

    /**
     * Returns {@code true} if the base packages of the main class should be included in the application context.
     *
     * @return {@code true} if the base packages of the main class should be included in the application context.
     */
    public boolean includeBasePackages() {
        return this.includeBasePackages;
    }
}
