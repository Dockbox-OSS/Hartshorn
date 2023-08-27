package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.application.StandardApplicationBuilder;
import org.dockbox.hartshorn.application.StandardApplicationContextConstructor;
import org.dockbox.hartshorn.application.context.SimpleApplicationContext;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.util.Customizer;

public final class TestCustomizer<T> {

    public static final TestCustomizer<StandardApplicationBuilder.Configurer> BUILDER = new TestCustomizer<>();
    public static final TestCustomizer<ContextualApplicationEnvironment.Configurer> ENVIRONMENT = new TestCustomizer<>();
    public static final TestCustomizer<StandardApplicationContextConstructor.Configurer> CONSTRUCTOR = new TestCustomizer<>();
    public static final TestCustomizer<SimpleApplicationContext.Configurer> APPLICATION_CONTEXT = new TestCustomizer<>();

    private final Customizer<T> customizer = Customizer.useDefaults();

    private TestCustomizer() {
        // Private constructor, use static instances
    }

    public void compose(Customizer<T> customizer) {
        this.customizer.compose(customizer);
    }

    Customizer<T> customizer() {
        return this.customizer;
    }
}
