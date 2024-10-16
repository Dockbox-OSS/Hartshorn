package test.org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false, binderPostProcessors = SampleBinderPostProcessor.class)
public class BinderProcessorTests {

    @Test
    void testBinderPostProcessorIsCalled(@Inject HierarchicalBinder binder, @Inject ComponentProvider provider) {
        BindingHierarchy<String> hierarchy = binder.hierarchy(ComponentKey.of(String.class));
        Assertions.assertTrue(hierarchy.size() > 0);

        String message = provider.get(String.class);
        Assertions.assertEquals(SampleBinderPostProcessor.HELLO_WORLD, message);
    }
}
