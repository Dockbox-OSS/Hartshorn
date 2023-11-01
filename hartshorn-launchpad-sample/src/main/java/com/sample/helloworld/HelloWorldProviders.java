package com.sample.helloworld;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Binds;

@Service
public class HelloWorldProviders {

    @Binds
    public GreetingAction greetingAction() {
        return () -> System.out.println("Hello World!");
    }
}
