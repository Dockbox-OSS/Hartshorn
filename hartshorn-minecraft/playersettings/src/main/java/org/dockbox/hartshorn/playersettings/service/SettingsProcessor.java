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

import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.playersettings.Setting;
import org.dockbox.hartshorn.playersettings.annotations.UseSettings;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class SettingsProcessor implements ServiceProcessor<UseSettings> {

    @Override
    public boolean preconditions(Class<?> type) {
        return !Reflect.fieldsLike(type, Setting.class).isEmpty();
    }

    @Override
    public <T> void process(ApplicationContext context, Class<T> type) {
        SettingsContext settingsContext = context.first(SettingsContext.class).get();

        final List<Field> fields = Reflect.fieldsLike(type, Setting.class);
        for (Field field : fields) {
            try {
                final int modifiers = field.getModifiers();
                if (!Modifier.isPublic(modifiers)) throw new IllegalStateException("Expected Setting field '" + field.getName() + "' in " + type.getSimpleName() + " to be public");
                if (!Modifier.isStatic(modifiers)) throw new IllegalStateException("Expected Setting field '" + field.getName() + "' in " + type.getSimpleName() + " to be static");
                if (!Modifier.isFinal(modifiers)) throw new IllegalStateException("Expected Setting field '" + field.getName() + "' in " + type.getSimpleName() + " to be final");

                final Setting<?> setting = (Setting<?>) field.get(null);
                if (setting == null) throw new IllegalStateException("Expected Setting field '" + field.getName() + "' in " + type.getSimpleName() + " to be non-null");

                settingsContext.add(setting);
            }
            catch (IllegalAccessException | ClassCastException e) {
                Except.handle(e);
            }
        }
    }

    @Override
    public Class<UseSettings> activator() {
        return UseSettings.class;
    }
}
