package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.targets.InjectionPoint;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * Functional interface for resolving the declaration type of an injection point.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface InjectionPointDeclarationResolver {

    /**
     * Resolves the declaration type of the given injection point.
     *
     * @param injectionPoint the injection point to resolve
     * @return the type view representing the declaration type of the injection point
     */
    TypeView<?> resolve(InjectionPoint injectionPoint);
}
