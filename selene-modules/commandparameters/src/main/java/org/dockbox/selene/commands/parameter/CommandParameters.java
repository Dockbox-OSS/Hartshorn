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

package org.dockbox.selene.commands.parameter;

import org.dockbox.selene.api.BootstrapPhase;
import org.dockbox.selene.api.Phase;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.commands.annotations.Parameter;
import org.dockbox.selene.commands.convert.DynamicPatternConverter;
import org.dockbox.selene.di.preload.Preloadable;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.util.Reflect;

import java.util.Collection;

@Phase(BootstrapPhase.PRE_INIT)
@Module
public class CommandParameters implements Preloadable {

    @Override
    public void preload() {
        Collection<Class<?>> customParameters = Reflect.annotatedTypes(SeleneInformation.PACKAGE_PREFIX, Parameter.class);
        for (Class<?> customParameter : customParameters) {
            Parameter meta = customParameter.getAnnotation(Parameter.class);
            CustomParameterPattern pattern = Provider.provide(meta.pattern());
            String key = meta.value();
            // Automatically registers to the ArgumentConverterRegistry
            new DynamicPatternConverter<>(customParameter, pattern, key);
        }
    }

}
