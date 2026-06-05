package org.dockbox.sample.web;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.reporting.UseReporting;
import org.dockbox.hartshorn.web.UseWebServer;

@UseWebServer
@UseReporting
public class SampleWebApplication {

    void main(String[] args) {
        ApplicationContext applicationContext = HartshornApplication.createApplication(args).initialize(application -> {
            application.includeBasePackages(false);
        });
        System.out.println();
    }
}
