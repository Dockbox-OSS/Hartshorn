package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.pipeline.exceptions.CancelledPipelineException;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineConverterException;
import org.dockbox.selene.integrated.data.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPipeline<P, I> {

    protected List<IPipe<I, I>> pipes = new ArrayList<>();
    protected boolean isCancellable, isCancelled;

    public AbstractPipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        if (!this.isCancellable && pipe.getType().isAssignableFrom(CancellablePipe.class)) {
            throw new IllegalArgumentException("Attempted to add a CancellablePipe to an uncancellable pipeline.");
        }

        this.pipes.add(pipe);
        return this;
    }

    public AbstractPipeline<P, I> addPipes(@NotNull IPipe<I, I>[] pipes) {
        for (IPipe<I, I> pipe : pipes) {
            this.addPipe(pipe);
        }
        return this;
    }

    public abstract Exceptional<I> process (@NotNull P input, @Nullable Throwable throwable);

    protected Exceptional<I> processPipe(IPipe<I, I> pipe, Exceptional<I> input) {
        Exceptional<I> output = Exceptional.ofSupplier(() -> {
            //Check if the pipelines an instance of the Cancellable pipeline or not.
            if (pipe instanceof CancellablePipe) {
                CancellablePipe<I, I> cancellablePipe = (CancellablePipe<I, I>) pipe;
                return cancellablePipe.execute(this::cancelPipeline, input.get(), input.getError());
            }
            else {
                return pipe.apply(null, input.get(), input.getError());
            }
        });

        if (this.isCancellable && this.isCancelled) {
            //Reset it straight after its been detected for next time the pipeline's used.
            this.isCancelled = false;
            output = Exceptional.of(new CancelledPipelineException("Pipeline has been cancelled."));
        }

        return output;
    }

    public I processUnsafely(@NotNull P input) {
        return this.process(input, null).get();
    }

    public Exceptional<I> process(@NotNull P input) {
        return this.process(input, null);
    }

    protected void cancelPipeline() {
        this.isCancelled = true;
    }

    public void setCancellable(boolean isCancellable) {
        this.isCancellable = isCancellable;
    }

    public void removePipeAt(int index) {
        this.pipes.remove(index);
    }

    public int pipelineSize() {
        return this.pipes.size();
    }

    public List<IPipe<I, I>> getPipes() {
        return this.pipes;
    }

    public boolean isCancellable() {
        return this.isCancellable;
    }
}
