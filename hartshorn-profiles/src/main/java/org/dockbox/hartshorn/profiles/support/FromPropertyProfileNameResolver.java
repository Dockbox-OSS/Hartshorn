package org.dockbox.hartshorn.profiles.support;

import org.dockbox.hartshorn.profiles.ProfileNameResolver;
import org.dockbox.hartshorn.properties.PropertyRegistry;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ProfileNameResolver} that resolves profile names from a specific property
 * in the given {@link PropertyRegistry}. The property key used is {@value #PROFILES_PROPERTY}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class FromPropertyProfileNameResolver implements ProfileNameResolver {

    public static final String PROFILES_PROPERTY = "hartshorn.profiles";

    @Override
    public Set<String> resolveProfileNames(PropertyRegistry rootRegistry) {
        return rootRegistry.list(PROFILES_PROPERTY)
                .stream(list -> list.values().stream())
                .flatMap(property -> property.value().stream())
                .collect(Collectors.toSet());
    }
}
