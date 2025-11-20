/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.profiles.support;

import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.profiles.ProfileNameResolver;
import org.dockbox.hartshorn.properties.PropertyRegistry;

/**
 * A composite implementation of {@link ProfileNameResolver} that aggregates multiple resolvers and
 * combines their results.
 *
 * @param resolvers the set of profile name resolvers to aggregate
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public record CompositeProfileNameResolver(Set<ProfileNameResolver> resolvers)
    implements ProfileNameResolver {

    public CompositeProfileNameResolver(ProfileNameResolver... resolvers) {
        this(Set.of(resolvers));
    }

    @Override
    public SequencedSet<String> resolveProfileNames(PropertyRegistry rootRegistry) {
        return this.resolvers.stream()
            .flatMap(resolver -> resolver.resolveProfileNames(rootRegistry).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
