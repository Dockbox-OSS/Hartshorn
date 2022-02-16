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

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A low-level interface for easy thread-based actions.
 */
@Deprecated(forRemoval = true, since = "22.2")
public interface ThreadUtils {

    /**
     * Performs a given Runnable on an async thread, returning a Future object to be used for
     * completion checks. The threads are usually provided by the underlying platform.
     *
     * @param runnable The Runnable to run
     *
     * @return The Future object for completion checks
     */
    CompletableFuture<?> performAsync(Runnable runnable);

    /**
     * Performs a given Runnable on an async thread, and blocks the current thread until it completes.
     * The threads are usually provided by the underlying platform.
     *
     * @param <T> The return type of the callable
     * @param callable The callable to use
     */
    <T> void performAsync(Callable<T> callable, Consumer<Exceptional<T>> consumer);

    /**
     * Performs a given Runnable on a sync thread, and blocks the current thread until it completes.
     * The threads are usually provided by the underlying platform.
     *
     * @param <T> The return type of the callable
     * @param callable The callable to use
     *
     * @return The Exceptional holding either the return value or an Exception
     */
    <T> Exceptional<T> performSync(Callable<T> callable);
}
