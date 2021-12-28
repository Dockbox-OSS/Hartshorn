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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

public interface ComponentContainer {

    String id();

    String name();

    boolean enabled();

    TypeContext<?> type();

    TypeContext<?> owner();

    List<Class<? extends Annotation>> activators();

    @Deprecated(since = "4.2.5", forRemoval = true)
    boolean hasActivator();

    @Deprecated(since = "4.2.5", forRemoval = true)
    boolean hasActivator(Class<? extends Annotation> activator);

    boolean singleton();

    ComponentType componentType();

    boolean permitsProxying();

    static String id(final ApplicationContext context, final TypeContext<?> type) {
        return id(context, type, false);
    }

    static String id(final ApplicationContext context, final TypeContext<?> type, final boolean ignoreExisting) {
        final Exceptional<ComponentContainer> container = context.locator().container(type);
        if (!ignoreExisting && container.present()) {
            final String id = container.get().id();
            if (!"".equals(id)) return id;
        }

        String raw = type.name();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        final String[] parts = HartshornUtils.splitCapitals(raw);
        return String.join("-", parts).toLowerCase(Locale.ROOT);
    }

    static String name(final ApplicationContext context, final TypeContext<?> type, final boolean ignoreExisting) {
        final Exceptional<ComponentContainer> container = context.locator().container(type);
        if (!ignoreExisting && container.present()) {
            final String name = container.get().name();
            if (!"".equals(name)) return name;
        }

        String raw = type.name();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        final String[] parts = HartshornUtils.splitCapitals(raw);
        return HartshornUtils.capitalize(String.join(" ", parts));
    }
}
