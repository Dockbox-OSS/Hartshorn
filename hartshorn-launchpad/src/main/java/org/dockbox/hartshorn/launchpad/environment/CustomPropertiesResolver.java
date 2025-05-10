package org.dockbox.hartshorn.launchpad.environment;

import java.util.Properties;
import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.launchpad.properties.PropertySourceResolver;

/**
 * Functional interface for resolving custom properties. This interface is used to provide a mechanism for
 * resolving properties that do not reside in a specific resource or file, which would otherwise be resolved
 * through {@link PropertySourceResolver}s.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface CustomPropertiesResolver {

    /**
     * Resolves the properties for the application. This method is called during the initialization of the
     * application environment, and should not depend on other components or services.
     *
     * @return the resolved properties
     */
    Properties resolveProperties(SingleElementContext<ApplicationEnvironment> initializerContext);
}
