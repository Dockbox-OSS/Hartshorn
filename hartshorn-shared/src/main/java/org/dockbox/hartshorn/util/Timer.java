/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.util;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * A utility class for measuring the duration of code execution.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class Timer {

    private final long start;

    private Timer(long start) {
        this.start = start;
    }

    /**
     * Starts a new timer.
     *
     * @return a new {@link Timer} instance
     */
    public static Timer start() {
        return new Timer(System.nanoTime());
    }

    /**
     * Stops the timer and returns the duration since it was started. Can be re-used to measure
     * multiple durations.
     *
     * @return the duration since the timer was started
     */
    public Duration stop() {
        long end = System.nanoTime();
        return Duration.ofNanos(end - start);
    }

    /**
     * Executes the given {@link Runnable} and returns the duration of its execution.
     *
     * @param runnable the {@link Runnable} to execute
     * @return the duration of the execution
     */
    public static Duration execute(Runnable runnable) {
        return execute(() -> {
            runnable.run();
            return null;
        }).duration();
    }

    /**
     * Executes the given {@link Supplier} and returns a {@link Timing} object containing the
     * duration of its execution and the result of the supplier.
     *
     * @param supplier the {@link Supplier} to execute
     * @param <T> the type of the result returned by the supplier
     *
     * @return a {@link Timing} object containing the duration and result of the execution
     */
    public static <T> Timing<T> execute(Supplier<T> supplier) {
        long start = System.nanoTime();
        T result = supplier.get();
        long end = System.nanoTime();
        return new Timing<>(Duration.ofNanos(end - start), result);
    }

    /**
     * A record representing the result of a timed execution, containing the duration and the
     * result.
     *
     * @param duration the duration of the execution
     * @param result the result of the execution
     * @param <T> the type of the result
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public record Timing<T>(Duration duration, T result) {}
}
