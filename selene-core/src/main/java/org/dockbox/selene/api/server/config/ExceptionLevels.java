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

package org.dockbox.selene.api.server.config;

import org.dockbox.selene.api.ExceptionHelper;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.tasks.TriConsumer;

import java.util.Arrays;

public enum ExceptionLevels
{
    FRIENDLY((message, exception, stacktrace) -> {
        Selene.provide(ExceptionHelper.class)
                .printFriendly(message, exception, stacktrace);
    }),
    MINIMAL((message, exception, stacktrace) -> {
        Selene.provide(ExceptionHelper.class)
                .printMinimal(message, exception, stacktrace);
    }),
    NATIVE((message, exception, stacktrace) -> {
        Selene.log().error(message);
        Selene.log().error(Arrays.toString(exception.getStackTrace()));
    });

    private final TriConsumer<String, Throwable, Boolean> consumer;

    ExceptionLevels(TriConsumer<String, Throwable, Boolean> consumer)
    {

        this.consumer = consumer;
    }

    public void handle(String message, Throwable exception, boolean stacktrace)
    {
        this.consumer.accept(message, exception, stacktrace);
    }
}
