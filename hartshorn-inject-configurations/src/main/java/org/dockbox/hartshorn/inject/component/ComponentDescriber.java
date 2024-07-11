/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.inject.component;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Locale;

/**
 * Utility to generate a name or ID for a component based on its type.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public final class ComponentDescriber {

    /**
     * Formats the component type into a kebab-case string. E.g. {@code JsonDataWorker} becomes
     * {@code json-data-worker}.
     *
     * @return the kebab-case formatted string
     */
    public static String id(TypeView<?> type) {
        return ComponentDescriber.format(type, '-').toLowerCase(Locale.ROOT);
    }

    /**
     * Formats the component type into a human-readable string. E.g. {@code JsonDataWorker} becomes
     * {@code Json Data Worker}.
     *
     * @return the human-readable formatted string
     */
    public static String name(TypeView<?> type) {
        return ComponentDescriber.format(type, ' ');
    }

    private static String format(TypeView<?> type, char delimiter) {
        String raw = type.name();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        String[] parts = StringUtilities.splitCapitals(raw);
        return StringUtilities.capitalize(String.join(String.valueOf(delimiter), parts));
    }
}
