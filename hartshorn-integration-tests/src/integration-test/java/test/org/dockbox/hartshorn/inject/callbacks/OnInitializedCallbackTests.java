package test.org.dockbox.hartshorn.inject.callbacks;

import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
public class OnInitializedCallbackTests {

    @Test
    @TestComponents(components = TypeWithPostConstructableInjectField.class)
    void testPostConstructInjectDoesNotInjectTwice(TypeWithPostConstructableInjectField instance) {
        Assertions.assertNotNull(instance);
        Assertions.assertNotNull(instance.postConstructableObject());
        Assertions.assertEquals(1, instance.postConstructableObject().getTimesConstructed());
    }
}
