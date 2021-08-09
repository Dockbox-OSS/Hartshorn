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
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Locale;

public final class Bindings {

    private Bindings() {
    }

    public static Named named(String value) {
        return new NamedImpl(value);
    }

    public static void enable(Object instance, Attribute<?>... properties) throws ApplicationException {
        if (instance instanceof AttributeHolder injectable && injectable.canEnable()) {
            for (Attribute<?> property : properties) injectable.apply(property);
            injectable.enable();
        }
    }

    public static <A, T extends Attribute<A>> boolean has(Class<T> type, Attribute<?>... properties) {
        for (Attribute<?> property : properties) {
            if (type.isInstance(property)) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <A, T extends Attribute<A>> Exceptional<A> lookup(Class<T> type, Attribute<?>... properties) {
        for (Attribute<?> property : properties) {
            if (type.isInstance(property)) return Exceptional.of(() -> (A) property.value());
        }
        return Exceptional.empty();
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
