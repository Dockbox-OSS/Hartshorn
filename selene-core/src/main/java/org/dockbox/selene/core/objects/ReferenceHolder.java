/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.objects;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public abstract class ReferenceHolder<T> {

    private transient WeakReference<T> reference;

    protected ReferenceHolder(@NotNull Exceptional<T> reference) {
        this.setReference(reference);
    }

    public boolean referenceExists() {
        return this.getReference().isPresent();
    }

    public Exceptional<T> getReference() {
        Exceptional<T> updated = this.getUpdateReferenceTask().apply(this.reference.get());
        updated.ifPresent(t -> this.reference = new WeakReference<>(t));
        return Exceptional.ofNullable(this.reference.get());
    }

    protected void setReference(@NotNull Exceptional<T> reference) {
        this.reference = reference.map(WeakReference::new).orElseGet(() -> new WeakReference<>(null));
    }

    public abstract Function<T, Exceptional<T>> getUpdateReferenceTask();

    public abstract Class<?> getReferenceType();
}
