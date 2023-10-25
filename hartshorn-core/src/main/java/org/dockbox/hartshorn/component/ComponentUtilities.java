/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Locale;
import java.util.function.Function;

public final class ComponentUtilities {

    private ComponentUtilities() {
    }

    public static String id(ApplicationContext context, Class<?> type) {
        return id(context, type, false);
    }

    public static String id(ApplicationContext context, Class<?> type, boolean ignoreExisting) {
        return format(context, type, ignoreExisting, '-', ComponentContainer::id).toLowerCase(Locale.ROOT);
    }

    public static String name(ApplicationContext context, Class<?> type, boolean ignoreExisting) {
        return format(context, type, ignoreExisting, ' ', ComponentContainer::name);
    }

    public static String format(ApplicationContext context, Class<?> type, boolean ignoreExisting, char delimiter, Function<ComponentContainer<?>, String> attribute) {
        Option<ComponentContainer<?>> container = context.get(ComponentLocator.class).container(type);
        if (!ignoreExisting && container.present()) {
            String name = attribute.apply(container.get());
            if (StringUtilities.notEmpty(name)) {
                return name;
            }
        }

        String raw = type.getSimpleName();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        String[] parts = StringUtilities.splitCapitals(raw);
        return StringUtilities.capitalize(String.join(String.valueOf(delimiter), parts));
    }
}
