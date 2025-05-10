package org.dockbox.hartshorn.launchpad.environment;

import java.util.List;
import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.launchpad.launch.ApplicationBuildContext;

public class CommandLineArgumentsPropertiesResolver extends AbstractCustomPropertiesResolver {

    @Override
    protected List<String> resolveStringProperties(SingleElementContext<ApplicationEnvironment> initializerContext) {
        return initializerContext.firstContext(ApplicationBuildContext.class)
            .map(ApplicationBuildContext::arguments)
            .orElseGet(List::of);
    }
}
