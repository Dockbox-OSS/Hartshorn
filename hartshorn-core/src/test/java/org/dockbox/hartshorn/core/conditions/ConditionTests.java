package org.dockbox.hartshorn.core.conditions;

import org.dockbox.hartshorn.application.ApplicationFactory;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.testsuite.HartshornFactory;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import javax.inject.Inject;

@HartshornTest
@TestComponents(ConditionalProviders.class)
public class ConditionTests {

    @Inject
    private ApplicationContext applicationContext;

    @HartshornFactory
    public static ApplicationFactory<?, ?> factory(final ApplicationFactory<?, ?> factory) {
        return factory.arguments("--property.c=o",
                "--property.d=d",
                "--property.e=otherValue");
    }

    public static Stream<Arguments> providers() {
        return Stream.of(
                Arguments.of("a", true),
                Arguments.of("b", false),
                Arguments.of("c", true),
                Arguments.of("d", true),
                Arguments.of("e", false),
                Arguments.of("f", true)
        );
    }

    @ParameterizedTest
    @MethodSource("providers")
    void test(final String name, final boolean present) {
        final Key<String> key = Key.of(String.class, name);
        final BindingHierarchy<String> hierarchy = this.applicationContext.hierarchy(key);
        Assertions.assertEquals(present ? 1 : 0, hierarchy.size());

        final String value = this.applicationContext.get(key);
        if (present) {
            Assertions.assertEquals(name, value);
        }
        else {
            Assertions.assertEquals("", value); // Default value, not null
        }
    }
}
