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

    public AbstractPipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        if (!this.isCancellable() && SeleneUtils.isAssignableFrom(CancellablePipe.class, pipe.getType())) {
            throw new IllegalPipelineException("Attempted to add a CancellablePipe to an uncancellable pipeline.");
        }

        this.pipes.add(pipe);
        return this;
    }

    public AbstractPipeline<P, I> addPipes(@NotNull IPipe<I, I>[] pipes) {
        return this.addPipes(Arrays.asList(pipes));
    }

    public AbstractPipeline<P, I> addPipes(@NotNull Iterable<IPipe<I, I>> pipes) {
        for (IPipe<I, I> pipe : pipes) {
            this.addPipe(pipe);
        }
        return this;
    }

    public AbstractPipeline<P, I> addPipeline(@NotNull AbstractPipeline<?, I> pipeline) {
        return this.addPipes(pipeline.getPipes());
    }

    public abstract Exceptional<I> process (@NotNull P input, @Nullable Throwable throwable);

    protected Exceptional<I> process(@NotNull Exceptional<I> exceptionalInput) {
        for (IPipe<I, I> pipe : this.getPipes()) {

            //This occurs when a pipeline is converted, previously allowed cancellable pipes are now illegal.
            if (pipe instanceof CancellablePipe && !this.isCancellable())
                throw new IllegalPipelineException("Attempted to add a CancellablePipe to an uncancellable pipeline.");

            //Create a temporary final version that can be used within the supplier.
            final Exceptional<I> finalInput = exceptionalInput;

            exceptionalInput = Exceptional.ofSupplier(() -> {
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

    public I processUnsafe(@NotNull P input) {
        return this.process(input, null).get();
    }

    public Exceptional<I> process(@NotNull P input) {
        return this.process(input, null);
    }

    protected void cancelPipeline() {
        if (this.isCancellable) this.isCancelled = true;
    }

    public AbstractPipeline<P, I> setCancellable(boolean isCancellable) {
        this.isCancellable = isCancellable;
        return this;
    }

    public void removePipeAt(int index) {
        this.pipes.remove(index);
    }

    public void removeLastPipe() {
        this.pipes.remove(this.pipes.size() - 1);
    }

    public int size() {
        return this.pipes.size();
    }

    public List<IPipe<I, I>> getPipes() {
        return SeleneUtils.asUnmodifiableList(this.pipes);
    }

    protected void clearPipes() {
        this.pipes.clear();
    }

    public boolean isCancellable() {
        return this.isCancellable;
    }
}
