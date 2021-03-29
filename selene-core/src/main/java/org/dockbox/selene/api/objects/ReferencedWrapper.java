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

package org.dockbox.selene.api.objects;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public abstract class ReferencedWrapper<T> implements Wrapper<T> {

    private transient WeakReference<T> reference;

    protected ReferencedWrapper() {
        this.setReference(this.constructInitialReference());
    }

    protected ReferencedWrapper(T reference) {
        this.setReference(Exceptional.of(reference));
    }

    @Override
    public Exceptional<T> getReference() {
        this.updateReference().present(t -> this.setInternalReference(new WeakReference<>(t)));
        return Exceptional.of(this.getInternalReference().get());
    }

    @Override
    public void setReference(@NotNull Exceptional<T> reference) {
        this.setInternalReference(reference.map(WeakReference::new).get(() -> new WeakReference<>(null)));
    }

    public Exceptional<T> updateReference() {
        return this.getUpdateReferenceTask().apply(this.getInternalReference().get());
    }

    protected WeakReference<T> getInternalReference() {
        return this.reference;
    }

    public Function<T, Exceptional<T>> getUpdateReferenceTask() {
        return value -> Exceptional.of(value).then(() -> constructInitialReference().orNull());
    }

    protected void setInternalReference(WeakReference<T> reference) {
        this.reference = reference;
    }
}
