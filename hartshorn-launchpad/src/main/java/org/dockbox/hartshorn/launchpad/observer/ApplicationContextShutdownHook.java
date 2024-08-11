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

package org.dockbox.hartshorn.launchpad.observer;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.slf4j.Logger;

/**
 * A shutdown hook that closes the application context when the JVM is shutting down.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ApplicationContextShutdownHook implements Runnable {

    private final Logger logger;
    private final ApplicationContext applicationContext;

    public ApplicationContextShutdownHook(Logger logger, ApplicationContext applicationContext) {
        this.logger = logger;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        try {
            if (!this.applicationContext.isClosed()) {
                this.applicationContext.close();
            }
        }
        catch (ApplicationException e) {
            this.logger.error("Failed to close application context", e);
        }
    }
}
