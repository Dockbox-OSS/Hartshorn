package org.dockbox.hartshorn.util;

/**
 * A functional interface for customizing objects. This interface is similar to {@link java.util.function.Consumer} but
 * allows for composition of customizers, and is the common interface for all customizers in Hartshorn.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public interface Customizer<T extends Configurer> {

    /**
     * Configures the given target object. Implementations of this method may access the target object directly, and
     * configure it as necessary.
     *
     * @param target The object to configure.
     */
    void configure(T target);

    /**
     * Returns a customizer that composes this customizer with the given customizer. When the returned customizer is
     * invoked, the given customizer is invoked first, and then this customizer is invoked.
     *
     * @param before The customizer to invoke first.
     * @return A customizer that composes this customizer with the given customizer.
     */
    default Customizer<T> compose(Customizer<T> before) {
        return (T target) -> {
            before.configure(target);
            this.configure(target);
        };
    }

    /**
     * Returns a customizer that does nothing. This can be used to accept the default configuration without
     * further modification.
     *
     * @return A customizer that does nothing.
     * @param <T> The type of object to customize.
     */
    static <T extends Configurer> Customizer<T> useDefaults() {
        return target -> {};
    }
}
