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

import org.dockbox.hartshorn.profiles.EnvironmentProfile;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.profiles.aggregation.ProfilePropertyRegistryAggregator;
import org.dockbox.hartshorn.profiles.aggregation.SimpleProfilePropertyRegistryAggregator;
import org.dockbox.hartshorn.profiles.support.ConcurrentProfileRegistry;
import org.dockbox.hartshorn.profiles.support.SimpleEnvironmentProfile;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.SingleConfiguredProperty;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfileAggregationTests {

    @Test
    void testAggregationWithoutProfiles() {
        ProfilePropertyRegistryAggregator aggregator =
            new SimpleProfilePropertyRegistryAggregator();
        ProfileRegistry profileRegistry = new ConcurrentProfileRegistry();
        ProfilePropertyRegistry profilePropertyRegistry = Assertions.assertDoesNotThrow(
            () -> aggregator.aggregate(profileRegistry)
        );
        Assertions.assertTrue(profilePropertyRegistry.keys().isEmpty());
        Assertions.assertTrue(profilePropertyRegistry.profileRegistry().profiles().isEmpty());
    }

    @Test
    void testAggregationWithSingleProfile() {
        ProfilePropertyRegistryAggregator aggregator =
            new SimpleProfilePropertyRegistryAggregator();
        ProfileRegistry profileRegistry = new ConcurrentProfileRegistry();
        EnvironmentProfile profile1 =
            new SimpleEnvironmentProfile("profile1", new MapPropertyRegistry());
        profile1.propertyRegistry().register(new SingleConfiguredProperty("key", "value1"));
        profileRegistry.register(0, profile1);

        ProfilePropertyRegistry profilePropertyRegistry = Assertions.assertDoesNotThrow(
            () -> aggregator.aggregate(profileRegistry)
        );
        Assertions.assertEquals(1, profilePropertyRegistry.keys().size());
        Assertions.assertTrue(profilePropertyRegistry.contains("key"));

        Assertions.assertEquals(1, profilePropertyRegistry.profileRegistry().profiles().size());
        Assertions.assertTrue(profilePropertyRegistry.profileRegistry()
            .profile("profile1")
            .present());
    }

    @Test
    void testAggregationWithMultipleProfiles_UsesCorrectPriority() {
        ProfilePropertyRegistryAggregator aggregator =
            new SimpleProfilePropertyRegistryAggregator();
        ProfileRegistry profileRegistry = new ConcurrentProfileRegistry();

        EnvironmentProfile profile1 =
            new SimpleEnvironmentProfile("profile1", new MapPropertyRegistry());
        profile1.propertyRegistry().register(new SingleConfiguredProperty("key", "value1"));
        profileRegistry.register(0, profile1);

        EnvironmentProfile profile2 =
            new SimpleEnvironmentProfile("profile2", new MapPropertyRegistry());
        profile2.propertyRegistry().register(new SingleConfiguredProperty("key", "value2"));
        profileRegistry.register(1, profile2);

        ProfilePropertyRegistry profilePropertyRegistry = Assertions.assertDoesNotThrow(
            () -> aggregator.aggregate(profileRegistry)
        );
        Assertions.assertEquals(1, profilePropertyRegistry.keys().size());
        Assertions.assertTrue(profilePropertyRegistry.contains("key"));

        Option<ValueProperty> valueProperty = profilePropertyRegistry.get("key");
        Assertions.assertTrue(valueProperty.present());
        // profile2 has a higher priority, thus after aggregation the value from profile2 should be used
        Assertions.assertTrue(valueProperty.get().value().contains("value2"));

        Assertions.assertEquals(2, profilePropertyRegistry.profileRegistry().profiles().size());
        Assertions.assertTrue(profilePropertyRegistry.profileRegistry()
            .profile("profile1")
            .present());
        Assertions.assertTrue(profilePropertyRegistry.profileRegistry()
            .profile("profile2")
            .present());
    }
}
