package org.dockbox.hartshorn.launchpad.environment;

import java.util.List;
import org.dockbox.hartshorn.context.SingleElementContext;

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
