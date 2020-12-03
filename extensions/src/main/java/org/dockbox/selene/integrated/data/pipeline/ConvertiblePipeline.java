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

    /**
     * A private constructor for the pipeline as all convertible pipelines should be instantiated initally as {@link ConvertiblePipelineSource}
     * @param inputClass The {@link Class} of the input type {@link I}
     */
    protected ConvertiblePipeline(Class<I> inputClass) {
        this.inputClass = inputClass;
    }

    /**
     * Internally calls {@link AbstractPipeline#addPipe(IPipe)} and returns itself.
     * @param pipe The non-null {@link IPipe} to add to the pipeline.
     * @return Itself.
     */
    @Override
    public ConvertiblePipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        return (ConvertiblePipeline<P, I>)super.addPipe(pipe);
    }

    /**
     * Internally calls {@link AbstractPipeline#addPipes(IPipe[])} and returns itself.
     * @param pipes The non-null varargs of {@link IPipe}s to add to the pipeline.
     * @return Itself.
     */
    @SafeVarargs
    @Override
    public final ConvertiblePipeline<P, I> addPipes(@NotNull IPipe<I, I>... pipes) {
        return (ConvertiblePipeline<P, I>)super.addPipes(pipes);
    }

    /**
     * Internally calls {@link AbstractPipeline#addPipeline(AbstractPipeline)} and returns itself.
     * @param pipeline The non-null {@link AbstractPipeline} whos {@link IPipe}s should be added to this pipeline.
     * @return Itself.
     */
    @Override
    public ConvertiblePipeline<P, I> addPipeline(@NotNull AbstractPipeline<?, I> pipeline) {
        return (ConvertiblePipeline<P, I>)super.addPipeline(pipeline);
    }

    /**
     * Processes an input by recursively calling the process method of the previous pipeline. This will eventually call
     * the {@link ConvertiblePipelineSource#process(Object, Throwable)}, which will stop the recursive calling of previous
     * pipelines. If somehow, a convertible pipeline is used as the source of the pipeline, then it will attempt to cast
     * the {@link P} input to {@link I}. 
     * @param input The non-null input value.
     * @param throwable The nullable input {@link Throwable}.
     * @return An {@link Exceptional} containing the output. If the output is not present it will contain a throwable describing why.
     */
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

    /**
     * Set if this pipeline can be cancelled.
     * <b>NOTE:</b> A pipeline can only be cancelled if it is the last pipeline Doesn't have a pipeline after it).
     * When you convert the pipeline to another type, it automatically sets the current pipeline to not cancellable.
     * @param isCancellable A boolean describing if the pipeline is cancellable or not.
     * @return Itself.
     */
    @Override
    public ConvertiblePipeline<P, I> setCancellable(boolean isCancellable) {
        //Only allow this pipeline to be cancellable if theres not a pipeline after this.
        super.setCancellable(null == this.nextPipeline && super.isCancellable());
        return this;
    }

    /**
     * Takes an input and internally processes it by calling {@link ConvertiblePipeline#process(Object, Throwable)} and
     * then converting the output using the converter provided in {@link ConvertiblePipeline#convertPipeline(Function, Class)}.
     * @param input The non-null {@link P} input to be processed by the pipeline.
     * @param throwable An nullable {@link Throwable} that may have been thrown while processing the input.
     * @param <K> The type of the next pipeline.
     * @return An {@link Exceptional} of type {@link K} containing the converted output.
     * @throws IllegalPipelineConverterException If the pipeline converter returns null even though the input wasn't null.
     */
    @SuppressWarnings("unchecked")
    protected <K> Exceptional<K> processConverted(@NotNull P input, @Nullable Throwable throwable) throws IllegalPipelineConverterException {
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

    /**
     * Converts the pipeline to a different type. <b>NOTE:</b> When you convert a pipeline, this automatically makes it
     * uncancellable and so will throw an {@link IllegalPipelineException} if you try and process an input with any
     * {@link org.dockbox.selene.integrated.data.pipeline.pipes.CancellablePipe}s in this pipeline.
     * @param converter A {@link Function} that takes in an {@link I} input and returns a converted {@link K} output.
     * @param inputClass An {@link Class} of the type of the new pipeline.
     * @param <K> The type of the new pipeline.
     * @return A pipeline of the new type.
     */
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

    /**
     * Removes the current pipeline by clearing the {@link IPipe}s and any links with previous and next pipelines.
     * @param previousClass The {@link Class} of the previous pipeline's input.
     * @param <K> The type of the previous pipeline's input.
     * @return The previous pipeline. If there are no previous pipelines, then it returns itself.
     */
    @SuppressWarnings("unchecked")
    public <K> ConvertiblePipeline<P, K> removePipeline(Class<K> previousClass) {
        super.clearPipes();

        if (null == this.previousPipeline) {
            return (ConvertiblePipeline<P, K>)this;
        }
        else {
            if (SeleneUtils.isAssignableFrom(previousClass, this.previousPipeline.inputClass)) {
                this.previousPipeline.nextPipeline = null;
                this.previousPipeline.converter = null;

                if (null != this.nextPipeline) {
                    this.nextPipeline.previousPipeline = null;
                    this.nextPipeline = null;
                    this.converter = null;
                }

                return (ConvertiblePipeline<P, K>) this.previousPipeline;
            }
            else {
                throw new IllegalArgumentException(
                    String.format("Input class was not correct. [Expected: %s, Actual: %s]",
                        this.previousPipeline.inputClass, previousClass));
            }
        }
    }

    /**
     * Calculates the size of the pipeline by recursively getting the size of the previous pipeline. <b>NOTE:</b> The
     * size includes the number of converters in the pipeline too.
     * @return The number of {@link IPipe} and converters in the pipeline.
     */
    @Override
    public int size() {
        int size = super.size();
        if (null != this.previousPipeline) size += this.previousPipeline.size();
        if (null != this.converter) size++;

        return size;
    }
}
