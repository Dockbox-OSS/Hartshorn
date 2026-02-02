package org.dockbox.sample.web;

import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.reporting.UseReporting;
import org.dockbox.hartshorn.web.UseWebServer;

@UseWebServer
@UseReporting
public class SampleWebApplication {

    static void main(String[] args) {
        HartshornApplication.create(args);
    }
}
