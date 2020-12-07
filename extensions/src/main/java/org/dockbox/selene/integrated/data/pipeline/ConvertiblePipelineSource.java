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

package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.Exceptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConvertiblePipelineSource<I> extends ConvertiblePipeline<I, I> {

    /**
     * Calls the super constructor to instantiate a new convertible pipeline.
     * @param inputClass The {@link Class} of the {@link I input} type.
     */
    protected ConvertiblePipelineSource(Class<I> inputClass) {
        super(inputClass);
    }

    /**
     * Processes an input by first wrapping it in an {@link Exceptional}.
     * @param input The non-null {@link I input} to be processed by the pipeline.
     * @param throwable A nullable {@link Throwable} that may wish to be passed in.
     * @return An {@link Exceptional} containing the {@link I output}. If the output is not present it will contain a throwable describing why.
     */
    @Override
    public Exceptional<I> process(@NotNull I input, @Nullable Throwable throwable) {
        Exceptional<I> exceptionalInput = Exceptional.ofNullable(input, throwable);
        return super.process(exceptionalInput);
    }
}
