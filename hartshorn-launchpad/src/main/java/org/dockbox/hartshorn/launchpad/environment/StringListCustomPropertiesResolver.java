package org.dockbox.hartshorn.launchpad.environment;

import java.util.List;
import org.dockbox.hartshorn.context.SingleElementContext;

/**
 * A properties resolver that resolves a list of properties from a given list of strings. The string
 * values should be formatted as key-value pairs, compatible with the Java Properties format.
 *
 * <p>This resolver is typically used to provide custom properties that are not sourced from
 * external files or resources, but rather defined directly in the code or passed
 * as command line arguments. It is useful for scenarios where properties need to be
 * dynamically defined or configured at runtime, such as in testing environments or
 * during application startup.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StringListCustomPropertiesResolver extends AbstractCustomPropertiesResolver {

    private final List<String> properties;

    public StringListCustomPropertiesResolver(List<String> properties) {
        this.properties = properties;
    }

    @Override
    protected List<String> resolveStringProperties(SingleElementContext<ApplicationEnvironment> initializerContext) {
        return this.properties;
    }
}
