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

package org.dockbox.selene.integrated.data.pipeline.pipes;

import org.dockbox.selene.core.objects.optional.Exceptional;

@FunctionalInterface
public interface CancellablePipe<I, O> extends IPipe<I, O> {

    O execute(Runnable cancelPipeline, I input, Throwable throwable);

    @Override
    default O apply(Exceptional<I> input) {
        return this.execute(null, input.get(), input.orElseExcept(null));
    }

    @Override
    default Class<CancellablePipe> getType() {
        return CancellablePipe.class;
    }

    static <I, O> CancellablePipe<I, O> of(CancellablePipe<I, O> pipe) {
        return pipe;
    }
}
