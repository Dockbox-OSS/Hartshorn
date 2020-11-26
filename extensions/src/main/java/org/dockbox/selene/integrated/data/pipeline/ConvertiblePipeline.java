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

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineConverterException;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineException;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ConvertiblePipeline<P, I> extends AbstractPipeline<P, I> {

    @Nullable private ConvertiblePipeline<P, ?> previousPipeline;
    @Nullable private ConvertiblePipeline<P, ?> nextPipeline;

    @Nullable private Function<? super I, ?> converter;
    private Class<I> inputClass;

    protected ConvertiblePipeline(Class<I> inputClass) {
        this.inputClass = inputClass;
    }

    @Override
    public ConvertiblePipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        return (ConvertiblePipeline<P, I>)super.addPipe(pipe);
    }

    @SafeVarargs
    @Override
    public final ConvertiblePipeline<P, I> addPipes(@NotNull IPipe<I, I>... pipes) {
        return (ConvertiblePipeline<P, I>)super.addPipes(pipes);
    }

    @Override
    public ConvertiblePipeline<P, I> addPipeline(@NotNull AbstractPipeline<?, I> pipeline) {
        return (ConvertiblePipeline<P, I>)super.addPipeline(pipeline);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Exceptional<I> process(@NotNull P input, @Nullable Throwable throwable) {
        Exceptional<I> exceptionalInput;
        if (null == this.previousPipeline) {
            //This should never be called, unless this class was used initially instead of ConvertiblePipelineSource.
            //(Which shouldn't be possible due to the protected constructor).
            if (SeleneUtils.isAssignableFrom(this.inputClass, input.getClass())) {
                exceptionalInput = Exceptional.ofNullable((I) input, throwable);
            }
            else {
                throw new IllegalPipelineException(
                    String.format("Pipeline sources types don't match. [Expected: %s, Actual: %s]",
                        this.inputClass, input.getClass()));
            }
        }
        else {
            exceptionalInput = this.previousPipeline.processConverted(input, throwable);
        }

        return super.process(exceptionalInput);
    }

    @Override
    public ConvertiblePipeline<P, I> setCancellable(boolean isCancellable) {
        //Only allow this pipeline to be cancellable if theres not a pipeline after this.
        super.setCancellable(null == this.nextPipeline && isCancellable);
        return this;
    }

    @SuppressWarnings("unchecked")
    private <K> Exceptional<K> processConverted(@NotNull P input, @Nullable Throwable throwable) throws IllegalPipelineException {
        Exceptional<I> result = this.process(input, throwable);
        Exceptional<K> output = (Exceptional<K>) result.map(this.converter);

        //If the mapper returns null (If it wasn't already null)
        if (result.isPresent() && !output.isPresent()) {
            throw new IllegalPipelineConverterException(
                String.format("The pipeline converter returned null. [Input: %s, Output: %s]",
                    result, output));
        }
        return output;
    }

    public <K> ConvertiblePipeline<P, K> convertPipeline(Function<? super I, K> converter, Class<K> inputClass) {
        if (this.inputClass.equals(inputClass)) {
            Selene.log().warn(
                "The use of a converter is unnecessary as the pipeline doesn't change type. Consider using a pipe instead.");
        }

        this.converter = converter;

        ConvertiblePipeline<P, K> nextPipeline = new ConvertiblePipeline<>(inputClass);
        nextPipeline.previousPipeline = this;
        this.nextPipeline = nextPipeline;

        //If the current pipeline is cancellable, make the next pipeline cancellable.
        nextPipeline.setCancellable(this.isCancellable());
        //As it is no longer the final pipeline, this is no longer cancellable.
        this.setCancellable(false);
        return nextPipeline;
    }

    @SuppressWarnings("unchecked")
    public <K> ConvertiblePipeline<P, K> removePipeline(Class<K> inputClass) {
        this.clearPipes();

        if (null == this.previousPipeline) {
            return (ConvertiblePipeline<P, K>)this;
        }
        else {
            if (SeleneUtils.isAssignableFrom(inputClass, this.previousPipeline.inputClass)) {
                this.previousPipeline.nextPipeline = null;
                this.previousPipeline.converter = null;
                return (ConvertiblePipeline<P, K>) this.previousPipeline;
            }
            else {
                throw new IllegalArgumentException(
                    String.format("Input class was not correct. [Expected: %s, Actual: %s]",
                        this.previousPipeline.inputClass, inputClass));
            }
        }
    }

    @Override
    public int size() {
        int size = super.size();
        if (null != this.previousPipeline) size += this.previousPipeline.size();
        if (null != this.converter) size++;

        return size;
    }
}
