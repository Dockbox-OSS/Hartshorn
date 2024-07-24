package test.org.dockbox.hartshorn.inject.context;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.populate.ComponentPopulator;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class ContextInjectionTests {

    @Inject
    private ComponentPopulator componentPopulator;

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testContextFieldsAreInjected() {
        String contextName = "InjectedContext";
        this.applicationContext.addContext(new SampleContext(contextName));

        ContextInjectedType instance = this.componentPopulator.populate(new ContextInjectedType());

        Assertions.assertNotNull(instance.context());
        Assertions.assertEquals(contextName, instance.context().name());
    }

    @Test
    void testNamedContextFieldsAreInjected() {
        String contextName = "InjectedContext";
        this.applicationContext.addContext("another", new SampleContext(contextName));

        ContextInjectedType instance = this.componentPopulator.populate(new ContextInjectedType());

        Assertions.assertNotNull(instance.anotherContext());
        Assertions.assertEquals(contextName, instance.anotherContext().name());
    }
}
