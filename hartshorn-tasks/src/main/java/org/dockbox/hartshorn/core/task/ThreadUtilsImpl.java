/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.core.task;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Deprecated(forRemoval = true, since = "22.2")
@ComponentBinding(ThreadUtils.class)
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
