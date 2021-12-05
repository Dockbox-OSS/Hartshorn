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

package org.dockbox.hartshorn.core.task.pipeline.pipelines;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.task.pipeline.CancelBehaviour;
import org.dockbox.hartshorn.core.task.pipeline.PipelineDirection;
import org.dockbox.hartshorn.core.task.pipeline.exceptions.IllegalPipelineException;
import org.dockbox.hartshorn.core.task.pipeline.pipes.CancellablePipe;
import org.dockbox.hartshorn.core.task.pipeline.pipes.IPipe;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

public class ConvertiblePipeline<P, I> extends AbstractPipeline<P, I> {

    private final TypeContext<I> inputClass;

    @Nullable
    private ConvertiblePipeline<P, ?> previousPipeline;

    @Nullable
    private ConvertiblePipeline<P, ?> nextPipeline;

    @Nullable
    private Function<? super I, ?> converter;

    /**
     * A private constructor for the pipeline as all convertible pipelines should be instantiated
     * initially as {@link ConvertiblePipelineSource}
     *
     * @param inputClass The {@link Class} of the {@code I} input type
     */
    protected ConvertiblePipeline(final Class<I> inputClass) {
        this.inputClass = TypeContext.of(inputClass);
    }

    /**
     * Internally calls {@link AbstractPipeline#add(IPipe[])} and returns itself.
     *
     * @param pipes The non-null varargs of {@link IPipe pipes} to add to the pipeline
     *
     * @return Itself
     */
    @SafeVarargs
    @Override
    public final ConvertiblePipeline<P, I> add(@NonNull final IPipe<I, I>... pipes) {
        return (ConvertiblePipeline<P, I>) super.add(pipes);
    }

    /**
     * Internally calls {@link AbstractPipeline#add(IPipe)} and returns itself.
     *
     * @param pipe The non-null {@link IPipe} to add to the pipeline
     *
     * @return Itself
     */
    @Override
    public ConvertiblePipeline<P, I> add(@NonNull final IPipe<I, I> pipe) {
        return (ConvertiblePipeline<P, I>) super.add(pipe);
    }

    /**
     * Internally calls {@link AbstractPipeline#add(AbstractPipeline)} and returns itself.
     *
     * @param pipeline The non-null {@link AbstractPipeline} of which the {@link IPipe pipes} should be added to this pipeline
     *
     * @return Itself
     */
    @Override
    public ConvertiblePipeline<P, I> add(@NonNull final AbstractPipeline<?, I> pipeline) {
        return (ConvertiblePipeline<P, I>) super.add(pipeline);
    }

    /**
     * Processes an {@link Exceptional input} by calling {@link AbstractPipeline#processPipe(IPipe,
     * Exceptional)} on each {@link IPipe} in the pipeline and then returns the output wrapped in an
     * {@link Exceptional}.
     *
     * @param exceptionalInput A non-null {@link Exceptional} which contains the input value and throwable
     *
     * @return An {@link Exceptional} containing the output
     */
    @Override
    protected Exceptional<I> process(@NonNull Exceptional<I> exceptionalInput) {
        for (final IPipe<I, I> pipe : this.pipes()) {
            // If the pipelines been cancelled, stop processing any further pipes.
            if (super.cancelled()) {
                // Only permit the pipeline straight away if there's no pipeline after this one
                // as it will be checked again in the processConverted method.
                if (null == this.next()) super.permit();
                return exceptionalInput;
            }

            exceptionalInput = super.processPipe(pipe, exceptionalInput);
        }
        return exceptionalInput;
    }

    /**
     * Sets the cancel behaviour to be used by the entire pipeline by internally calling {@link
     * ConvertiblePipeline#cancelBehaviour(CancelBehaviour, PipelineDirection)}.
     *
     * @param cancelBehaviour A {@link CancelBehaviour} describing the cancellability of the pipeline
     *
     * @return Itself
     */
    @Override
    public ConvertiblePipeline<P, I> cancelBehaviour(final CancelBehaviour cancelBehaviour) {
        this.cancelBehaviour(cancelBehaviour, PipelineDirection.BOTH);
        return this;
    }

    /**
     * Processes an input by recursively calling the process method of the previous pipeline. This
     * will eventually call the {@link ConvertiblePipelineSource#process(Object, Throwable)}, which
     * will stop the recursive calling of previous pipelines. If somehow, a convertible pipeline is
     * used as the source of the pipeline, then it will attempt to cast the {@code P} input to {@code
     * I}.
     *
     * @param input The non-null input value
     * @param throwable The nullable input {@link Throwable}
     *
     * @return An {@link Exceptional} containing the {@code I} output. If the output is not present it will contain a throwable describing why
     */
    @Override
    public Exceptional<I> process(@NonNull final P input, @Nullable final Throwable throwable) {
        final Exceptional<I> exceptionalInput;

        // This should never be called, unless this class was used initially instead of
        // ConvertiblePipelineSource.
        // (Which shouldn't be possible due to the protected constructor).
        if (null == this.previous()) {
            throw new IllegalPipelineException("Pipeline wasn't constructed using ConvertiblePipelineSource.");
        }
        else {
            exceptionalInput = this.previous().processConverted(input, throwable);
        }

        return this.process(exceptionalInput);
    }

