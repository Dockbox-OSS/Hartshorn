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

package org.dockbox.hartshorn.core.task;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.annotations.inject.Binds;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Binds(ThreadUtils.class)
public class ThreadUtilsImpl implements ThreadUtils {

    @Override
    public CompletableFuture<?> performAsync(final Runnable runnable) {
        return CompletableFuture.runAsync(runnable);
    }

    @Override
    public <T> void performAsync(final Callable<T> callable, final Consumer<Exceptional<T>> consumer) {
        CompletableFuture.supplyAsync(() -> Exceptional.of(callable)).thenAccept(consumer);
    }

    @Override
    public <T> Exceptional<T> performSync(final Callable<T> callable) {
        return Exceptional.of(callable);
    }
}
