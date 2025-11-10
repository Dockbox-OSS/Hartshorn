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

import org.dockbox.hartshorn.profiles.EnvironmentProfile;
import org.dockbox.hartshorn.profiles.ProfileNameResolver;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistryFactory;
import org.dockbox.hartshorn.profiles.ProfileResourceResolver;
import org.dockbox.hartshorn.profiles.PropertyRegistrySupplier;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.loader.PropertyRegistryPathLoader;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * Standard implementation of {@link ProfileRegistryFactory} that creates a {@link ProfileRegistry} based on
 * a root {@link PropertyRegistry} and additional profiles defined in the root registry. Additional profiles
 * are resolved using a {@link ProfileResourceResolver} and loaded using a {@link PropertyRegistryPathLoader}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ConfigurationProfileRegistryFactory implements ProfileRegistryFactory {

    public static final String DEFAULT_PROFILE_NAME = "default";

    private final PropertyRegistryPathLoader propertyRegistryLoader;
    private final ProfileResourceResolver resourceResolver;
    private final PropertyRegistrySupplier registrySupplier;
    private final ProfileNameResolver profileNameResolver;

    public ConfigurationProfileRegistryFactory(
            PropertyRegistryPathLoader propertyRegistryLoader,
            ProfileResourceResolver resourceResolver,
            PropertyRegistrySupplier registrySupplier,
            ProfileNameResolver profileNameResolver
    ) {
        this.propertyRegistryLoader = propertyRegistryLoader;
        this.resourceResolver = resourceResolver;
        this.registrySupplier = registrySupplier;
        this.profileNameResolver = profileNameResolver;
    }

    @Override
    public ProfileRegistry create(PropertyRegistry rootRegistry) {
        ProfileRegistry profileRegistry = new ConcurrentProfileRegistry();

        EnvironmentProfile defaultProfile = new SimpleEnvironmentProfile(DEFAULT_PROFILE_NAME, rootRegistry);
        profileRegistry.register(0, defaultProfile);

        List<EnvironmentProfile> additionalProfiles = this.profileNameResolver
                .resolveProfileNames(rootRegistry).stream()
                .map(this::resolveProfile)
                .toList();

        for(int i = 0; i < additionalProfiles.size(); i++) {
            EnvironmentProfile profile = additionalProfiles.get(i);
            profileRegistry.register(i + 1, profile);
        }

        return profileRegistry;
    }

    private EnvironmentProfile resolveProfile(String profileName) {
        Set<URI> resources = this.resourceResolver.resolve(profileName);
        PropertyRegistry registry = this.registrySupplier.get();
        for(URI resource : resources) {
            try {
                this.propertyRegistryLoader.loadRegistry(registry, resource);
            }
            catch(IOException e) {
                throw new ProfileLoadingFailedException(profileName, resource, e);
            }
        }
        return new SimpleEnvironmentProfile(profileName, registry);
    }
}
