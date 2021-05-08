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
import org.dockbox.selene.config.annotations.Source;
import org.dockbox.selene.config.annotations.Value;
import org.dockbox.selene.di.InjectionPoint;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.di.preload.Preloadable;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.util.Reflect;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collection;

@Phase(BootstrapPhase.CONSTRUCT)
public class ConfigurationPreload implements Preloadable {

    @Override
    public void preload() {
        InjectionPoint<?> point = InjectionPoint.of(Object.class, (instance, properties) -> {
            Collection<Field> fields = Reflect.annotatedFields(Value.class, instance.getClass());
            String file = "selene.yml";
            Class<?> owner = Selene.class;
            if (instance.getClass().isAnnotationPresent(Source.class)) {
                Source source = instance.getClass().getAnnotation(Source.class);
                file = source.value();
                owner = source.owner().value();
            }

            FileManager fileManager = Provider.provide(FileManager.class);
            Path config = fileManager.getConfigFile(owner, file);

            Configuration configuration = Provider.provide(Configuration.class, config);

            for (Field field : fields) {
                field.setAccessible(true);
                Value value = field.getAnnotation(Value.class);
                // TODO: Convert primitives and enums
                Object fieldValue = configuration.get(value.value());
                // TODO: handle
                field.set(instance, fieldValue);
            }

            return instance;
        });
    }
}