    /** Cancels the pipeline and all the following pipelines. */
    @Override
    public void cancel() {
        super.cancel();

        if (null != this.next()) {
            this.next().cancel();
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
        if (null != this.previous()) size += this.previous().size();
        if (null != this.converter) size++;

        return size;
    }

    /**
     * Takes an input and internally processes it by calling {@link
     * ConvertiblePipeline#process(Object, Throwable)} and then converting the output using the
     * converter provided in {@link ConvertiblePipeline#convertPipeline(Function, Class)}.
     *
     * @param input The non-null {@code P} input to be processed by the pipeline
     * @param throwable A nullable {@link Throwable} that may have been thrown while processing the input
     * @param <K> The type of the next pipeline
     *
     * @return An {@link Exceptional} of type {@code K} containing the converted output
     */
    protected <K> Exceptional<K> processConverted(@NonNull final P input, @Nullable final Throwable throwable) {
        final Exceptional<I> result = this.process(input, throwable);

        if (super.cancelled()) {
            super.permit();

            return Exceptional.of(
                    (K) super.cancelBehaviour().act(result.orNull(), (Function<Object, Object>) this.converter),
                    result.unsafeError()
            );
        }
        else return (Exceptional<K>) result.map(t -> this.converter.apply(t));
    }

    /**
     * Converts the pipeline to a different type. <b>Note:</b> When you convert a pipeline, this
     * automatically makes it non-cancellable and so will throw an {@link IllegalPipelineException} if
     * you try and process an input with any {@link CancellablePipe}s in this pipeline.
     *
     * @param converter A non-null {@link Function} that takes in an {@code I} input and returns a converted {@code K} output
     * @param outputClass A non-null {@link Class} of the type of the new pipeline
     * @param <K> The type of the new pipeline
     *
     * @return A pipeline of the new type
     */
    public <K> ConvertiblePipeline<P, K> convertPipeline(@NonNull final Function<? super I, K> converter, @NonNull final Class<K> outputClass) {
        this.converter = converter;

        final ConvertiblePipeline<P, K> nextPipeline = new ConvertiblePipeline<>(outputClass);
        nextPipeline.previous(this);
        this.next(nextPipeline);

        // Set the cancel behaviour of the new pipeline to the current one.
        nextPipeline.cancelBehaviour(this.cancelBehaviour(), PipelineDirection.NEITHER);
        return nextPipeline;
    }

    /**
     * Removes the current pipeline by clearing the {@link IPipe pipes} and any links with previous and
     * next pipelines.
     *
     * @param previousClass The {@link Class} of the previous pipeline's input
     * @param <K> The type of the previous pipeline's input
     *
     * @return The previous pipeline. If there are no previous pipelines, then it returns itself
     */
    public <K> ConvertiblePipeline<P, K> remove(final Class<K> previousClass) {
        super.clear();

        if (null == this.previous()) {
            this.clearPipelineConnections();
            return (ConvertiblePipeline<P, K>) this;
        }
        else {
            if (this.previous().input().childOf(previousClass)) {
                final ConvertiblePipeline<P, K> previousPipeline =
                        (ConvertiblePipeline<P, K>) this.previous();
                this.clearPipelineConnections();

                return previousPipeline;
            }
            else {
                throw new IllegalArgumentException(String.format(
                        "Input class was not correct. [Expected: %s, Actual: %s]",
                        this.previous().input().qualifiedName(),
                        previousClass.getCanonicalName())
                );
            }
        }
    }

    /** @return The previous pipeline or null if there isn't one */
    @Nullable
    protected ConvertiblePipeline<P, ?> previous() {
        return this.previousPipeline;
    }

    /**
     * Clears all the references and converters between this pipeline and the next / previous
     * pipelines.
     */
    protected void clearPipelineConnections() {
        if (null != this.previous()) {
            this.previous().next(null);
            this.previous().converter = null;
            this.previous(null);
        }

        if (null != this.next()) {
            this.next().previous(null);
            this.next(null);
            this.converter = null;
        }
    }

    /** @return A {@link Class} of the {@code I} input type for this pipeline */
    public TypeContext<I> input() {
        return this.inputClass;
    }

    /**
     * Setter function to set the next pipeline.
     *
     * @param nextPipeline The next pipeline
     */
    protected void next(@Nullable final ConvertiblePipeline<P, ?> nextPipeline) {
        this.nextPipeline = nextPipeline;
    }

    /**
     * Setter function to set the previous pipeline.
     *
     * @param previousPipeline The previous pipeline
     */
    protected void previous(@Nullable final ConvertiblePipeline<P, ?> previousPipeline) {
        this.previousPipeline = previousPipeline;
    }

    /** @return The next pipeline or null if there isn't one */
    @Nullable
    protected ConvertiblePipeline<P, ?> next() {
        return this.nextPipeline;
    }

    /**
     * Propagates the cancel behaviour throughout all the linked pipelines in a particular {@link
     * PipelineDirection}.
     *
     * @param cancelBehaviour The {@link CancelBehaviour} to be used by the pipeline
     * @param direction The {@link PipelineDirection} that the cancel behaviour needs to be passed along
     */
    protected void cancelBehaviour(final CancelBehaviour cancelBehaviour, final PipelineDirection direction) {
        super.cancelBehaviour(cancelBehaviour);

        if ((PipelineDirection.FORWARD == direction || PipelineDirection.BOTH == direction)
                && null != this.next()) {
            this.next().cancelBehaviour(cancelBehaviour, PipelineDirection.FORWARD);
        }

        if ((PipelineDirection.BACKWARD == direction || PipelineDirection.BOTH == direction)
                && null != this.previous()) {
            this.previous().cancelBehaviour(cancelBehaviour, PipelineDirection.BACKWARD);
        }
    }
}
