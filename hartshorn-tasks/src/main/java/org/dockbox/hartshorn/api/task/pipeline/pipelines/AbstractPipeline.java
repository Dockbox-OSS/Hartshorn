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
import org.dockbox.hartshorn.api.task.pipeline.exceptions.IllegalPipeException;
import org.dockbox.hartshorn.api.task.pipeline.pipes.CancellablePipe;
import org.dockbox.hartshorn.api.task.pipeline.pipes.ComplexPipe;
import org.dockbox.hartshorn.api.task.pipeline.pipes.IPipe;
import org.dockbox.hartshorn.api.task.pipeline.pipes.StandardPipe;
import org.dockbox.hartshorn.util.Reflect;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPipeline<P, I> {

    private final List<IPipe<I, I>> pipes = HartshornUtils.emptyList();
    private boolean isCancelled;
    private CancelBehaviour cancelBehaviour = CancelBehaviour.UNCANCELLABLE;

    /**
     * Internally calls {@link AbstractPipeline#addPipes(IPipe[])} and returns itself.
     *
     * @param pipes
     *         The non-null varargs of {@link IPipe}s to add to the pipeline
     *
     * @return Itself
     */
    @SafeVarargs
    public final AbstractPipeline<P, I> addVarargPipes(@NotNull IPipe<I, I>... pipes) {
        return this.addPipes(pipes);
    }

    /**
     * Adds a non-null array of {@link IPipe}s to the pipeline by internally calling {@link
     * AbstractPipeline#addPipes(Iterable)}.
     *
     * @param pipes
     *         The non-null array of {@link IPipe}s to add to the pipeline
     *
     * @return Itself
     */
    public AbstractPipeline<P, I> addPipes(@NotNull IPipe<I, I>[] pipes) {
        return this.addPipes(Arrays.asList(pipes));
    }

    /**
     * Adds a non-null {@link Iterable} of {@link IPipe}s to the pipeline by internally calling {@link
     * AbstractPipeline#addPipe} on each pipe.
     *
     * @param pipes
     *         The non-null {@link Iterable} of {@link IPipe}s to add to the pipeline
     *
     * @return Itself
     */
    public AbstractPipeline<P, I> addPipes(@NotNull Iterable<IPipe<I, I>> pipes) {
        for (IPipe<I, I> pipe : pipes) {
            this.addPipe(pipe);
        }
        return this;
    }

    /**
     * Add a non-null {@link IPipe} to the pipeline.
     *
     * @param pipe
     *         The non-null {@link IPipe} to add to the pipeline
     *
     * @return Itself
     */
    public AbstractPipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        this.pipes.add(pipe);
        return this;
    }

    /**
     * Adds a {@link AbstractPipeline}'s {@link IPipe}s to this current pipeline by internally calling
     * {@link AbstractPipeline#addPipes(Iterable)}.
     *
     * @param pipeline
     *         The non-null {@link AbstractPipeline} whos {@link IPipe}s should be added to
     *         this pipeline
     *
     * @return Itself
     */
    public AbstractPipeline<P, I> addPipeline(@NotNull AbstractPipeline<?, I> pipeline) {
        return this.addPipes(pipeline.getPipes());
    }

    /** @return An unmodifiabe list of the {@link IPipe}s in the pipeline */
    public List<IPipe<I, I>> getPipes() {
        return HartshornUtils.asUnmodifiableList(this.pipes);
    }

    /**
     * An abstract method which defines how an {@link Exceptional input} should be processed.
     *
     * @param exceptionalInput
     *         A non-null {@link Exceptional} which contains the input value and
     *         throwable
     *
     * @return An {@link Exceptional} containing the output value after it has been processed by the
     *         pipeline
     */
    protected abstract Exceptional<I> process(@NotNull Exceptional<I> exceptionalInput);

    /**
     * A default method for processing a pipe, which handles converting the pipe, checking that
     * they're not illegal {@link CancellablePipe}s and passing forward the previous input if the pipe
     * throws an caught.
     *
     * @param pipe
     *         The {@link IPipe} to be processed
     * @param exceptionalInput
     *         An {@link Exceptional} containing the input to be processed by the
     *         {@link IPipe}
     *
     * @return An {@link Exceptional} of the output of the {@link IPipe}
     * @throws IllegalPipeException
     *         If you try and add a {@link CancellablePipe} and the pipeline is
     *         not cancellable
     */
    protected Exceptional<I> processPipe(IPipe<I, I> pipe, Exceptional<I> exceptionalInput) {
        if (!this.isCancellable() && Reflect.assignableFrom(CancellablePipe.class, pipe.getType())) {
            throw new IllegalPipeException(
                    "Attempted to add a CancellablePipe to an uncancellable pipeline.");
        }

        // Create a temporary final version that can be used within the supplier.
        final Exceptional<I> finalInput = exceptionalInput;

        exceptionalInput = Exceptional.of(() -> {
            if (Reflect.assignableFrom(ComplexPipe.class, pipe.getType())) {
                ComplexPipe<I, I> complexPipe = (ComplexPipe<I, I>) pipe;
                return complexPipe.apply(
                        this, finalInput.orNull(), finalInput.unsafeError());
            }
            else if (Reflect.assignableFrom(StandardPipe.class, pipe.getType())) {
                StandardPipe<I, I> standardPipe = (StandardPipe<I, I>) pipe;
                return standardPipe.apply(finalInput);
            }
            else {
                return finalInput.orNull();
            }
        });

        // If there was an caught, the supplier won't have captured any input, so we'll try and
        // pass the previous input forwards.
        if (exceptionalInput.caught()) {
            exceptionalInput = Exceptional.of(finalInput.orNull(), exceptionalInput.error());
        }
        return exceptionalInput;
    }

    /** @return A boolean describing if the pipeline is cancellable or not */
    public boolean isCancellable() {
        return CancelBehaviour.UNCANCELLABLE != this.getCancelBehaviour();
    }

    /** @return The {@link CancelBehaviour} of this pipeline */
    public CancelBehaviour getCancelBehaviour() {
        return this.cancelBehaviour;
    }

    /**
     * Sets how the pipeline responds if cancelled by an {@link CancellablePipe} or any child pipes of
     * it.
     *
     * @param cancelBehaviour
     *         A {@link CancelBehaviour} describing the cancellability of the pipeline
     *
     * @return Itself
     */
    public AbstractPipeline<P, I> setCancelBehaviour(CancelBehaviour cancelBehaviour) {
        this.cancelBehaviour = cancelBehaviour;
        return this;
    }

    /**
     * Unsafely processes an {@code P} input by calling {@link AbstractPipeline#process(Object,
     * Throwable)} and unwrapping the output value without checking if its present.
     *
     * @param input
     *         The non-null {@code P} input to be processed by the pipeline
     *
     * @return The {@code I} output of processing the input, unwrapped from the {@link Exceptional}
     *         without checking if its present
     */
    public I processUnsafe(@NotNull P input) {
        return this.process(input).orNull();
    }

    /**
     * Processes an {@code P} input by internally calling {@link AbstractPipeline#process(Object,
     * Throwable)}.
     *
     * @param input
     *         The non-null {@code P} input to be processed by the pipeline
     *
     * @return An {@link Exceptional} containing the {@code I} output. If the output is not present it
     *         will contain a throwable describing why
     */
    public Exceptional<I> process(P input) {
        return this.process(input, null);
    }

    /**
     * An abstract method which defines how an {@code P} input and a {@link Throwable} should be
     * passed to {@link AbstractPipeline#process(Exceptional)}.
     *
     * @param input
     *         The non-null input value
     * @param throwable
     *         The nullable input {@link Throwable}
     *
     * @return An {@link Exceptional} of the output
     */
    public abstract Exceptional<I> process(@NotNull P input, @Nullable Throwable throwable);

    /**
     * Processes a {@link Collection} of {@code P} inputs by internally calling {@link
     * AbstractPipeline#process(Object)} on each input in the {@link Collection} and returns the
     * result as a {@link List} of {@link Exceptional}.
     *
     * @param inputs
     *         The non-null {@link Collection} of {@code P} inputs to be processed by the
     *         pipeline
     *
     * @return A {@link List} of {@link Exceptional} containing the processed {@code I} output of each
     *         input
     */
    public List<Exceptional<I>> processAll(@NotNull Collection<P> inputs) {
        return inputs.stream().map(this::process).collect(Collectors.toList());
    }

    /**
     * Processes a {@link Collection} of {@code P} inputs by internally calling {@link
     * AbstractPipeline#process(Object)} on each input in the {@link Collection} and returns the
     * non-null results as a {@link List} of {@code I} outputs.
     *
     * @param inputs
     *         The non-null {@link Collection} of {@code P} inputs to be processed by the
     *         pipeline
     *
     * @return A {@link List} containing the processed {@code I} output of each input, if not null
     */
    public List<I> processAllSafe(@NotNull Collection<P> inputs) {
        return inputs.stream()
                .map(this::process)
                .filter(Exceptional::present)
                .map(Exceptional::get)
                .collect(Collectors.toList());
    }

    /**
     * Processes a {@link Collection} of {@code P} inputs by internally calling {@link
     * AbstractPipeline#process(Object)} on each input in the {@link Collection} and returns the
     * result as a {@link List} of {@code I} outputs, including null values.
     *
     * @param inputs
     *         The non-null {@link Collection} of {@code P} inputs to be processed by the
     *         pipeline
     *
     * @return A {@link List} containing the processed {@code I} output of each input, even if its
     *         null
     */
    public List<I> processAllUnsafe(@NotNull Collection<P> inputs) {
        return inputs.stream().map(this::process).map(Exceptional::orNull).collect(Collectors.toList());
    }

    /** Cancels the pipeline which prevents it from processing any further {@link IPipe}s. */
    public void cancelPipeline() {
        if (this.isCancellable()) this.isCancelled = true;
    }

    /** Uncancels the pipeline. */
    public void uncancelPipeline() {
        this.isCancelled = false;
    }

    /** @return If the pipeline is cancelled */
    protected boolean isCancelled() {
        return this.isCancellable() && this.isCancelled;
    }

    /**
     * Removes the last {@link IPipe} from the pipeline. If there are no {@link IPipe}s in the
     * pipeline, this does nothing.
     */
    public void removeLastPipe() {
        this.removePipeAt(this.size() - 1);
    }

    /**
     * Removes a {@link IPipe} from the pipeline at the specified index. If the specified index is out
     * of bounds, nothing will be removed.
     *
     * @param index
     *         The index of the {@link IPipe} to remove
     */
    public void removePipeAt(int index) {
        if (this.pipes.size() > index) {
            this.pipes.remove(index);
        }
    }

    /** @return The number of {@link IPipe}s in the pipeline */
    public int size() {
        return this.pipes.size();
    }

    /** Removes all the {@link IPipe}s in the pipeline. */
    public void clearPipes() {
        this.pipes.clear();
    }
}