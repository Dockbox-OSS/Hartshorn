package org.dockbox.sample.java;

import org.dockbox.hartshorn.inject.annotations.PropertyValue;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.annotations.LoggerMeta;
import org.slf4j.Logger;

@Configuration
public class GreetingConfiguration {

    @Singleton(lazy = true)
    public GreetingAction greetingAction(
            @PropertyValue(name = "greetings.hello", defaultValue = "Hello there, {}") String helloGreetingTemplate,
            @LoggerMeta(name = "Greeting implementation") Logger logger
    ) {
        return () -> logger.info(helloGreetingTemplate, "World");
    }
}
