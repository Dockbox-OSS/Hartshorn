/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.util.Exceptional;

import java.util.Properties;

/**
 * This class is used to store application properties.
 */
public interface ApplicationPropertyHolder {

    Properties properties();

    /**
     * Attempts to obtain a single configuration value from the given key. Configuration
     * values can also represent system/environment variables.
     *
     * @param key The key used to look up the value
     *
     * @return The value if it exists, or {@link Exceptional#empty()}
     */
    Exceptional<String> property(String key);
}
