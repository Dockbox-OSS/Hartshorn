package org.dockbox.sample.proxies;

import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.sample.proxies.processing.UseLoggable;

@UseLoggable
public class ProxyingLaunchpadDemo {

    void main(String[] args) {
        HartshornApplication.create(args);
    }
}
