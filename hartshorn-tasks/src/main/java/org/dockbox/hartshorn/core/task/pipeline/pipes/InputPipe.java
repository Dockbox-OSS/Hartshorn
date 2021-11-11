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

package org.dockbox.hartshorn.core.task.pipeline.pipes;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

@FunctionalInterface
public interface InputPipe<I, O> extends StandardPipe<I, O> {

    static <I, O> InputPipe<I, O> of(final InputPipe<I, O> pipe) {
        return pipe;
    }

    @Override
    default O apply(final Exceptional<I> input) throws ApplicationException {
        return this.execute(input.orNull());
    }

    O execute(I input) throws ApplicationException;
}
