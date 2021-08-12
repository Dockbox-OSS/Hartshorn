package org.dockbox.hartshorn.api.task;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.inject.Binds;

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
