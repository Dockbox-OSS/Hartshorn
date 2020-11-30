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
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineException;
import org.dockbox.selene.integrated.data.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractPipeline<P, I> {

    private List<IPipe<I, I>> pipes = new ArrayList<>();
    private boolean isCancellable, isCancelled;

    /**
     * Add a non-null {@link IPipe} to the pipeline. If the added pipe is a {@link CancellablePipe}
     * and the pipeline is not cancellable, then it will throw an {@link IllegalPipelineException}.
     * @param pipe The {@link IPipe} to add to the pipeline.
     * @return Itself.
     */
    public AbstractPipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        if (!this.isCancellable() && SeleneUtils.isAssignableFrom(CancellablePipe.class, pipe.getType())) {
            throw new IllegalPipelineException("Attempted to add a CancellablePipe to an uncancellable pipeline.");
        }

        this.pipes.add(pipe);
        return this;
    }

    /**
     * Adds a non-null array of {@link IPipe}s to the pipeline by internally calling {@link AbstractPipeline#addPipes(Iterable)}.
     * @param pipes The array of {@link IPipe}s to add to the pipeline.
     * @return Itself.
     */
    public AbstractPipeline<P, I> addPipes(@NotNull IPipe<I, I>[] pipes) {
        return this.addPipes(Arrays.asList(pipes));
    }

    /**
     * Adds a non-null {@link Iterable} of {@link IPipe}s to the pipeline by internally calling
     * {@link AbstractPipeline#addPipe} on each pipe.
     * @param pipes The {@link Iterable} of {@link IPipe}s to add to the pipeline.
     * @return Itself.
     */
    public AbstractPipeline<P, I> addPipes(@NotNull Iterable<IPipe<I, I>> pipes) {
        for (IPipe<I, I> pipe : pipes) {
            this.addPipe(pipe);
        }
        return this;
    }

    /**
     * Adds a {@link AbstractPipeline}'s {@link IPipe}s to this current pipeline by internally calling {@link AbstractPipeline#addPipes(Iterable)}.
     * @param pipeline The {@link AbstractPipeline} whos {@link IPipe}s should be added to this pipeline.
     * @return Itself.
     */
    public AbstractPipeline<P, I> addPipeline(@NotNull AbstractPipeline<?, I> pipeline) {
        return this.addPipes(pipeline.getPipes());
    }

    /**
     * An abstract method which defines how an {@link P} input and a {@link Throwable} should be processed.
     * @param input The input value.
     * @param throwable The input throwable.
     * @return An {@link Exceptional} of the output.
     */
    public abstract Exceptional<I> process (@NotNull P input, @Nullable Throwable throwable);

    /**
     * A default method which processes an {@link Exceptional} input and returns an {@link Exceptional} output,
     * which is the value after it has been passed through each {@link IPipe} in the pipeline. <b>NOTE:</b> If a
     * {@link IPipe} throws an error while processing the input, the pipeline will try and automatically pass the input
     * value on to the next {@link IPipe} in the pipeline, althong with the error thrown.
     *
     * @param exceptionalInput An {@link Exceptional} which contains the input value and throwable.
     * @return
     * An {@link Exceptional} containing an optional output value after it has been passed through each {@link IPipe} in
     * the pipeline. If the value is not present, the output will contain an {@link Throwable} describing why.
     */
    protected Exceptional<I> process(@NotNull Exceptional<I> exceptionalInput) {
        for (IPipe<I, I> pipe : this.getPipes()) {

            //This occurs when a pipeline is converted, previously allowed cancellable pipes are now illegal.
            if (pipe instanceof CancellablePipe && !this.isCancellable())
                throw new IllegalPipelineException("Attempted to add a CancellablePipe to an uncancellable pipeline.");

            //Create a temporary final version that can be used within the supplier.
            final Exceptional<I> finalInput = exceptionalInput;

            exceptionalInput = Exceptional.of(() -> {
                //Check if the pipelines an instance of the Cancellable pipeline or not.
                if (pipe instanceof CancellablePipe) {
                    CancellablePipe<I, I> cancellablePipe = (CancellablePipe<I, I>) pipe;
                    return cancellablePipe.execute(this::cancelPipeline, finalInput.orElse(null), finalInput.orElseExcept(null));
                }
                else {
                    return pipe.apply(finalInput);
                }
            });

            //If the pipelines been cancelled, stop processing any further pipes.
            if (this.isCancellable() && this.isCancelled) {
                //Reset it straight after its been detected for next time the pipeline's used.
                this.isCancelled = false;
                break;
            }
            //If there was an error, the supplier won't have captured any input, so we'll try and
            //pass the previous input forwards.
            else if (exceptionalInput.errorPresent()){
                exceptionalInput = Exceptional.ofNullable(finalInput.orElse(null), exceptionalInput.getError());
            }
        }

        return exceptionalInput;
    }

    /**
     * Unsafely processes a non-null {@link P} input by calling {@link AbstractPipeline#process(Object, Throwable)} and
     * unwrapping the output value without checking if its present.
     * @param input The {@link P} input to be processed by the pipeline.
     * @return The {@link I} output of processing the input, unwrapped from the {@link Exceptional} without checking if its present.
     */
    public I processUnsafe(@NotNull P input) {
        return this.process(input, null).get();
    }

    /**
     * Processes a non-null {@link P} input by internally calling {@link AbstractPipeline#process(Object, Throwable)}.
     * @param input The {@link P} input to be processed by the pipeline.
     * @return An {@link Exceptional} containing the output. If the output is not present it will contain a throwable describing why.
     */
    public Exceptional<I> process(@NotNull P input) {
        return this.process(input, null);
    }

    /**
     * Cancels the pipeline which prevents it from processing any further {@link IPipe}s.
     */
    protected void cancelPipeline() {
        if (this.isCancellable()) this.isCancelled = true;
    }

    /**
     * Sets if the pipeline can be cancelled by an {@link CancellablePipe} or any child pipes of it.
     * @param isCancellable A boolean describing if the pipeline is cancellable or not.
     * @return Itself.
     */
    public AbstractPipeline<P, I> setCancellable(boolean isCancellable) {
        this.isCancellable = isCancellable;
        return this;
    }

    /**
     * Removes a {@link IPipe} from the pipeline at the specified index.
     * @param index The index of the {@link IPipe} to remove.
     */
    public void removePipeAt(int index) {
        this.pipes.remove(index);
    }

    /**
     * Removes the last {@link IPipe} from the pipeline. If there are no {@link IPipe}s in the pipeline, this does nothing.
     */
    public void removeLastPipe() {
        if (!this.pipes.isEmpty()) {
            this.pipes.remove(this.pipes.size() - 1);
        }
    }

    /**
     * @return The number of {@link IPipe}s in the pipeline.
     */
    public int size() {
        return this.pipes.size();
    }

    /**
     * @return An unmodifiabe list of the {@link IPipe}s in the pipeline.
     */
    public List<IPipe<I, I>> getPipes() {
        return SeleneUtils.asUnmodifiableList(this.pipes);
    }

    /**
     * Removes all the {@link IPipe}s in the pipeline.
     */
    public void clearPipes() {
        this.pipes.clear();
    }

    /**
     * @return A boolean describing if the pipeline is cancellable or not.
     */
    public boolean isCancellable() {
        return this.isCancellable;
    }
}
