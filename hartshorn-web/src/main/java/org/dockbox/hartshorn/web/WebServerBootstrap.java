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

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A bootstrap class responsible for starting and stopping the web server during the application
 * lifecycle.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class WebServerBootstrap implements LifecycleObserver {

    private static final Logger LOG = LoggerFactory.getLogger(WebServerBootstrap.class);

    @Override
    public void onStarted(ApplicationContext applicationContext) {
        try {
            WebServer webServer = applicationContext.get(WebServer.class);
            webServer.start();
            LOG.info("Web server started on port {}", webServer.port());
        }
        catch (ServerException e) {
            applicationContext.handle("Failed to start web server", e);
        }
    }

    @Override
    public void onExit(ApplicationContext applicationContext) {
        try {
            WebServer webServer = applicationContext.get(WebServer.class);
            webServer.stop();
        }
        catch (ServerException e) {
            applicationContext.handle("Failed to stop web server", e);
        }
    }
}
