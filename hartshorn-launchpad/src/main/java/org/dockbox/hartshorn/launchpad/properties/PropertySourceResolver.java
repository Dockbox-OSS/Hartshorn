package org.dockbox.hartshorn.launchpad.properties;

import java.util.Set;

@FunctionalInterface
public interface PropertySourceResolver {

    Set<String> resolve();
}
