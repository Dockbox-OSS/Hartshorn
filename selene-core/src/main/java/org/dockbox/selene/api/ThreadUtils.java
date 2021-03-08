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

package org.dockbox.selene.api;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.server.properties.InjectorProperty;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * A low-level interface for easy thread-based actions. For all cases this should only be accessed
 * through {@link org.dockbox.selene.api.server.bootstrap.InjectableBootstrap#getInstance(Class,
 * InjectorProperty[])}}.
 */
public interface ThreadUtils {

    /**
     * Performs a given Runnable on a async thread, returning a Future object to be used for
     * completion checks. The threads are usually provided by the underlying platform.
     *
     * @param runnable
     *         The Runnable to run
     *
     * @return The Future object for completion checks
     */
    Future<?> performAsync(Runnable runnable);

    /**
     * Performs a given Runnable on a sync thread, returning a Future object to be used for completion
     * checks. The threads are usually provided by the underlying platform.
     *
     * @param runnable
     *         The Runnable to run
     *
     * @return The Future object for completion checks
     */
    Future<?> performSync(Runnable runnable);

    /**
     * Performs a given Runnable on a async thread, and blocks the current thread until it completes.
     * The threads are usually provided by the underlying platform.
     *
     * @param <T>
     *         The return type of the callabe
     * @param callable
     *         The callable to use
     *
     * @return The Exceptional holding either the return value or a Exception
     */
    <T> Exceptional<T> awaitAsync(Callable<T> callable);

    /**
     * Performs a given Runnable on a sync thread, and blocks the current thread until it completes.
     * The threads are usually provided by the underlying platform.
     *
     * @param <T>
     *         The return type of the callabe
     * @param callable
     *         The callable to use
     *
     * @return The Exceptional holding either the return value or a Exception
     */
    <T> Exceptional<T> awaitSync(Callable<T> callable);
}
