/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public final class Bindings {

    private Bindings() {
    }

    public static Named named(String value) {
        return new NamedImpl(value);
    }

    /**
     * Looks up a {@link InjectorProperty} based on a given {@code key}. If a property with that key
     * is present, and matches the expected type it is returned. If no property is present, or the
     * type of the object does not match the expected type,
     * {@code null} is returned.
     *
     * @param <T>
     *         The expected type of the property
     * @param key
     *         The key of the property to look up
     * @param expectedType
     *         The {@link Class} of the expected property type
     * @param properties
     *         The properties to perform a lookup on
     *
     * @return The nullable property value
     */
    public static <T> Exceptional<T> value(@NonNls String key, Class<T> expectedType, InjectorProperty<?>... properties) {
        if (properties.length == 0) return Exceptional.empty();

        InjectorProperty<T> property = Bindings.property(key, expectedType, properties);
        // As the object is provided by a supplier this cannot currently be simplified to #of
        if (null != property) {
            return Exceptional.of(property.value());
        }
        return Exceptional.empty();
    }

    /**
     * Gets property.
     *
     * @param <T>
     *         the type parameter
     * @param key
     *         the key
     * @param expectedType
     *         the expected type
     * @param properties
     *         the properties
     *
     * @return the property
     */
    @Nullable
    public static <T> InjectorProperty<T> property(@NonNls String key, Class<T> expectedType, InjectorProperty<?>... properties) {
        List<InjectorProperty<T>> matchingProperties = Bindings.properties(key, expectedType, properties);
        if (matchingProperties.isEmpty()) return null;
        else return matchingProperties.get(0);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<InjectorProperty<T>> properties(@NonNls String key, Class<T> expectedType, InjectorProperty<?>... properties) {
        List<InjectorProperty<T>> matchingProperties = HartshornUtils.emptyList();
        for (InjectorProperty<?> property : properties) {
            if (property.key().equals(key)
                    && null != property.value()
                    && Reflect.assigns(expectedType, property.value().getClass())) {
                matchingProperties.add((InjectorProperty<T>) property);
            }
        }
        return matchingProperties;
    }

    @SuppressWarnings("unchecked")
    public static <P extends InjectorProperty<?>> List<P> properties(@NonNls String key,InjectorProperty<?>... properties) {
        List<P> matchingProperties = HartshornUtils.emptyList();
        for (InjectorProperty<?> property : properties) {
            if (property.key().equals(key) && null != property.value()) {
                matchingProperties.add((P) property);
            }
        }
        return matchingProperties;
    }

    /**
     * Gets sub properties.
     *
     * @param <T>
     *         the type parameter
     * @param propertyFilter
     *         the property filter
     * @param properties
     *         the properties
     *
     * @return the sub properties
     */
    @SuppressWarnings("unchecked")
    public static <T extends InjectorProperty<?>> List<T> valuesOfType(Class<T> propertyFilter, InjectorProperty<?>... properties) {
        List<T> values = HartshornUtils.emptyList();
        for (InjectorProperty<?> property : properties) {
            if (Reflect.assigns(propertyFilter, property.getClass())) values.add((T) property);
        }
        return values;
    }

    public static String serviceId(Class<?> type) {
        return serviceId(type, false);
    }

    public static String serviceId(Class<?> type, boolean ignoreExisting) {
        final Exceptional<ComponentContainer> container = ApplicationContextAware.instance().context().locator().container(type);
        if (!ignoreExisting && container.present()) {
            final String id = container.get().id();
            if (!"".equals(id)) return id;
        }

        String raw = type.getSimpleName();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        final String[] parts = HartshornUtils.splitCapitals(raw);
        return String.join("-", parts).toLowerCase(Locale.ROOT);
    }

    public static String serviceName(Class<?> type) {
        return serviceName(type, false);
    }

    public static String serviceName(Class<?> type, boolean ignoreExisting) {
        final Exceptional<ComponentContainer> container = ApplicationContextAware.instance().context().locator().container(type);
        if (!ignoreExisting && container.present()) {
            final String name = container.get().name();
            if (!"".equals(name)) return name;
        }

        String raw = type.getSimpleName();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        final String[] parts = HartshornUtils.splitCapitals(raw);
        return HartshornUtils.capitalize(String.join(" ", parts));
    }
}
