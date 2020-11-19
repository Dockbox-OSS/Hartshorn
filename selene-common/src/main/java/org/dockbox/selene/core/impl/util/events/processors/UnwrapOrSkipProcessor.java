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

import org.dockbox.selene.core.annotations.UnwrapOrSkip;
import org.dockbox.selene.core.exceptions.SkipEventException;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.util.events.AbstractEventParamProcessor;
import org.dockbox.selene.core.util.events.IWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.util.Optional;

public class UnwrapOrSkipProcessor extends AbstractEventParamProcessor<UnwrapOrSkip> {

    @Override
    public @NotNull Class<UnwrapOrSkip> getAnnotationClass() {
        return UnwrapOrSkip.class;
    }

    @Override
    public @Nullable Object process(Object object, UnwrapOrSkip annotation, Event event, Parameter parameter, IWrapper wrapper) throws SkipEventException {
        if (object instanceof Exceptional<?>) {
            if (((Exceptional<?>) object).isPresent()) return ((Exceptional<?>) object).get();
            else throw new SkipEventException();

        } else if (object instanceof Optional<?>) {
            if (((Optional<?>) object).isPresent()) return ((Optional<?>) object).get();
            else throw new SkipEventException();

        } else return object; // Already unwrapped
    }
}
