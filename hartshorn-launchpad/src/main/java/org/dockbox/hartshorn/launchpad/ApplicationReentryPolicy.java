package org.dockbox.hartshorn.launchpad;

/**
 * Behavior policies for re-entry of a closed {@link ApplicationContext}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public enum ApplicationReentryPolicy {
    /**
     * Ignore re-entry, and do not take further action (including the requested operation).
     */
    IGNORE,
    /**
     * Fail on re-entry, throwing a {@link ContextClosedException}.
     */
    FAIL,
}
