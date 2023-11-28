package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.StandardApplicationContextConstructor;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment.Configurer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentResolutionException;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.util.Tristate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornTest(includeBasePackages = false)
public class LooseInjectionTest {

    @InjectTest
    void testNonStrictModeMatchesCompatibleBinding(ApplicationContext context) {
        context.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class)
                .strict(false)
                .build();
        CharSequence sequence = context.get(key);
        Assertions.assertEquals("Hello World", sequence);
    }

    @InjectTest
    void testStrictModeOnlyMatchesExactBinding(ApplicationContext context) {
        context.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class)
                .strict(true)
                .build();
        Assertions.assertThrows(ComponentResolutionException.class, () -> context.get(key));
    }

    @Test
    void testStrictModeIsUndefinedByDefault() {
        ComponentKey<CharSequence> componentKey = ComponentKey.of(CharSequence.class);
        Assertions.assertSame(Tristate.UNDEFINED, componentKey.strict());
    }

    @Test
    void testEnvironmentStrictModeIsEnabledByDefault() {
        ApplicationEnvironment environment = HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.constructor(StandardApplicationContextConstructor.create(constructor -> {
                constructor.includeBasePackages(false);
            }));
        }).environment();
        Assertions.assertTrue(environment.isStrictMode());
    }

    public static void main(String[] args) {
        HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.constructor(StandardApplicationContextConstructor.create(constructor -> {
                constructor.includeBasePackages(false);
            }));
        });
    }


    @Test
    void testCustomizingEnvironmentStrictModeAffectsLookup() {
        ApplicationContext applicationContext = HartshornApplication.create(LooseInjectionTest.class, application -> {
            application.constructor(StandardApplicationContextConstructor.create(constructor -> {
                constructor.includeBasePackages(false);
                constructor.environment(ContextualApplicationEnvironment.create(Configurer::disableStrictMode));
            }));
        });
        ApplicationEnvironment environment = applicationContext.environment();
        Assertions.assertFalse(environment.isStrictMode());

        applicationContext.bind(String.class).singleton("Hello World");
        ComponentKey<CharSequence> key = ComponentKey.builder(CharSequence.class).build();
        Assertions.assertSame(Tristate.UNDEFINED, key.strict());

        CharSequence sequence = applicationContext.get(key);
        Assertions.assertEquals("Hello World", sequence);
    }
}
