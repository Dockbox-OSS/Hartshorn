/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.logging;

import org.slf4j.Logger;

/**
 * The {@link ApplicationLogger} is a wrapper for the {@link Logger} class. This allows for modification and validation
 * of the logger used throughout an active application.
 *
 * @author Guus Lieben
 * @since 21.9
 */
@LogExclude
public interface ApplicationLogger {

    /**
     * Gets the logger.
     * @return The logger.
     */
    Logger log();

    /**
     * Sets whether the logger should log at debug level.
     * @param active Whether the logger should log at debug level.
     */
    void setDebugActive(boolean active);
}
