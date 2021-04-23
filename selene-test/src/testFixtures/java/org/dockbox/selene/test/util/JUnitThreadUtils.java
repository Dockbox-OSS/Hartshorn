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

package org.dockbox.selene.test.util;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.task.ThreadUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class JUnitThreadUtils implements ThreadUtils {
    @Override
    public Future<?> performAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable);
    }

    @Override
    public Future<?> performSync(Runnable runnable) {
        return new FutureTask<>(runnable, null);
    }

    @Override
    public <T> Exceptional<T> awaitAsync(Callable<T> callable) {
        // During tests we skip async behavior
        return Exceptional.of(callable);
    }

    @Override
    public <T> Exceptional<T> awaitSync(Callable<T> callable) {
        return Exceptional.of(callable);
    }
}
