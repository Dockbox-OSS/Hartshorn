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

package org.dockbox.darwin.sponge.util.thread;

import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.util.threads.ThreadUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SpongeThreadUtils implements ThreadUtils {

    @Override
    public Future<?> performAsync(Runnable runnable) {
        SpongeExecutorService ses = Sponge.getScheduler().createAsyncExecutor(Server.getServer());
        return ses.submit(runnable);
    }

    @Override
    public Future<?> performSync(Runnable runnable) {
        SpongeExecutorService ses = Sponge.getScheduler().createSyncExecutor(Server.getServer());
        return ses.submit(runnable);
    }

    @Override
    public <T> Optional<T> awaitAsync(Callable<T> callable) {
        SpongeExecutorService ses = Sponge.getScheduler().createAsyncExecutor(Server.getServer());
        try {
            return Optional.ofNullable(ses.submit(callable).get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> awaitSync(Callable<T> callable) {
        SpongeExecutorService ses = Sponge.getScheduler().createSyncExecutor(Server.getServer());
        try {
            return Optional.ofNullable(ses.submit(callable).get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }


}
