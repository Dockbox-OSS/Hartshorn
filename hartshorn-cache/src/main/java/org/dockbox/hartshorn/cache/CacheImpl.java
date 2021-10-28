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

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.task.TaskRunner;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.core.properties.AttributeHolder;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Default implementation of {@link Cache}.
 *
 * @see Cache
 */
@Binds(Cache.class)
public class CacheImpl<T> implements Cache<T>, AttributeHolder {

    private Expiration expiration;
    private T content;

    @Inject
    private ApplicationContext applicationContext;

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
            TaskRunner.create(this.applicationContext).acceptDelayed(this::evict, this.expiration.amount(), this.expiration.unit());
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
    public void apply(final Attribute<?> property) {
        if (property instanceof ExpirationAttribute expirationAttribute) {
            this.expiration = expirationAttribute.value();
        }
    }

    @Override
    public void enable() {
        if (this.expiration == null) throw new IllegalArgumentException("Expected expiration property to be present");
    }
}
