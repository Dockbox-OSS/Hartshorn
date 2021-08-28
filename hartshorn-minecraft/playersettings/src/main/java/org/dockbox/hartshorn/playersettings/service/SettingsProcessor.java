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

package org.dockbox.hartshorn.playersettings.service;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.GenericType;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.FieldContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.playersettings.Setting;
import org.dockbox.hartshorn.playersettings.annotations.UseSettings;

import java.util.List;

public class SettingsProcessor implements ServiceProcessor<UseSettings> {

    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return !type.fieldsOf(Setting.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        final SettingsContext settingsContext = context.first(SettingsContext.class).get();

        final List<FieldContext<Setting<?>>> fields = type.fieldsOf(new GenericType<>() {
        });
        for (final FieldContext<Setting<?>> field : fields) {
            if (field.isPublic()) throw new IllegalStateException("Expected Setting field '" + field.name() + "' in " + type.name() + " to be public");
            if (field.isStatic()) throw new IllegalStateException("Expected Setting field '" + field.name() + "' in " + type.name() + " to be static");
            if (field.isFinal()) throw new IllegalStateException("Expected Setting field '" + field.name() + "' in " + type.name() + " to be final");

            final Exceptional<Setting<?>> setting = field.getStatic();
            if (setting.absent()) throw new IllegalStateException("Expected Setting field '" + field.name() + "' in " + type.name() + " to be non-null");

            settingsContext.add(setting.get());
        }
    }

    @Override
    public Class<UseSettings> activator() {
        return UseSettings.class;
    }
}
