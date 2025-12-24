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

package test.org.dockbox.hartshorn.profiles;

import java.util.List;
import java.util.Set;
import org.dockbox.hartshorn.profiles.EnvironmentProfile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistryFactory;
import org.dockbox.hartshorn.profiles.support.ConfigurationProfileRegistryFactory;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.junit.jupiter.api.Test;

class ProfileRegistryFactoryTests {

    @Test
    void profilesAreRegisteredWithCorrectPriority() {
        ProfileRegistryFactory factory = new ConfigurationProfileRegistryFactory(
            (registry, path) -> {
                // Do nothing, resource resolver has no URIs to index, so this will never be called
                fail("Should never be called");
            },
            profileName -> Set.of(),
            MapPropertyRegistry::new,
            rootRegistry -> CollectionUtilities.sequencedSet("profile1", "profile2")
        );
        ProfileRegistry profileRegistry = factory.create(new MapPropertyRegistry());
        assertThat(profileRegistry).isNotNull();

        List<EnvironmentProfile> profiles = profileRegistry.profiles();
        assertThat(profiles).hasSize(3);

        // Default profile is always priority 0 (highest)
        assertThat(profiles.get(0).name()).isEqualTo(ConfigurationProfileRegistryFactory.DEFAULT_PROFILE_NAME);
        // profile1 was provided first in resolver passed to constructor, thus should have priority 1 (higher than profile2, but lower than default)
        assertThat(profiles.get(1).name()).isEqualTo("profile1");
        // profile2 was provided last in resolver passed to constructor, thus should have priority 2 (lowest)
        assertThat(profiles.get(2).name()).isEqualTo("profile2");
    }
}
