package org.dockbox.hartshorn.profiles.support;

import org.dockbox.hartshorn.profiles.ProfileNameResolver;
import org.dockbox.hartshorn.properties.PropertyRegistry;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A composite implementation of {@link ProfileNameResolver} that aggregates multiple resolvers and
 * combines their results.
 *
 * @param resolvers the set of profile name resolvers to aggregate
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record CompositeProfileNameResolver(Set<ProfileNameResolver> resolvers) implements ProfileNameResolver {

    public CompositeProfileNameResolver(ProfileNameResolver... resolvers) {
        this(Set.of(resolvers));
    }

    @Override
    public Set<String> resolveProfileNames(PropertyRegistry rootRegistry) {
        return this.resolvers.stream()
                .flatMap(resolver -> resolver.resolveProfileNames(rootRegistry).stream())
                .collect(Collectors.toSet());
    }
}
