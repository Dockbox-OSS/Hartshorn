package org.dockbox.hartshorn.api;

import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Stream;

@ExtendWith(HartshornRunner.class)
public class ComponentProvisionTests {

    /**
     * Used to indicate which services should not be tested, typically because
     * they are targeted by other tests and/or are intended to fail.
     */
    private static final Collection<Class<?>> excluded = HartshornUtils.asList(
            UnactivatedPostBootstrapService.class,
            EmptyPostBootstrapService.class,
            ValidPostBootstrapService.class
    );

    public static Stream<Arguments> components() {
        return Hartshorn.context().locator()
                .containers().stream()
                .map(ComponentContainer::type)
                .filter(type -> !excluded.contains(type))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("components")
    public void testComponentProvision(Class<?> component) {
        Assertions.assertDoesNotThrow(() -> {
            final Object instance = Hartshorn.context().get(component);
            Assertions.assertNotNull(instance);
        });
    }
}
