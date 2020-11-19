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

import org.dockbox.selene.core.annotations.Getter;
import org.dockbox.selene.core.exceptions.SkipEventException;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.core.util.events.AbstractEventParamProcessor;
import org.dockbox.selene.core.util.events.IWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.util.concurrent.atomic.AtomicReference;

public class GetterProcessor extends AbstractEventParamProcessor<Getter> {

    @Override
    public @NotNull Class<Getter> getAnnotationClass() {
        return Getter.class;
    }

    @Override
    public @Nullable Object process(Object object, Getter annotation, Event event, Parameter parameter, IWrapper wrapper) throws SkipEventException {
        AtomicReference<Object> arg = new AtomicReference<>(null);
        SeleneUtils.getMethodValue(event, annotation.value(), parameter.getType())
                .ifPresent(arg::set);
        return arg.get();
    }
}
