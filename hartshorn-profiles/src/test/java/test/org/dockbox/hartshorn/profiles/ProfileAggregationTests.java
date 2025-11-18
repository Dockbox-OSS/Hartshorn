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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ProfileAggregationTests {

    @Test
    void aggregationWithoutProfiles() {
        ProfilePropertyRegistryAggregator aggregator = new SimpleProfilePropertyRegistryAggregator();
        ProfileRegistry profileRegistry = new ConcurrentProfileRegistry();
         assertThatCode(() -> {
             ProfilePropertyRegistry profilePropertyRegistry = aggregator.aggregate(profileRegistry);
             assertThat(profilePropertyRegistry.keys()).isEmpty();
             assertThat(profilePropertyRegistry.profileRegistry().profiles()).isEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    void aggregationWithSingleProfile() {
        ProfilePropertyRegistryAggregator aggregator = new SimpleProfilePropertyRegistryAggregator();
        ProfileRegistry profileRegistry = new ConcurrentProfileRegistry();
        EnvironmentProfile profile1 = new SimpleEnvironmentProfile("profile1", new MapPropertyRegistry());
        profile1.propertyRegistry().register(new SingleConfiguredProperty("key", "value1"));
        profileRegistry.register(0, profile1);

        assertThatCode(() -> {
            ProfilePropertyRegistry profilePropertyRegistry = aggregator.aggregate(profileRegistry);
            assertThat(profilePropertyRegistry.keys()).hasSize(1);
            assertThat(profilePropertyRegistry.contains("key")).isTrue();

            assertThat(profilePropertyRegistry.profileRegistry().profiles()).hasSize(1);
            assertThat(profilePropertyRegistry.profileRegistry().profile("profile1").present()).isTrue();
        }).doesNotThrowAnyException();
    }

    @Test
    void aggregationWithMultipleProfilesUsesCorrectPriority() {
        ProfilePropertyRegistryAggregator aggregator = new SimpleProfilePropertyRegistryAggregator();
        ProfileRegistry profileRegistry = new ConcurrentProfileRegistry();

        EnvironmentProfile profile1 = new SimpleEnvironmentProfile("profile1", new MapPropertyRegistry());
        profile1.propertyRegistry().register(new SingleConfiguredProperty("key", "value1"));
        profileRegistry.register(0, profile1);

        EnvironmentProfile profile2 = new SimpleEnvironmentProfile("profile2", new MapPropertyRegistry());
        profile2.propertyRegistry().register(new SingleConfiguredProperty("key", "value2"));
        profileRegistry.register(1, profile2);

        assertThatCode(() -> {
            ProfilePropertyRegistry profilePropertyRegistry = aggregator.aggregate(profileRegistry);
            assertThat(profilePropertyRegistry.keys()).hasSize(1);
            assertThat(profilePropertyRegistry.contains("key")).isTrue();

            Option<ValueProperty> valueProperty = profilePropertyRegistry.get("key");
            assertThat(valueProperty.present()).isTrue();
            // profile2 has a higher priority, thus after aggregation the value from profile2 should be used
            assertThat(valueProperty.get().value().contains("value2")).isTrue();

            assertThat(profilePropertyRegistry.profileRegistry().profiles()).hasSize(2);
            assertThat(profilePropertyRegistry.profileRegistry().profile("profile1").present()).isTrue();
            assertThat(profilePropertyRegistry.profileRegistry().profile("profile2").present()).isTrue();
        }).doesNotThrowAnyException();
    }
}
