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

package org.dockbox.hartshorn.profiles.aggregation;

import org.dockbox.hartshorn.profiles.EnvironmentProfile;
import org.dockbox.hartshorn.profiles.support.ProfileMapPropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.SingleConfiguredProperty;
import org.dockbox.hartshorn.properties.ValueProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Standard implementation of {@link ProfilePropertyRegistryAggregator} that aggregates properties from multiple
 * {@link EnvironmentProfile}s into a single {@link ProfilePropertyRegistry}, respecting profile priorities.
 *
 * <p>This aggregator creates a new {@link ProfileMapPropertyRegistry} containing all properties from the profiles
 * in the given {@link ProfileRegistry}. Properties from higher priority profiles override those from lower priority
 * ones.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleProfilePropertyRegistryAggregator implements ProfilePropertyRegistryAggregator {

    @Override
    public ProfilePropertyRegistry aggregate(ProfileRegistry profileRegistry) {
        Map<String, ConfiguredProperty> properties = new HashMap<>();
        List<EnvironmentProfile> profiles = profileRegistry.profiles();
        for (int i = profiles.size() - 1; i >= 0; i--) {
            EnvironmentProfile profile = profiles.get(i);
            PropertyRegistry propertyRegistry = profile.propertyRegistry();
            Set<String> keys = propertyRegistry.keys();
            for (String key : keys) {
                // Don't override properties defined in higher priority profiles
                if (!properties.containsKey(key)) {
                    ValueProperty valueProperty = propertyRegistry.get(key)
                            .orElseThrow(() -> new IllegalStateException("Property " + key + " not found in registry"));
                    properties.put(key, new SingleConfiguredProperty(valueProperty.name(),
                            valueProperty.value().orElseThrow(() -> new IllegalStateException("Property " + key + " has no value"))));
                }
            }
        }
        return new ProfileMapPropertyRegistry(properties, profileRegistry);
    }
}
