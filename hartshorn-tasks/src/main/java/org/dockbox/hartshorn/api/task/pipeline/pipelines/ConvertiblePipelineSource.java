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

package org.dockbox.hartshorn.api.task.pipeline.pipelines;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConvertiblePipelineSource<I> extends ConvertiblePipeline<I, I> {

    /**
     * Calls the super constructor to instantiate a new convertible pipeline.
     *
     * @param inputClass
     *         The {@link Class} of the {@code I} input type
     */
    public ConvertiblePipelineSource(final Class<I> inputClass) {
        super(inputClass);
    }

    /**
     * Processes an input by first wrapping it in an {@link Exceptional}.
     *
     * @param input
     *         The non-null {@code I} input to be processed by the pipeline
     * @param throwable
     *         A nullable {@link Throwable} that may wish to be passed in
     *
     * @return An {@link Exceptional} containing the {@code I} output
     */
    @Override
    public Exceptional<I> process(@NotNull final I input, @Nullable final Throwable throwable) {
        final Exceptional<I> exceptionalInput = Exceptional.of(input, throwable);
        return super.process(exceptionalInput);
    }
}
