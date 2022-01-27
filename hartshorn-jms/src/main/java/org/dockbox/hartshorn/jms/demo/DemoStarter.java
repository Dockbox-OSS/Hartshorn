package org.dockbox.hartshorn.jms.demo;

import org.dockbox.hartshorn.application.Activator;
import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.StartupModifiers;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jms.annotations.UseActiveMQ;

@Activator
@UseActiveMQ
public class DemoStarter {

    public static void main(final String[] args) {
        final ApplicationContext applicationContext = HartshornApplication.create(DemoStarter.class, new String[] {"--hartshorn.jms.url=tcp://localhost:61616"}, StartupModifiers.DEBUG);
        applicationContext.get(DemoSub.class).publish("Hello world!");
    }
}
