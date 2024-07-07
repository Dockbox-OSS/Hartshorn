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

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers a shutdown hook for the application context when the application starts. This hook will ensure that the
 * application context is closed when the JVM is shutting down.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class RuntimeHookLifecycleObserver implements LifecycleObserver {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeHookLifecycleObserver.class);

    @Override
    public void onStarted(ApplicationContext applicationContext) {
        LOG.debug("Registering shutdown hook for application context");
        ApplicationContextShutdownHook shutdownHook = new ApplicationContextShutdownHook(LOG, applicationContext);
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook, "ShutdownHook"));
    }
}
