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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;

/**
 * Default implementation of {@link Cache}.
 * @see Cache
 */
@Binds(Cache.class)
public class SimpleCache<T> implements Cache<T>, InjectableType {

    private Expiration expiration;
    private Collection<T> content;

    @Override
    public Exceptional<Collection<T>> get() {
        return Exceptional.of(this.content);
    }

    @Override
    public void populate(Collection<T> content) {
        if (this.content != null) throw new IllegalStateException("Cannot populate existing cache, ensure the existing content is evicted before repopulating.");
        else {
            this.content = content;
            this.scheduleEviction();
        }
    }

    private void scheduleEviction() {
        // Negative amounts are considered non-expiring
        if (this.expiration.amount() > 0) {
            TaskRunner.create().acceptDelayed(this::evict, this.expiration.amount(), this.expiration.unit());
        }
    }

    @Override
    public void update(T object) {
        if (this.content == null) {
            this.populate(HartshornUtils.emptyList());
        }
        this.content.add(object);
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
    public void enable(InjectorProperty<?>... properties) {
        final InjectorProperty<Expiration> property = Bindings.property(ExpirationProperty.KEY, Expiration.class, properties);
        if (property != null) {
            this.expiration = property.value();
        } else {
            throw new IllegalArgumentException("Expected expiration property to be present");
        }
    }
}
