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

package org.dockbox.hartshorn.application;

/**
 * A class that contains the default modifiers for the framework. Each entry may
 * modify the behavior of the framework in a specific way.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public enum StartupModifiers {
    /**
     * Makes it so application activators do not need to have service activator
     * annotationsWith present, and will indicate all activators are present when
     * requested.
     *
     * @since 21.2
     */
    ACTIVATE_ALL,

    /**
     * Makes it so the logging level of the application is changed to {@code DEBUG}.
     * This allows for finer logging and debugging.
     *
     * @since 22.1
     */
    DEBUG,
}
