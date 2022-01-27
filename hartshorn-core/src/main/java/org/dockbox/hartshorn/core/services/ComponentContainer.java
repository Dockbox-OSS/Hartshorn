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

    TypeContext<?> type();

    TypeContext<?> owner();

    List<Class<? extends Annotation>> activators();

    boolean singleton();

    boolean lazy();

    ComponentType componentType();

    boolean permitsProxying();

    boolean permitsProcessing();

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
