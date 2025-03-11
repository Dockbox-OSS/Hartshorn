package com.sample.helloworld;

import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;

@Configuration
public class HelloWorldConfiguration {

    @Singleton
    public GreetingAction greetingAction() {
        return () -> System.out.println("Hello World!");
    }
}
