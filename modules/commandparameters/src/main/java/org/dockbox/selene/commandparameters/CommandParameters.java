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

package org.dockbox.selene.commandparameters;

import org.dockbox.selene.annotations.command.CustomParameter;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.SeleneInformation;
import org.dockbox.selene.api.server.bootstrap.Preloadable;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.command.convert.DynamicPatternConverter;
import org.dockbox.selene.command.parameter.CustomParameterPattern;

import java.util.Collection;

@Module(id = "commandparameters", name = "Command Parameters", description = "Allows annotated types to be command parameters", authors = "GuusLieben")
public class CommandParameters implements Preloadable {

    @Override
    public void preload() {
        Collection<Class<?>> customParameters = Reflect.annotatedTypes(SeleneInformation.PACKAGE_PREFIX, CustomParameter.class);
        for (Class<?> customParameter : customParameters) {
            CustomParameter meta = customParameter.getAnnotation(CustomParameter.class);
            CustomParameterPattern pattern = Selene.provide(meta.pattern());
            String key = meta.value();
            // Automatically registers to the ArgumentConverterRegistry
            new DynamicPatternConverter<>(customParameter, pattern, key);
        }
    }

}
