package org.dockbox.sample.java;

import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.slf4j.Logger;

@Component
public class SampleApplicationObserver implements LifecycleObserver {

    @Inject
    private Logger logger;

    @Override
    public void onStarted(ApplicationContext applicationContext) {
        this.logger.info("Application started");
    }

    @Override
    public void onExit(ApplicationContext applicationContext) {
        this.logger.info("Application is exiting");
    }
}
