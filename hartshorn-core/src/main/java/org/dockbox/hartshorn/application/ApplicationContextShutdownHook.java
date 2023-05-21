package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.slf4j.Logger;

public class ApplicationContextShutdownHook implements Runnable {
    private final Logger logger;
    private final ApplicationContext applicationContext;

    public ApplicationContextShutdownHook(final Logger logger, final ApplicationContext applicationContext) {
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
        catch (final ApplicationException e) {
            this.logger.error("Failed to close application context", e);
        }
    }
}
