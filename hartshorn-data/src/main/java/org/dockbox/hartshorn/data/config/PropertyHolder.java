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

package org.dockbox.hartshorn.data.config;

import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.GenericType;

import java.util.Map;
import java.util.Properties;

public interface PropertyHolder {

    boolean has(String key);

    <T> Exceptional<T> get(String key, Class<T> type);

    <T> Exceptional<T> get(String key, GenericType<T> type);

    default <T> Exceptional<T> get(final String key) {
        return this.get(key, (Class<T>) null);
    }

    <T> void set(String key, T value);

    void set(Map<String, Object> tree);

    Properties properties();
}
