package org.dockbox.sample.java;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.reporting.UseReporting;

@UseReporting
public class JavaLaunchpadDemo {

    public static void main(String[] args) {
        ApplicationContext applicationContext = HartshornApplication.create(args);
        applicationContext.get(GreetingAction.class).greet();
    }
}
