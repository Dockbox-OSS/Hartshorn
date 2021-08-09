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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.FileTypeAttribute;
import org.dockbox.hartshorn.util.Reflect;
import org.dockbox.hartshorn.util.exceptions.FieldAccessException;
import org.dockbox.hartshorn.util.exceptions.NotPrimitiveException;
import org.dockbox.hartshorn.util.exceptions.TypeConversionException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.nio.file.Path;

/**
 * Looks up and populates fields annotated with {@link Value}. If the type is annotated with
 * {@link Configuration} in which a {@link Configuration#source() source} is configured, the
 * provided source is used. If the source is not explicitly configured, the default configuration
 * file is used.
 */
public class ConfigurationServiceModifier implements InjectionModifier<UseConfigurations> {

    @Override
    public <T> boolean preconditions(Class<T> type, @Nullable T instance, Attribute<?>... properties) {
        Class<?> instanceType = type;
        if (instance != null) instanceType = instance.getClass();
        boolean decorated = this.isAnnotated(instanceType);
        return decorated && !Reflect.fields(instanceType, Value.class).isEmpty();
    }

    private boolean isAnnotated(Class<?> type) {
        return Hartshorn.context().locator().container(type).present();
    }

    @Override
    public <T> T process(ApplicationContext context, Class<T> type, @Nullable T instance, Attribute<?>... properties) {
        Class<?> instanceType = type;
        if (instance != null) instanceType = instance.getClass();

        String file = Hartshorn.PROJECT_ID;
        Class<?> owner = Hartshorn.class;
        final Exceptional<Configuration> annotated = Reflect.annotation(instanceType, Configuration.class);
        if (annotated.present()) {
            Configuration configuration = annotated.get();
            file = configuration.source();
            owner = configuration.owner();
        }

        FileManager fileManager = Hartshorn.context().get(FileManager.class, FileTypeAttribute.of(FileType.YAML));
        Path config = fileManager.configFile(owner, file);

        ConfigurationManager configurationManager = Hartshorn.context().get(ConfigurationManager.class, config);

        for (Field field : Reflect.fields(instanceType, Value.class)) {
            try {
                field.setAccessible(true);
                Value value = Reflect.annotation(field, Value.class).get();
                Object fieldValue = configurationManager.get(value.value()).or(value.or());

                if ((!Reflect.assigns(String.class, field.getType())) && (fieldValue instanceof String)) {
                    fieldValue = Reflect.toPrimitive(field.getType(), (String) fieldValue);
                }

                Reflect.set(field, instance, fieldValue);
            } catch (FieldAccessException | TypeConversionException | NotPrimitiveException e) {
                Hartshorn.log().warn("Could not prepare value field " + field.getName() + " in " + instanceType.getSimpleName());
            }
        }

        return instance;
    }

    @Override
    public Class<UseConfigurations> activator() {
        return UseConfigurations.class;
    }
}
