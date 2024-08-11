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

package org.dockbox.hartshorn.launchpad.launch;

import java.util.List;

import org.dockbox.hartshorn.context.DefaultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic instruction context for the application. This context is used to determine the main class and arguments
 * passed to the application. Most likely this context is constructed based on the parameters provided to a
 * {@link ApplicationBuilder}. The amount of context provided is limited at this stage, as the application bootstrap
 * process is the first step in the application lifecycle.
 *
 * @see ApplicationBuilder
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ApplicationBuildContext extends DefaultContext {

    private final Class<?> mainClass;
    private final List<String> arguments;

    private final Logger logger;

    public ApplicationBuildContext(Class<?> mainClass, List<String> arguments) {
        this(mainClass, arguments, LoggerFactory.getLogger(mainClass));
    }

    public ApplicationBuildContext(Class<?> mainClass, List<String> arguments, Logger logger) {
        this.mainClass = mainClass;
        this.arguments = arguments;
        this.logger = logger;
    }

    /**
     * Returns the main class of the application. When this context is created, the validity of the main class is
     * verified. It is thus safe to assume that this class can be loaded, or otherwise accessed.
     *
     * @return The main class of the application.
     */
    public Class<?> mainClass() {
        return this.mainClass;
    }

    /**
     * Returns the arguments that were passed to the application. These arguments have not been parsed or validated,
     * and are provided as-is.
     *
     * @return The arguments that were passed to the application.
     */
    public List<String> arguments() {
        return this.arguments;
    }

    /**
     * Returns the logger to use for the bootstrap process. This logger is based on the main class of the application,
     * or otherwise a default logger.
     *
     * @return The logger to use for the bootstrap process.
     */
    public Logger logger() {
        return this.logger;
    }
}
