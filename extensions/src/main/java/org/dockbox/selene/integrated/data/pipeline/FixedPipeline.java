package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FixedPipeline<I>{

    private List<IPipe<I, I>> pipes = new ArrayList<>();
    private boolean isCancellable, isCancelled;

    public FixedPipeline<I> addPipe(@NotNull IPipe<I, I> pipe) {
        if (!this.isCancellable && pipe.getType().equals(CancellablePipe.class)) {
            throw new IllegalArgumentException("Attempted to add a CancellablePipe to an uncancellable pipeline.");
        }

        this.pipes.add(pipe);
        return this;
    }

    @SafeVarargs
    public final FixedPipeline<I> addPipes(@NotNull IPipe<I, I>... pipes) {
        for (IPipe<I, I> pipe : pipes) {
            this.addPipe(pipe);
        }
        return this;
    }

    public I processUnsafely(I input) {
        return this.process(input, null).get();
    }

    public Exceptional<I> process(@NotNull I input) {
        return this.process(input, null);
    }

    public Exceptional<I> process (@NotNull I input, @Nullable Throwable throwable) {
        for (IPipe<I, I> pipe : this.pipes) {
            try {
                //Check if the pipelines an instance of the Cancellable pipeline or not.
                if (pipe instanceof CancellablePipe) {
                    CancellablePipe<I, I> cancellablePipe = (CancellablePipe<I, I>)pipe;
                    input = cancellablePipe.execute(this::cancelPipeline, input, throwable);
                }
                else {
                    input = pipe.apply(null, input, throwable);
                }
                throwable = null;
            } catch (Throwable t) {
                throwable = t;
            }

            if (this.isCancellable && this.isCancelled) {
                //Reset it straight after its been detected for next time the pipeline's used.
                this.isCancelled = false;
               break;
            }
        }
        return Exceptional.ofNullable(input, throwable);
    }

    private void cancelPipeline() {
        this.isCancelled = true;
    }

    public FixedPipeline<I> setCancellable(boolean isCancellable) {
        this.isCancellable = isCancellable;
        return this;
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
