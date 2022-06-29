package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * Standard implementation of {@link CodeCustomizer}, to simplify the registration of the
 * {@link CodeCustomizer}'s {@link Phase}.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public abstract class AbstractCodeCustomizer implements CodeCustomizer {

    private final Phase phase;

    protected AbstractCodeCustomizer(final Phase phase) {
        this.phase = phase;
    }

    @Override
    public Phase phase() {
        return this.phase;
    }
}
