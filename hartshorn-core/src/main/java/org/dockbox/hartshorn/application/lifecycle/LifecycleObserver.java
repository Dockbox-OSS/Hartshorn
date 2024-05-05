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

package org.dockbox.hartshorn.application.lifecycle;

import org.dockbox.hartshorn.application.context.ApplicationContext;

/**
 * A lifecycle observer is notified when the application state changes. This can be used to implement
 * application-wide logic that needs to be executed when the application starts or stops.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface LifecycleObserver extends Observer {
    /**
     * Called when the application is started. This is called directly after the {@link ApplicationContext}
     * has been created and configured.
     *
     * @param applicationContext The application context
     */
    default void onStarted(ApplicationContext applicationContext) {}

    /**
     * Called when the application is stopped. This is called directly when the {@link Runtime#getRuntime() runtime}
     * is shutting down.
     *
     * @param applicationContext The application context
     */
    default void onExit(ApplicationContext applicationContext) {}
}
