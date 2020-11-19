/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.util.events.processors;

import org.dockbox.selene.core.annotations.Provided;
import org.dockbox.selene.core.exceptions.SkipEventException;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.events.AbstractEventParamProcessor;
import org.dockbox.selene.core.util.events.IWrapper;
import org.dockbox.selene.core.util.extension.Extension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;

public class ProvidedProcessor extends AbstractEventParamProcessor<Provided> {

    @Override
    public @NotNull Class<Provided> getAnnotationClass() {
        return Provided.class;
    }

    @Override
    public @Nullable Object process(Object object, Provided annotation, Event event, Parameter parameter, IWrapper wrapper) throws SkipEventException {
        Class<?> extensionClass = parameter.getType();
        if (Void.class != annotation.value() && annotation.value().isAnnotationPresent(Extension.class)) {
            extensionClass = annotation.value();
        } else if (wrapper.getListener().getClass().isAnnotationPresent(Extension.class)) {
            extensionClass = wrapper.getListener().getClass();
        }
        return Selene.getInstance(parameter.getType(), extensionClass);
    }
}
