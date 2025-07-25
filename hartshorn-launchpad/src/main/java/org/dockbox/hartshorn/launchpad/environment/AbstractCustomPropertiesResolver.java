package org.dockbox.hartshorn.launchpad.environment;

import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException;
import org.dockbox.hartshorn.util.StringUtilities;

/**
 * Abstract base class for resolving custom properties.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractCustomPropertiesResolver implements CustomPropertiesResolver {

    @Override
    public Properties resolveProperties(SingleElementContext<ApplicationEnvironment> initializerContext) {
        Properties properties = new Properties();
        String propertyString = this.resolveStringProperties(initializerContext).stream()
            .filter(StringUtilities::notEmpty)
            .collect(Collectors.joining("\n"));
        this.safeLoadProperties(properties, new StringReader(propertyString));
        return properties;
    }

    private void safeLoadProperties(Properties properties, StringReader stringReader) {
        try {
            properties.load(stringReader);
        }
        catch (Exception e) {
            // Should never happen, as the StringReader is created from a stable String
            throw new ComponentInitializationException("Failed to load command line arguments as properties", e);
        }
    }

    protected abstract List<String> resolveStringProperties(SingleElementContext<ApplicationEnvironment> initializerContext);
}
