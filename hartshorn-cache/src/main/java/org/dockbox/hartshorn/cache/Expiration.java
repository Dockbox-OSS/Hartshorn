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

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.cache.annotations.Expire;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Simple bean indicating the expiration of a {@link Cache}.
 */
public class Expiration {

    private final int amount;
    private final TimeUnit unit;

    public Expiration(final int amount, final TimeUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public int amount() {
        return this.amount;
    }

    public TimeUnit unit() {
        return this.unit;
    }

    public Duration toDuration() {
        return Duration.of(this.amount, this.unit.toChronoUnit());
    }

    public static Expiration of(final Expire expire) {
        return new Expiration(expire.amount(), expire.unit());
    }
}
