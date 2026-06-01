package org.dockbox.sample.java;

import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.ApplicationStarter;
import org.dockbox.hartshorn.launchpad.HartshornApplication;

public class JavaLaunchpadDemo {

    void main(String[] args) {
        HartshornApplication.create(args);
    }

    @Singleton
    public ApplicationStarter applicationStarter() {
        return applicationContext -> {
            GreetingAction greetingAction = applicationContext.get(GreetingAction.class);
            greetingAction.greet();
        };
    }
}
