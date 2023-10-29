package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;

/**
 * Resolution strategy for dependencies. This can be used to determine how to proceed when
 * a circular dependency is detected.
 *
 * @author Guus Lieben
 * @see DependencyContext#needsImmediateResolution(ComponentKey)
 * @see DependencyContext#dependencies(DependencyResolutionType)
 * @since 0.5.0
 */
public enum DependencyResolutionType {

    /**
     * Dependency needs to be resolved immediately. Immediate means that the consumer requiring
     * the dependency cannot be instantiated or invoked without the dependency being available.
     * If a dependency can be provided after an initial phase of instantiation, it does not need
     * to be resolved immediately.
     *
     * <p>A common example of a dependency that needs to be resolved immediately is a constructor
     * parameter. A common example of a dependency that does not need to be resolved immediately
     * is a field that is injected after the constructor has been invoked.
     *
     * <p>Note that e.g. fields can only be lazily resolved, as long as the component provider
     * saves intermediate state. If the {@link org.dockbox.hartshorn.component.ComponentProvider}
     * is not able to save intermediate state, the dependency needs to be resolved immediately.
     */
    IMMEDIATE,

    /**
     * Dependency that can be lazily resolved. This means that the dependency does not need to
     * be available immediately, but can be provided at a later stage. This is typically the case
     * for fields or setters that are injected after the constructor has been invoked.
     *
     * <p>Note that lazy resolution is only possible, as long as the component provider saves
     * intermediate state. If the {@link org.dockbox.hartshorn.component.ComponentProvider} is
     * not able to save intermediate state, the dependency needs to be resolved immediately.
     */
    DELAYED,
}
