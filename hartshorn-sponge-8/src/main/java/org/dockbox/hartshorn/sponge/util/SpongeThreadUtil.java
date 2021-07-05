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

package org.dockbox.hartshorn.sponge.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.task.ThreadUtils;
import org.dockbox.hartshorn.sponge.Sponge8Application;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTaskFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class SpongeThreadUtil implements ThreadUtils {

    @Override
    public Future<?> performAsync(Runnable runnable) {
        return Sponge.asyncScheduler().createExecutor(Sponge8Application.container())
                .schedule(runnable, 0, TimeUnit.SECONDS);
    }

    @Override
    public Future<?> performSync(Runnable runnable) {
        return new FutureTask<>(runnable, null);
    }

    @Override
    public <T> Exceptional<T> awaitAsync(Callable<T> callable) {
        final ScheduledTaskFuture<T> future = Sponge.asyncScheduler()
                .createExecutor(Sponge8Application.container())
                .schedule(callable, 0, TimeUnit.SECONDS);
        return SpongeUtil.await(future);
    }

    @Override
    public <T> Exceptional<T> awaitSync(Callable<T> callable) {
        return Exceptional.of(callable);
    }
}
