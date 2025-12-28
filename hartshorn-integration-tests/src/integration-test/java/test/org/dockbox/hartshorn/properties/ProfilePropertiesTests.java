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

package test.org.dockbox.hartshorn.properties;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.profiles.support.ConfigurationProfileRegistryFactory;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.test.HartshornAssertions;
import org.dockbox.hartshorn.test.annotations.TestProfiles;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dockbox.hartshorn.test.HartshornAssertions.assertThat;

@TestProfiles("ProfilePropertiesTests")
@HartshornIntegrationTest(includeBasePackages = false)
class ProfilePropertiesTests {

    @Inject
    private PropertyRegistry propertyRegistry;

    @Test
    void propertyRegistryHasProfilesInIntegrationTest() {
        ProfilePropertyRegistry registry = assertThat(propertyRegistry)
                .asInstanceOf(InstanceOfAssertFactories.type(ProfilePropertyRegistry.class))
                .actual();
        ProfileRegistry profileRegistry = registry.profileRegistry();
        assertThat(profileRegistry.profiles()).hasSize(2);

        assertThat(profileRegistry.profile(
                ConfigurationProfileRegistryFactory.DEFAULT_PROFILE_NAME
        )).present();
        assertThat(profileRegistry.profile("ProfilePropertiesTests")).present();
    }

    @Test
    void profilePropertiesLoadInIntegrationTest() {
        Option<ValueProperty> propertyOption = this.propertyRegistry.get("test.property");
        assertThat(propertyOption)
                .value()
                .extracting(ValueProperty::value, HartshornAssertions.option(String.class))
                .value()
                .isEqualTo("This is a profile-specific property value.");
    }
}
