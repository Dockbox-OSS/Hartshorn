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

package org.dockbox.selene.config;

import org.dockbox.selene.api.BootstrapPhase;
import org.dockbox.selene.api.Phase;
import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.config.annotations.Configuration;
import org.dockbox.selene.config.annotations.Value;
import org.dockbox.selene.di.InjectionPoint;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.di.preload.Preloadable;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.FileType;
import org.dockbox.selene.persistence.FileTypeProperty;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.exceptions.FieldAccessException;
import org.dockbox.selene.util.exceptions.NotPrimitiveException;
import org.dockbox.selene.util.exceptions.TypeConversionException;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collection;

@Phase(BootstrapPhase.CONSTRUCT)
public class ConfigurationPreload implements Preloadable {

    @Override
    public void preload() {
        InjectionPoint<?> point = InjectionPoint.of(Object.class, (instance, properties) -> {
            Collection<Field> fields = Reflect.annotatedFields(Value.class, instance.getClass());
            if (fields.isEmpty()) return instance;

            String file = SeleneInformation.PROJECT_ID;
            Class<?> owner = Selene.class;
            if (instance.getClass().isAnnotationPresent(Configuration.class)) {
                Configuration configuration = instance.getClass().getAnnotation(Configuration.class);
                file = configuration.value();
                owner = configuration.service().owner();
            }

            FileManager fileManager = Provider.provide(FileManager.class, FileTypeProperty.of(FileType.YAML));
            Path config = fileManager.getConfigFile(owner, file);

            ConfigurationManager configurationManager = Provider.provide(ConfigurationManager.class, config);

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Value value = field.getAnnotation(Value.class);
                    Object fieldValue = Exceptional.of(() -> configurationManager.get(value.value())).or(value.or());

                    if ((!Reflect.assignableFrom(String.class, field.getType())) && (fieldValue instanceof String)) {
                        fieldValue = Reflect.primitiveFromString(field.getType(), (String) fieldValue);
                    }

                    Reflect.set(field, instance, fieldValue);
                } catch (FieldAccessException | TypeConversionException | NotPrimitiveException e) {
                    Selene.log().warn("Could not prepare value field " + field.getName() + " in " + instance.getClass().getSimpleName());
                }
            }

            return instance;
        });
        Selene.getServer().injectAt(point);
    }
}
