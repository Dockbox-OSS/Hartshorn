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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Locale;
import java.util.function.Function;

/**
 * Utilities for working with {@link ComponentContainer}s, specifically for generating IDs and names.
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public final class ComponentUtilities {

    private ComponentUtilities() {
    }

    /**
     * Generates an ID for the given type. If the type is a {@link ComponentContainer}, the ID of the container is
     * returned. Otherwise, the ID is generated based on the type's simple name.
     *
     * @param context the application context
     * @param type the type to generate an ID for
     * @return the ID
     */
    public static String id(ApplicationContext context, Class<?> type) {
        return id(context, type, false);
    }

    public static String id(ComponentContainer<?> container) {
        return id(container, false);
    }

    /**
     * Generates an ID for the given type. If the type is a {@link ComponentContainer}, the ID of the container is
     * returned. Otherwise, the ID is generated based on the type's simple name. If {@code ignoreContainerName} is
     * {@code true}, the container name is ignored and the ID is generated based on the type's simple name.
     *
     * @param context the application context
     * @param type the type to generate an ID for
     * @param ignoreContainerName whether to ignore the container name
     * @return the ID
     */
    public static String id(ApplicationContext context, Class<?> type, boolean ignoreContainerName) {
        return format(context, type, ignoreContainerName, '-', ComponentContainer::id).toLowerCase(Locale.ROOT);
    }

    public static String id(ComponentContainer<?> container, boolean ignoreContainerName) {
        return format(container, ignoreContainerName, '-', ComponentContainer::id).toLowerCase(Locale.ROOT);
    }

    /**
     * Generates a name for the given type. If the type is a {@link ComponentContainer}, the name of the container is
     * returned. Otherwise, the name is generated based on the type's simple name.
     *
     * @param context the application context
     * @param type the type to generate a name for
     * @param ignoreContainerName whether to ignore the container name
     * @return the name
     */
    public static String name(ApplicationContext context, Class<?> type, boolean ignoreContainerName) {
        return format(context, type, ignoreContainerName, ' ', ComponentContainer::name);
    }

    public static String name(ComponentContainer<?> container, boolean ignoreContainerName) {
        return format(container, ignoreContainerName, ' ', ComponentContainer::name);
    }

    /**
     * Generates a name for the given type. If the type is a {@link ComponentContainer}, the name of the container is
     * returned. Otherwise, the name is generated based on the type's simple name.
     *
     * @param context the application context
     * @param type the type to generate a name for
     * @param ignoreContainerName whether to ignore the container name
     * @param delimiter the delimiter to use when generating the name
     * @param attribute the attribute to use when generating the name
     * @return the name
     */
    public static String format(ApplicationContext context, Class<?> type, boolean ignoreContainerName, char delimiter, Function<ComponentContainer<?>, String> attribute) {
        Option<ComponentContainer<?>> container = context.get(ComponentRegistry.class).container(type);
        if (container.present()) {
            return format(container.get(), ignoreContainerName, delimiter, attribute);
        }
        TypeView<?> typeView = context.environment().introspector().introspect(type);
        return format(new AnnotatedComponentContainer<>(typeView), ignoreContainerName, delimiter, attribute);
    }

    public static <T> String format(ComponentContainer<T> container, boolean ignoreContainerName, char delimiter, Function<ComponentContainer<?>, String> attribute) {
        if (!ignoreContainerName) {
            String name = attribute.apply(container);
            if (StringUtilities.notEmpty(name)) {
                return name;
            }
        }

        String raw = container.type().name();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        String[] parts = StringUtilities.splitCapitals(raw);
        return StringUtilities.capitalize(String.join(String.valueOf(delimiter), parts));
    }
}
