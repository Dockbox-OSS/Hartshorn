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

import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathStyle;

import java.util.Map;

/**
 * A {@link MapPropertyRegistry} that is also a {@link ProfilePropertyRegistry}, meaning it is aware
 * of an associated {@link ProfileRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ProfileMapPropertyRegistry extends MapPropertyRegistry
    implements ProfilePropertyRegistry {

    private final ProfileRegistry profileRegistry;

    public ProfileMapPropertyRegistry(ProfileRegistry profileRegistry) {
        this.profileRegistry = profileRegistry;
    }

    public ProfileMapPropertyRegistry(
        Map<String, ConfiguredProperty> properties,
        ProfileRegistry profileRegistry
    ) {
        super(properties);
        this.profileRegistry = profileRegistry;
    }

    public ProfileMapPropertyRegistry(
        PropertyPathStyle pathStyle,
        ProfileRegistry profileRegistry
    ) {
        super(pathStyle);
        this.profileRegistry = profileRegistry;
    }

    public ProfileMapPropertyRegistry(
        Map<String, ConfiguredProperty> properties,
        PropertyPathStyle pathStyle,
        ProfileRegistry profileRegistry
    ) {
        super(properties, pathStyle);
        this.profileRegistry = profileRegistry;
    }

    @Override
    public ProfileRegistry profileRegistry() {
        return this.profileRegistry;
    }
}
