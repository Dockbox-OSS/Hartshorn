package org.dockbox.hartshorn.launchpad;

@FunctionalInterface
public interface ApplicationStarter {

    void run(ApplicationContext applicationContext);
}
