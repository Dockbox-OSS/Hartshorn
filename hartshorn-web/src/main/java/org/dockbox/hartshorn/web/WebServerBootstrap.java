package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
