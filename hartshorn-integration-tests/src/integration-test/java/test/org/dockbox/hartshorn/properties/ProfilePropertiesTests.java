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

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.profiles.support.ConfigurationProfileRegistryFactory;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.ValueProperty;
import org.dockbox.hartshorn.test.annotations.TestProfiles;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@TestProfiles("ProfilePropertiesTests")
@HartshornIntegrationTest(includeBasePackages = false)
public class ProfilePropertiesTests {

    @Inject
    private PropertyRegistry propertyRegistry;

    @Test
    void testPropertyRegistryHasProfilesInIntegrationTest() {
        ProfilePropertyRegistry registry = Assertions.assertInstanceOf(ProfilePropertyRegistry.class, propertyRegistry);
        ProfileRegistry profileRegistry = registry.profileRegistry();
        Assertions.assertEquals(2, profileRegistry.profiles().size());

        Assertions.assertTrue(profileRegistry.profile(ConfigurationProfileRegistryFactory.DEFAULT_PROFILE_NAME).present());
        Assertions.assertTrue(profileRegistry.profile("ProfilePropertiesTests").present());
    }

    @Test
    void testProfilePropertiesLoadInIntegrationTest() {
        Option<ValueProperty> propertyOption = this.propertyRegistry.get("test.property");
        Assertions.assertTrue(propertyOption.present());
        Assertions.assertTrue(propertyOption.flatMap(ValueProperty::value).present());

        ValueProperty property = propertyOption.get();
        Assertions.assertEquals("This is a profile-specific property value.", property.value().get());
    }
}
