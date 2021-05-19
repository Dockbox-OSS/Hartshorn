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

package org.dockbox.selene.api.exceptions;

import org.dockbox.selene.api.TriConsumer;
import org.dockbox.selene.di.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public enum ExceptionLevels implements ExceptionHandle {
    FRIENDLY((message, exception, stacktrace) -> {
        Provider.provide(ExceptionHelper.class).printFriendly(message, exception, stacktrace);
    }),
    MINIMAL((message, exception, stacktrace) -> {
        Provider.provide(ExceptionHelper.class).printMinimal(message, exception, stacktrace);
    }),
    NATIVE((message, exception, stacktrace) -> {
        final Logger log = LoggerFactory.getLogger("Selene::native");
        log.error(message);
        log.error(Arrays.toString(exception.getStackTrace()));
    });

    private final TriConsumer<String, Throwable, Boolean> consumer;

    ExceptionLevels(TriConsumer<String, Throwable, Boolean> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void handle(String message, Throwable exception, boolean stacktrace) {
        this.consumer.accept(message, exception, stacktrace);
    }
}
