package org.dockbox.sample.java;

import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.ApplicationStarter;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.slf4j.Logger;

@UseCustomProcessors
public class ProcessingLaunchpadDemo {

    public static void main(String[] args) {
        HartshornApplication.create(args);
    }

    @Singleton
    public ApplicationStarter starter(Logger logger) {
        return applicationContext -> {
            // Binding defined in CustomBindingPostProcessor
            HelloWorldSupplier helloWorldSupplier = applicationContext.get(HelloWorldSupplier.class);
            logger.info(helloWorldSupplier.getHelloWorldMessage());
        };
    }
}
