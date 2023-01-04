/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.cache;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Simple immutable object indicating the expiration of a {@link Cache}.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public class Expiration {

    private static final Expiration NEVER = new Expiration(-1, TimeUnit.MILLISECONDS);
    private final long amount;
    private final TimeUnit unit;

    private Expiration(final long amount, final TimeUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    /**
     * The amount of time (units) before the cache expires.
     * @return the amount of time before the cache expires
     */
    public long amount() {
        return this.amount;
    }

    /**
     * The unit of time before the cache expires.
     * @return the unit of time before the cache expires
     */
    public TimeUnit unit() {
        return this.unit;
    }

    /**
     * Creates a new {@link Duration} representing the amount of time before the cache expires.
     * @return the new {@link Duration}
     */
    public Duration toDuration() {
        return Duration.of(this.amount, this.unit.toChronoUnit());
    }

    /**
     * Returns a new {@link Expiration} using the given expiration time.
     * @param amount the amount of time before the cache expires
     * @param unit the unit of time before the cache expires
     * @return the new {@link Expiration}
     */
    public static Expiration of(final long amount, final TimeUnit unit) {
        return new Expiration(amount, unit);
    }

    /**
     * Returns a new {@link Expiration} using the given expiration time.
     * @param amount the amount of time before the cache expires
     * @param unit the unit of time before the cache expires
     * @return the new {@link Expiration}
     */
    public static Expiration of(final long amount, final ChronoUnit unit) {
        return new Expiration(amount, TimeUnit.of(unit));
    }

    /**
     * Returns a new {@link Expiration} using the given {@link Duration}.
     * @param duration the {@link Duration} before the cache expires
     * @return the new {@link Expiration}
     */
    public static Expiration of(final Duration duration) {
        return new Expiration(duration.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Returns a {@link Expiration} that never expires.
     * @return the {@link Expiration} that never expires
     */
    public static Expiration never() {
        return NEVER;
    }
}
