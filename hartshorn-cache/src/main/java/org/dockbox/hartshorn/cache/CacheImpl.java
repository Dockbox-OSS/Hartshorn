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

import org.dockbox.hartshorn.core.Enableable;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Inject;

/**
 * Default implementation of {@link Cache}.
 *
 * @see Cache
 */
public class CacheImpl<T> implements Cache<T>, Enableable {

    private final Expiration expiration;
    private T content;

    @Inject
    private ApplicationContext applicationContext;

    @Bound
    public CacheImpl(final Expiration expiration) {
        this.expiration = expiration;
    }

    @Override
    public Exceptional<T> get() {
        return Exceptional.of(this.content);
    }

    @Override
    public void populate(final T content) {
        if (this.content != null) throw new IllegalStateException("Cannot populate existing cache, ensure the existing content is evicted before repopulating.");
        else {
            this.content = content;
            this.scheduleEviction();
        }
    }

    @Override
    public void update(final T object) {
        this.content = object;
    }

    private void scheduleEviction() {
        // Negative amounts are considered non-expiring
        if (this.expiration.amount() > 0) {
            final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
            executor.schedule(this::evict, this.expiration.amount(), this.expiration.unit());
            this.applicationContext.log().debug("Scheduled eviction after %d %s".formatted(this.expiration.amount(), this.expiration.unit().name().toLowerCase(Locale.ROOT)));
        }
    }

    @Override
    public void evict() {
        this.content = null;
    }

    @Override
    public boolean canEnable() {
        return this.expiration == null;
    }

    @Override
    public void enable() {
        if (this.expiration == null) throw new IllegalArgumentException("Expected expiration property to be present");
    }
}
