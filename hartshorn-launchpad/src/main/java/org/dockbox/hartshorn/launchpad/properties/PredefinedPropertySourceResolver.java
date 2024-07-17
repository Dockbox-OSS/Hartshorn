package org.dockbox.hartshorn.launchpad.properties;

import java.util.Set;

public class PredefinedPropertySourceResolver implements PropertySourceResolver {

    private final Set<String> sources;

    public PredefinedPropertySourceResolver(Set<String> sources) {
        this.sources = sources;
    }

    @Override
    public Set<String> resolve() {
        return Set.copyOf(this.sources);
    }
}
