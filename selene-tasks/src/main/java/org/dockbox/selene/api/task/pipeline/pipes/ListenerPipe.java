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

package org.dockbox.selene.api.task.pipeline.pipes;

import org.dockbox.selene.api.domain.Exceptional;

@SuppressWarnings("InterfaceNeverImplemented") // API type
@FunctionalInterface
public interface ListenerPipe<I> extends StandardPipe<I, I> {

    static <I> ListenerPipe<I> of(ListenerPipe<I> pipe) {
        return pipe;
    }

    @Override
    default I apply(Exceptional<I> input) throws Exception {
        this.execute(input.orNull());
        return input.orNull();
    }

    void execute(I input) throws Exception;
}
