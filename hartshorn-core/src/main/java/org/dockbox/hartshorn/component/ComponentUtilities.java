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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Locale;
import java.util.function.Function;

public class ComponentUtilities {

    public static String id(final ApplicationContext context, final Class<?> type) {
        return id(context, type, false);
    }

    public static String id(final ApplicationContext context, final Class<?> type, final boolean ignoreExisting) {
        return format(context, type, ignoreExisting, '-', ComponentContainer::id).toLowerCase(Locale.ROOT);
    }

    public static String name(final ApplicationContext context, final Class<?> type, final boolean ignoreExisting) {
        return format(context, type, ignoreExisting, ' ', ComponentContainer::name);
    }

    protected static String format(final ApplicationContext context, final Class<?> type, final boolean ignoreExisting, final char delimiter, final Function<ComponentContainer, String> attribute) {
        final Option<ComponentContainer> container = context.get(ComponentLocator.class).container(type);
        if (!ignoreExisting && container.present()) {
            final String name = attribute.apply(container.get());
            if (!"".equals(name)) return name;
        }

        String raw = type.getSimpleName();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        final String[] parts = StringUtilities.splitCapitals(raw);
        return StringUtilities.capitalize(String.join(delimiter + "", parts));
    }
}
