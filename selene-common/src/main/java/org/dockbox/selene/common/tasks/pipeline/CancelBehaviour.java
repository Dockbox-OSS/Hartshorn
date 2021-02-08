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

package org.dockbox.selene.common.tasks.pipeline;

import org.dockbox.selene.common.tasks.pipeline.pipelines.ConvertiblePipeline;
import org.dockbox.selene.common.tasks.pipeline.pipelines.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum CancelBehaviour
{
    UNCANCELLABLE,
    @SuppressWarnings("ReturnOfNull") DISCARD(output -> null),
    CONVERT((output, converter) -> (null == converter) ? output : converter.apply(output)),
    RETURN(output -> output);

    @Nullable
    private final Function<Object, Object> function;
    @Nullable
    private final BiFunction<Object, Function<Object, Object>, Object> biFunction;

    CancelBehaviour()
    {
        this.function = null;
        this.biFunction = null;
    }

    CancelBehaviour(@NotNull Function<Object, Object> function)
    {
        this.function = function;
        this.biFunction = null;
    }

    CancelBehaviour(@NotNull BiFunction<Object, Function<Object, Object>, Object> function)
    {
        this.function = null;
        this.biFunction = function;
    }

    /**
     * Determines what should be returned by a {@link Pipeline} when its cancelled by calling the {@link Function} on it.
     *
     * @param output
     *         The output of the pipeline to be acted upon by the cancel behaviour
     *
     * @return The output after it has been acted upon
     * @throws UnsupportedOperationException
     *         If the cancel behaviour has no {@link Function}
     */
    public Object act(Object output)
    {
        if (null == this.function)
        {
            throw new UnsupportedOperationException("The provided cancel behaviour is not supported by this pipeline.");
        }
        return this.function.apply(output);
    }

    /**
     * Determines what should be returned by a {@link ConvertiblePipeline} when its cancelled by calling the cancellable behaviours
     * {@link Function} or {@link BiFunction} on it.
     *
     * @param output
     *         The output of the pipeline to be acted upon by the cancel behaviour
     * @param converter
     *         The converter of the current pipeline
     *
     * @return The output after it has been acted upon
     * @throws UnsupportedOperationException
     *         If the cancel behaviour has no {@link Function} or {@link BiFunction}
     */
    public Object act(Object output, @Nullable Function<Object, Object> converter)
    {
        if (null != this.function)
        {
            return this.function.apply(output);
        }

        if (null != this.biFunction)
        {
            return this.biFunction.apply(output, converter);
        }
        throw new UnsupportedOperationException("The provided cancel behaviour is not supported by this pipeline.");
    }
}
