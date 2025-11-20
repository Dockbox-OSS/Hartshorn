package org.dockbox.sample.reporting;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.reporting.UseReporting;

@UseReporting
public class ReportingLaunchpadDemo {

    public static void main(String[] args) {
        ApplicationContext applicationContext = HartshornApplication.create(args);
        applicationContext.bind(String.class)
            .priority(100)
            .singleton("Hello 100");
        applicationContext.bind(String.class)
            .priority(50)
            .singleton("Hello 50");
        System.out.println();
    }
}
