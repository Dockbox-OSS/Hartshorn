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
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistryFactory;
import org.dockbox.hartshorn.profiles.support.ConfigurationProfileRegistryFactory;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfileRegistryFactoryTests {

    @Test
    void testProfilesAreRegisteredWithCorrectPriority() {
        ProfileRegistryFactory factory = new ConfigurationProfileRegistryFactory(
            (registry, path) -> {
                // Do nothing, resource resolver has no URIs to index, so this will never be called
                Assertions.fail("Should never be called");
            },
            profileName -> Set.of(),
            MapPropertyRegistry::new,
            rootRegistry -> CollectionUtilities.sequencedSet("profile1", "profile2")
        );
        ProfileRegistry profileRegistry = factory.create(new MapPropertyRegistry());
        Assertions.assertNotNull(profileRegistry);

        List<EnvironmentProfile> profiles = profileRegistry.profiles();
        Assertions.assertEquals(3, profiles.size());

        // Default profile is always priority 0 (highest)
        Assertions.assertEquals(ConfigurationProfileRegistryFactory.DEFAULT_PROFILE_NAME,
            profiles.get(0).name());
        // profile1 was provided first in resolver passed to constructor, thus should have priority 1 (higher than profile2, but lower than default)
        Assertions.assertEquals("profile1", profiles.get(1).name());
        // profile2 was provided last in resolver passed to constructor, thus should have priority 2 (lowest)
        Assertions.assertEquals("profile2", profiles.get(2).name());
    }
}
