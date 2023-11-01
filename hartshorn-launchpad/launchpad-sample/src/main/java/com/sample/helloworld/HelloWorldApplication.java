package com.sample.helloworld;

import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.context.ApplicationContext;

public class HelloWorldApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = HartshornApplication.create();
        applicationContext.get(GreetingAction.class).greet();
    }
}
