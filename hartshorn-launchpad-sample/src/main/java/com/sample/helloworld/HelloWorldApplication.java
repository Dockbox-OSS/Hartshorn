package com.sample.helloworld;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplication;

public class HelloWorldApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = HartshornApplication.create();
        applicationContext.get(GreetingAction.class).greet();
    }
}
