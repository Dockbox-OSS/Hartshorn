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

import org.dockbox.hartshorn.profiles.ProfileNameResolver;
import org.dockbox.hartshorn.profiles.support.FromPropertyProfileNameResolver;
import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.SingleConfiguredProperty;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathStyle;
import org.dockbox.hartshorn.properties.loader.path.StandardPropertyPathStyle;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ProfileNameResolverTests {

    @Test
    void fromPropertyProfileNameResolverSupportsListValue() {
        PropertyRegistry registry = new MapPropertyRegistry();
        PropertyPathStyle style = StandardPropertyPathStyle.INSTANCE;

        ConfiguredProperty profileOne = new SingleConfiguredProperty(
                FromPropertyProfileNameResolver.PROFILES_PROPERTY
                        + style.index(0), "development");
        registry.register(profileOne);

        ConfiguredProperty profileTwo = new SingleConfiguredProperty(
                FromPropertyProfileNameResolver.PROFILES_PROPERTY
                        + style.index(1), "testing");
        registry.register(profileTwo);

        ProfileNameResolver resolver = new FromPropertyProfileNameResolver();
        Set<String> profileNames = resolver.resolveProfileNames(registry);

        assertThat(profileNames)
                .hasSize(2)
                .contains("development")
                .contains("testing");
    }

    @Test
    void fromPropertyProfileNameResolverSupportsCommaSeparatedValue() {
        PropertyRegistry registry = new MapPropertyRegistry();

        ConfiguredProperty profiles = new SingleConfiguredProperty(
                FromPropertyProfileNameResolver.PROFILES_PROPERTY, "development,testing");
        registry.register(profiles);

        ProfileNameResolver resolver = new FromPropertyProfileNameResolver();
        Set<String> profileNames = resolver.resolveProfileNames(registry);

        assertThat(profileNames)
                .hasSize(2)
                .contains("development")
                .contains("testing");
    }

    @Test
    void fromPropertyProfileNameResolverSupportsAbsentProperty() {
        PropertyRegistry registry = new MapPropertyRegistry();
        ProfileNameResolver resolver = new FromPropertyProfileNameResolver();
        Set<String> profileNames = assertThatCode(() -> resolver.resolveProfileNames(registry)).doesNotThrowAnyException();
        assertThat(profileNames).isEmpty();
    }
}
