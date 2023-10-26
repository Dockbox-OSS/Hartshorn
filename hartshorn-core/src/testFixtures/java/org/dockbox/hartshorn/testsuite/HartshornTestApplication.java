package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.StandardApplicationContextConstructor;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import test.org.dockbox.hartshorn.ApplicationBatchingTest;

public class HartshornTestApplication {

    public static ApplicationContext createWithoutBasePackages() {
        return HartshornApplication.create(ApplicationBatchingTest.class, builder -> {
            builder.constructor(StandardApplicationContextConstructor.create(constructor -> {
                constructor.includeBasePackages(false);
                constructor.environment(ContextualApplicationEnvironment.create(ContextualApplicationEnvironment.Configurer::enableBatchMode));
            }));
        });
    }
}
