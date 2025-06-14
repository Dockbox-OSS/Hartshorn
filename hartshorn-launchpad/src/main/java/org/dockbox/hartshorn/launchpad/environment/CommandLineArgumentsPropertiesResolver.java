package org.dockbox.hartshorn.launchpad.environment;

import java.util.List;
import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.launchpad.launch.ApplicationBuildContext;

/**
 * Resolves command line arguments from the {@link ApplicationBuildContext} as properties for
 * the application environment.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class CommandLineArgumentsPropertiesResolver extends AbstractCustomPropertiesResolver {

    @Override
    protected List<String> resolveStringProperties(SingleElementContext<ApplicationEnvironment> initializerContext) {
        return initializerContext.firstContext(ApplicationBuildContext.class)
            .map(ApplicationBuildContext::arguments)
            .orElseGet(List::of);
    }
}
