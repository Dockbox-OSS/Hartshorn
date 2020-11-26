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

import org.dockbox.selene.core.objects.optional.Exceptional;

import java.util.function.Function;

public class FieldReferenceHolder<T> extends ReferenceHolder<T> {

    private final Function<T, Exceptional<T>> updateRefTask;
    private final Class<T> type;

    public FieldReferenceHolder(Exceptional<T> reference, Function<T, Exceptional<T>> updateRefTask, Class<T> type) {
        super(reference);
        this.updateRefTask = updateRefTask;
        this.type = type;
    }

    @Override
    public Function<T, Exceptional<T>> getUpdateReferenceTask() {
        return this.updateRefTask;
    }

    @Override
    public Class<?> getReferenceType() {
        return this.type;
    }
}
