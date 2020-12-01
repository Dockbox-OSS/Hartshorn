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

package org.dockbox.selene.integrated.event.processors;

import com.boydti.fawe.object.FawePlayer;

import org.dockbox.selene.core.exceptions.SkipEventException;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.core.events.processing.AbstractEventParamProcessor;
import org.dockbox.selene.core.events.handling.IWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;


/**
 Converts a {@link Player} instance into a {@link FawePlayer}, or skips the listener.
 */
public class FaweSourceProcessor extends AbstractEventParamProcessor<FaweSource> {
    @Override
    public @NotNull Class<FaweSource> getAnnotationClass() {
        return FaweSource.class;
    }

    @Override
    public @Nullable Object process(Object object, FaweSource annotation, Event event, Parameter parameter, IWrapper wrapper) throws SkipEventException {
        if (object instanceof Player) {
            Exceptional<FawePlayer<?>> player = ((Player) object).getFawePlayer();
            if (player.isPresent()) return player.get();
        }
        throw new SkipEventException();
    }
}
