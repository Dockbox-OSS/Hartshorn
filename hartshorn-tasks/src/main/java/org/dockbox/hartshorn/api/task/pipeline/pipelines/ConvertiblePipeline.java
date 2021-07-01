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
import org.dockbox.hartshorn.api.task.pipeline.CancelBehaviour;
import org.dockbox.hartshorn.api.task.pipeline.PipelineDirection;
import org.dockbox.hartshorn.api.task.pipeline.exceptions.IllegalPipelineException;
import org.dockbox.hartshorn.api.task.pipeline.pipes.CancellablePipe;
import org.dockbox.hartshorn.api.task.pipeline.pipes.IPipe;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ConvertiblePipeline<P, I> extends AbstractPipeline<P, I> {

    private final Class<I> inputClass;

    @Nullable
    private ConvertiblePipeline<P, ?> previousPipeline;

    @Nullable
    private ConvertiblePipeline<P, ?> nextPipeline;

    @Nullable
    private Function<? super I, ?> converter;

    /**
     * A private constructor for the pipeline as all convertible pipelines should be instantiated
     * initally as {@link ConvertiblePipelineSource}
     *
     * @param inputClass
     *         The {@link Class} of the {@code I} input type
     */
    protected ConvertiblePipeline(Class<I> inputClass) {
        this.inputClass = inputClass;
    }

    /**
     * Internally calls {@link AbstractPipeline#addPipes(IPipe[])} and returns itself.
     *
     * @param pipes
     *         The non-null varargs of {@link IPipe}s to add to the pipeline
     *
     * @return Itself
     */
    @SafeVarargs
    @Override
    public final ConvertiblePipeline<P, I> addPipes(@NotNull IPipe<I, I>... pipes) {
        return (ConvertiblePipeline<P, I>) super.addPipes(pipes);
    }

    /**
     * Internally calls {@link AbstractPipeline#addPipe(IPipe)} and returns itself.
     *
     * @param pipe
     *         The non-null {@link IPipe} to add to the pipeline
     *
     * @return Itself
     */
    @Override
    public ConvertiblePipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        return (ConvertiblePipeline<P, I>) super.addPipe(pipe);
    }

    /**
     * Internally calls {@link AbstractPipeline#addPipeline(AbstractPipeline)} and returns itself.
     *
     * @param pipeline
     *         The non-null {@link AbstractPipeline} whos {@link IPipe}s should be added to
     *         this pipeline
     *
     * @return Itself
     */
    @Override
    public ConvertiblePipeline<P, I> addPipeline(@NotNull AbstractPipeline<?, I> pipeline) {
        return (ConvertiblePipeline<P, I>) super.addPipeline(pipeline);
    }

    /**
     * Processes an {@link Exceptional input} by calling {@link AbstractPipeline#processPipe(IPipe,
     * Exceptional)} on each {@link IPipe} in the pipeline and then returns the output wrapped in an
     * {@link Exceptional}.
     *
     * @param exceptionalInput
     *         A non-null {@link Exceptional} which contains the input value and
     *         throwable
     *
     * @return An {@link Exceptional} containing the output
     */
    @Override
    protected Exceptional<I> process(@NotNull Exceptional<I> exceptionalInput) {
        for (IPipe<I, I> pipe : this.getPipes()) {
            // If the pipelines been cancelled, stop processing any further pipes.
            if (super.isCancelled()) {
                // Only uncancel the pipeline straight away if there's no pipeline after this one
                // as it will be checked again in the processConverted method.
                if (null == this.getNextPipeline()) super.uncancelPipeline();
                return exceptionalInput;
            }

            exceptionalInput = super.processPipe(pipe, exceptionalInput);
        }
        return exceptionalInput;
    }

    /**
     * Sets the cancel behaviour to be used by the entire pipeline by internally calling {@link
     * ConvertiblePipeline#setCancelBehaviour(CancelBehaviour, PipelineDirection)}.
     *
     * @param cancelBehaviour
     *         A {@link CancelBehaviour} describing the cancellability of the pipeline
     *
     * @return Itself
     */
    @Override
    public ConvertiblePipeline<P, I> setCancelBehaviour(CancelBehaviour cancelBehaviour) {
        this.setCancelBehaviour(cancelBehaviour, PipelineDirection.BOTH);
        return this;
    }

    /**
     * Processes an input by recursively calling the process method of the previous pipeline. This
     * will eventually call the {@link ConvertiblePipelineSource#process(Object, Throwable)}, which
     * will stop the recursive calling of previous pipelines. If somehow, a convertible pipeline is
     * used as the source of the pipeline, then it will attempt to cast the {@code P} input to {@code
     * I}.
     *
     * @param input
     *         The non-null input value
     * @param throwable
     *         The nullable input {@link Throwable}
     *
     * @return An {@link Exceptional} containing the {@code I} output. If the output is not present it
     *         will contain a throwable describing why
     */
    @Override
    public Exceptional<I> process(@NotNull P input, @Nullable Throwable throwable) {
        Exceptional<I> exceptionalInput;

        // This should never be called, unless this class was used initially instead of
        // ConvertiblePipelineSource.
        // (Which shouldn't be possible due to the protected constructor).
        if (null == this.getPreviousPipeline()) {
            throw new IllegalPipelineException("Pipeline wasn't constructed using ConvertiblePipelineSource.");
        }
        else {
            exceptionalInput = this.getPreviousPipeline().processConverted(input, throwable);
        }

        return this.process(exceptionalInput);
    }

    /** Cancels the pipeline and all the following pipelines. */
    @Override
    public void cancelPipeline() {
        super.cancelPipeline();

        if (null != this.getNextPipeline()) {
            this.getNextPipeline().cancelPipeline();
        }
    }

    /**
     * Calculates the size of the pipeline by recursively getting the size of the previous pipeline.
     * <b>Note:</b> The size includes the number of converters in the pipeline too.
     *
     * @return The number of {@link IPipe} and converters in the pipeline
     */
    @Override
    public int size() {
        int size = super.size();
        if (null != this.getPreviousPipeline()) size += this.getPreviousPipeline().size();
        if (null != this.converter) size++;

        return size;
    }

    /**
     * Takes an input and internally processes it by calling {@link
     * ConvertiblePipeline#process(Object, Throwable)} and then converting the output using the
     * converter provided in {@link ConvertiblePipeline#convertPipeline(Function, Class)}.
     *
     * @param input
     *         The non-null {@code P} input to be processed by the pipeline
     * @param throwable
     *         An nullable {@link Throwable} that may have been thrown while processing the
     *         input
     * @param <K>
     *         The type of the next pipeline
     *
     * @return An {@link Exceptional} of type {@code K} containing the converted output
     */
    @SuppressWarnings("unchecked")
    protected <K> Exceptional<K> processConverted(@NotNull P input, @Nullable Throwable throwable) {
        Exceptional<I> result = this.process(input, throwable);

        if (super.isCancelled()) {
            super.uncancelPipeline();

            return Exceptional.of(
                    (K) super.getCancelBehaviour().act(result.orNull(), (Function<Object, Object>) this.converter),
                    result.unsafeError()
            );
        }
        else return (Exceptional<K>) result.map(t -> this.converter.apply(t));
    }

    /**
     * Converts the pipeline to a different type. <b>Note:</b> When you convert a pipeline, this
     * automatically makes it uncancellable and so will throw an {@link IllegalPipelineException} if
     * you try and process an input with any {@link CancellablePipe}s in this pipeline.
     *
     * @param converter
     *         A non-null {@link Function} that takes in an {@code I} input and returns a
     *         converted {@code K} output
     * @param outputClass
     *         A non-null {@link Class} of the type of the new pipeline
     * @param <K>
     *         The type of the new pipeline
     *
     * @return A pipeline of the new type
     */
    public <K> ConvertiblePipeline<P, K> convertPipeline(@NotNull Function<? super I, K> converter, @NotNull Class<K> outputClass) {
        this.converter = converter;

        ConvertiblePipeline<P, K> nextPipeline = new ConvertiblePipeline<>(outputClass);
        nextPipeline.setPreviousPipeline(this);
        this.setNextPipeline(nextPipeline);

        // Set the cancel behaviour of the new pipeline to the current one.
        nextPipeline.setCancelBehaviour(this.getCancelBehaviour(), PipelineDirection.NEITHER);
        return nextPipeline;
    }

    /**
     * Removes the current pipeline by clearing the {@link IPipe}s and any links with previous and
     * next pipelines.
     *
     * @param previousClass
     *         The {@link Class} of the previous pipeline's input
     * @param <K>
     *         The type of the previous pipeline's input
     *
     * @return The previous pipeline. If there are no previous pipelines, then it returns itself
     */
    @SuppressWarnings("unchecked")
    public <K> ConvertiblePipeline<P, K> removePipeline(Class<K> previousClass) {
        super.clearPipes();

        if (null == this.getPreviousPipeline()) {
            this.clearPipelineConnections();
            return (ConvertiblePipeline<P, K>) this;
        }
        else {
            if (Reflect.assignableFrom(previousClass, this.getPreviousPipeline().getInputClass())) {
                ConvertiblePipeline<P, K> previousPipeline =
                        (ConvertiblePipeline<P, K>) this.getPreviousPipeline();
                this.clearPipelineConnections();

                return previousPipeline;
            }
            else {
                throw new IllegalArgumentException(String.format(
                        "Input class was not correct. [Expected: %s, Actual: %s]",
                        this.getPreviousPipeline().getInputClass().getCanonicalName(),
                        previousClass.getCanonicalName())
                );
            }
        }
    }

    /** @return The previous pipeline or null if there isn't one */
    @Nullable
    protected ConvertiblePipeline<P, ?> getPreviousPipeline() {
        return this.previousPipeline;
    }

    /**
     * Clears all the references and converters between this pipeline and the next / previous
     * pipelines.
     */
    protected void clearPipelineConnections() {
        if (null != this.getPreviousPipeline()) {
            this.getPreviousPipeline().setNextPipeline(null);
            this.getPreviousPipeline().converter = null;
            this.setPreviousPipeline(null);
        }

        if (null != this.getNextPipeline()) {
            this.getNextPipeline().setPreviousPipeline(null);
            this.setNextPipeline(null);
            this.converter = null;
        }
    }

    /** @return A {@link Class} of the {@code I} input type for this pipeline */
    public Class<I> getInputClass() {
        return this.inputClass;
    }

    /** @return The next pipeline or null if there isn't one */
    @Nullable
    protected ConvertiblePipeline<P, ?> getNextPipeline() {
        return this.nextPipeline;
    }

    /**
     * Setter function to set the next pipeline.
     *
     * @param nextPipeline
     *         The next pipeline
     */
    protected void setNextPipeline(@Nullable ConvertiblePipeline<P, ?> nextPipeline) {
        this.nextPipeline = nextPipeline;
    }

    /**
     * Setter function to set the previous pipeline.
     *
     * @param previousPipeline
     *         The previous pipeline
     */
    protected void setPreviousPipeline(@Nullable ConvertiblePipeline<P, ?> previousPipeline) {
        this.previousPipeline = previousPipeline;
    }

    /**
     * Propagates the cancel behaviour throughout all the linked pipelines in a particular {@link
     * PipelineDirection}.
     *
     * @param cancelBehaviour
     *         The {@link CancelBehaviour} to be used by the pipeline
     * @param direction
     *         The {@link PipelineDirection} that the cancel behaviour needs to be passed
     *         along
     */
    protected void setCancelBehaviour(CancelBehaviour cancelBehaviour, PipelineDirection direction) {
        super.setCancelBehaviour(cancelBehaviour);

        if ((PipelineDirection.FORWARD == direction || PipelineDirection.BOTH == direction)
                && null != this.getNextPipeline()) {
            this.getNextPipeline().setCancelBehaviour(cancelBehaviour, PipelineDirection.FORWARD);
        }

        if ((PipelineDirection.BACKWARD == direction || PipelineDirection.BOTH == direction)
                && null != this.getPreviousPipeline()) {
            this.getPreviousPipeline().setCancelBehaviour(cancelBehaviour, PipelineDirection.BACKWARD);
        }
    }
}
