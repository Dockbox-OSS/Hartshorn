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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

public abstract class ReferencedWrapper<T> implements Wrapper<T> {

    @Getter @Setter
    private transient WeakReference<T> internalReference;

    protected ReferencedWrapper() {
        this.reference(this.constructInitialReference());
    }

    protected ReferencedWrapper(T reference) {
        this.reference(Exceptional.of(reference));
    }

    @Override
    public Exceptional<T> reference() {
        this.updateReference().present(t -> this.internalReference(new WeakReference<>(t)));
        return Exceptional.of(this.internalReference().get());
    }

    @Override
    public void reference(@NotNull Exceptional<T> reference) {
        this.internalReference(reference.map(WeakReference::new).get(() -> new WeakReference<>(null)));
    }

    public Exceptional<T> updateReference() {
        return this.updateTask().apply(this.internalReference().get());
    }

    public Function<T, Exceptional<T>> updateTask() {
        return value -> Exceptional.of(value).orElse(() -> this.constructInitialReference().orNull());
    }
}
