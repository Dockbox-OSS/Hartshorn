package test.org.dockbox.hartshorn.inject.strategies;

import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.inject.stereotype.ComponentType;
import test.org.dockbox.hartshorn.inject.context.SampleContext;

@HartshornIntegrationTest(includeBasePackages = false)
public class SetterInjectionTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = { SetterInjectedComponent.class, ComponentType.class})
    void testSetterInjectionWithRegularComponent() {
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.component());
    }

    @Test
    @TestComponents(components = SetterInjectedComponentWithAbsentBinding.class)
    void testSetterInjectionWithAbsentRequiredComponent() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(SetterInjectedComponentWithAbsentBinding.class));
    }

    @Test
    @TestComponents(components = SetterInjectedComponentWithNonRequiredAbsentBinding.class)
    void testSetterInjectionWithAbsentComponent() {
        var component = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(SetterInjectedComponentWithNonRequiredAbsentBinding.class));
        Assertions.assertNotNull(component);
        Assertions.assertNull(component.object());
    }

    @Test
    @TestComponents(components = {SetterInjectedComponent.class, ComponentType.class})
    void testSetterInjectionWithContext() {
        SampleContext sampleContext = new SampleContext("setter");
        this.applicationContext.addContext("setter", sampleContext);
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.context());
        Assertions.assertSame(sampleContext, component.context());
    }
}
