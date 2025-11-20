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
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;

/**
 * Aggregates multiple {@link PropertyRegistry} instances from different {@link EnvironmentProfile}s
 * into a single {@link ProfilePropertyRegistry}, respecting profile priorities.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public interface ProfilePropertyRegistryAggregator {

    /**
     * Aggregates the {@link PropertyRegistry} instances from the given {@link ProfileRegistry} into
     * a single {@link ProfilePropertyRegistry}.
     *
     * @param profileRegistry the profile registry containing the profiles to aggregate
     *
     * @return the aggregated profile property registry
     */
    ProfilePropertyRegistry aggregate(ProfileRegistry profileRegistry);
}
