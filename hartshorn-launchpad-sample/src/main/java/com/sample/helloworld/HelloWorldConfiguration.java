package com.sample.helloworld;

import org.dockbox.hartshorn.inject.annotations.PropertyValue;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.slf4j.Logger;

@Configuration
public class HelloWorldConfiguration {

    @Singleton(lazy = true)
    public GreetingAction greetingAction(
            @PropertyValue(name = "greetings.hello", defaultValue = "Hello there, {}") String helloGreetingTemplate,
            Logger logger
    ) {
        return () -> logger.info(helloGreetingTemplate, "World");
    }
}
