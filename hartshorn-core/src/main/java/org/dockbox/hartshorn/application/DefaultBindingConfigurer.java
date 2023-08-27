package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.binding.Binder;

/**
 * A functional interface used to configure the default bindings of the {@link ContextualEnvironmentBinderConfiguration}.
 * This interface should be used to configure the default bindings of the application. This interface is typically
 * provided by the bootstrap {@link org.dockbox.hartshorn.util.Customizer} for the {@link ApplicationContext}.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
@FunctionalInterface
public interface DefaultBindingConfigurer {

    /**
     * Configures the default bindings of the application.
     */
    void configure(Binder binder);

    /**
     * Returns a new {@link DefaultBindingConfigurer} that will invoke this configurer, and then the given configurer.
     * This is useful for composing multiple configurers together.
     *
     * @param other The other configurer to invoke.
     * @return A new {@link DefaultBindingConfigurer} that will invoke this configurer, and then the given configurer.
     */
    default DefaultBindingConfigurer compose(final DefaultBindingConfigurer other) {
        return binder -> {
            this.configure(binder);
            other.configure(binder);
        };
    }

    /**
     * Returns a new {@link DefaultBindingConfigurer} that will exit without configuring anything. This is useful for
     * providing a default value for a {@link DefaultBindingConfigurer} parameter.
     *
     * @return A new {@link DefaultBindingConfigurer} that will exit without configuring anything.
     */
    static DefaultBindingConfigurer empty() {
        return binder -> {};
    }
}
