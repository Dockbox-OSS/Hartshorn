/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.test.RequiresTestApplication;

/**
 * Configuration for web servers active within a test context.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresTestApplication
public class WebServerTestConfiguration {

    /**
     * Port provider that always provides a dynamic selection hint to the server. As this isn't an
     * exact port in itself, it remains up to the server implementation to resolve an available port
     * for the server to run on.
     *
     * @return a {@link ServerPortProvider} that always returns
     * {@link ServerPortProvider#DYNAMIC_SELECTION}
     */
    @Singleton
    @Priority(Priority.SUPPORT_PRIORITY + 16)
    public ServerPortProvider dynamicPortProvider() {
        return () -> ServerPortProvider.DYNAMIC_SELECTION;
    }
}
