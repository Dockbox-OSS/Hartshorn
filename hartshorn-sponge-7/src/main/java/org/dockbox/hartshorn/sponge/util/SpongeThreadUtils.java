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
import org.dockbox.hartshorn.sponge.Sponge7Application;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The SpongePowered implementation for {@link ThreadUtils}, using Sponge's {@link
 * org.spongepowered.api.scheduler.SpongeExecutorService} for underlying thread access.
 */
public class SpongeThreadUtils implements ThreadUtils {

    @Override
    public Future<?> performAsync(Runnable runnable) {
        SpongeExecutorService ses = Sponge.getScheduler().createAsyncExecutor(Sponge7Application.container());
        return ses.submit(runnable);
    }

    @Override
    public Future<?> performSync(Runnable runnable) {
        SpongeExecutorService ses = Sponge.getScheduler().createSyncExecutor(Sponge7Application.container());
        return ses.submit(runnable);
    }

    @Override
    public <T> Exceptional<T> awaitAsync(Callable<T> callable) {
        SpongeExecutorService ses = Sponge.getScheduler().createAsyncExecutor(Sponge7Application.container());
        try {
            return Exceptional.of(ses.submit(callable).get());
        }
        catch (InterruptedException | ExecutionException e) {
            return Exceptional.of(e);
        }
    }

    @Override
    public <T> Exceptional<T> awaitSync(Callable<T> callable) {
        SpongeExecutorService ses = Sponge.getScheduler().createSyncExecutor(Sponge7Application.container());
        try {
            return Exceptional.of(ses.submit(callable).get());
        }
        catch (InterruptedException | ExecutionException e) {
            return Exceptional.of(e);
        }
    }
}