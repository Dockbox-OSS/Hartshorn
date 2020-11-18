package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unchecked")
/*
 * The Pipeline class is designed around a Doubly Linked List data structure.
 */
public class Pipeline<I, O> {

    private IPipe<I, O> currentPipe;

    private Pipeline<?, I> previousPipeline;
    private Pipeline<O, ?> nextPipeline;

    private boolean isCancelled;
    private O output;

    public Pipeline<I, O> of(IPipe<I, O> pipe) {
        this.currentPipe = pipe;
        return this;
    }

    public <T, R> Pipeline<T, R> of(@NotNull List<IPipe<?, ?>> pipes) {
        if (!pipes.isEmpty()) {
            Pipeline lastPipeline = this;

            if (null == this.currentPipe) this.currentPipe = (IPipe<I, O>) pipes.get(0);

            for (IPipe<?, ?> pipe : pipes) {
                lastPipeline = lastPipeline.addPipe(pipe);
            }
            return lastPipeline;
        }
        return (Pipeline<T, R>)this;
    }

    public <K> Pipeline<O, K> addPipe(IPipe<O, K> nextPipe) {
        Pipeline<O, K> nextPipeline = new Pipeline<>();
        nextPipeline.of(nextPipe);

        nextPipeline.previousPipeline = this;
        this.nextPipeline = nextPipeline;
        return nextPipeline;
    }

    public Pipeline<?, ?> addPipeline(@NotNull Pipeline<O, ?> nextPipeline) {
        nextPipeline.previousPipeline = this;
        this.nextPipeline = nextPipeline;

        // Its possible that the next pipeline has subsequent pipes. We want to return the last pipeline
        // so that further #addPipe / #addPipeline calls don't overwrite any existing pipeline.
        return this.getLastPipeline();
    }

    private void execute(I input, Throwable throwable) {
        Throwable error = null;
        try {
            this.output = this.currentPipe.apply(this, input, throwable);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                String.format("Couldn't pass forward the input %s to pipe: %s",
                    input, this.currentPipe.pipeName()), e);
        } catch (Throwable t) {
            error = t;
            // Try and pass the original input forwards.
            this.output = (O) input;
        }

        // If this pipeline was cancelled by the current pipe, don't continue to execute subsequent pipes.
        if (!this.isCancelled) {
            if (!this.isLast()) {
                this.nextPipeline.execute(this.output, error);
            }
        }
        else {
            //Propogate the final output up the pipeline so that it can be returned.
            this.pipelineCancelled(this.output);
            //Reset the pipeline for next useage.
            this.isCancelled = false;
        }
    }

    public O process(Object input) {
        return this.process(input, null);
    }

    public O process(Object input, Throwable throwable) {
        Pipeline firstPipeline = this.getFirstPipeline();

        try {
            firstPipeline.execute(input, throwable);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                String.format("The input: %s (%s), is of the wrong type.",
                    input, input.getClass()), e);
        }

        // If the pipeline was called to process the input from here, return the output of
        // this pipe in the pipeline, even if its not the last pipe.
        return this.output;
    }

    @NotNull
    public <T, R> Pipeline<T, R> getFirstPipeline() {
        Pipeline<?, ?> firstPipeline = this;

        while (!firstPipeline.isFirst()) {
            firstPipeline = firstPipeline.previousPipeline;
        }
        return (Pipeline<T, R>)firstPipeline;
    }

    @NotNull
    public <T, R> Pipeline<T, R> getLastPipeline() {
        Pipeline<?, ?> lastPipeline = this;

        while (!lastPipeline.isLast()) {
            lastPipeline = lastPipeline.nextPipeline;
        }
        return (Pipeline<T, R>)lastPipeline;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    private void pipelineCancelled(Object finalOutput) {
        this.output = (O)finalOutput;
        if (!this.isLast()) {
            this.nextPipeline.pipelineCancelled(finalOutput);
        }
    }

    //May not be necessary?
    public int size() {
        Pipeline<?, ?> currentPipeline = this.getFirstPipeline();
        int size = 1;

        while (!currentPipeline.isLast()) {
            size++;
            currentPipeline = currentPipeline.nextPipeline;
        }
        return size;
    }

    public Exceptional<Pipeline<?, ?>> getPipelineAt(int index) {
        Pipeline<?, ?> currentPipeline;
        int currentIndex;

        if (0 <= index) {
            currentIndex = 0;
            currentPipeline = this.getFirstPipeline();

            while (index != currentIndex) {
                if (!currentPipeline.isLast()) {
                    currentIndex++;
                    currentPipeline = currentPipeline.nextPipeline;
                }
                else break;
            }
        }
        // Allows you to get a pipeline using a negative index, which works backwards.
        else {
            currentIndex = -1;
            currentPipeline = this.getLastPipeline();

            while (index != currentIndex) {
                if (!currentPipeline.isFirst()) {
                    currentIndex--;
                    currentPipeline = currentPipeline.previousPipeline;
                }
                else break;
            }
        }

        if (index != currentIndex) {
            return Exceptional.of(new IndexOutOfBoundsException(
                String.format("The index: %s, was outside the bounds of %s.", index, currentIndex)
            ));
        }
        return Exceptional.of(currentPipeline);
    }

    public Pipeline<I, O> removePipeAt(int index) {
        this.getPipelineAt(index)
            .ifPresent(this::removePipeline
        );
        return this;
    }

    public Pipeline<?, ?> removePipe() {
        this.removePipeline(this);

        if (!this.isLast()) return this.nextPipeline;
        if (!this.isFirst()) return this.previousPipeline;
        return this;
    }

    public void removeFirstPipeOf(Class<? extends IPipe<?, ?>> pipeType) {
        Pipeline<?, ?> currentPipeline = this.getFirstPipeline();

        do {
            if (currentPipeline.currentPipe.getClass().isAssignableFrom(pipeType)) {
                this.removePipeline(currentPipeline);
                break;
            }

            currentPipeline = currentPipeline.nextPipeline;
        } while (null != currentPipeline);

    }

    public boolean isFirst() {
        return null == this.previousPipeline;
    }

    public boolean isLast() {
        return null == this.nextPipeline;
    }

    @SuppressWarnings("AssignmentToNull")
    private void removePipeline(Pipeline<?, ?> p) {
        if (!this.isFirst()) p.previousPipeline.nextPipeline = (Pipeline)p.nextPipeline;
        else p.nextPipeline.previousPipeline = null;

        if (!this.isLast()) p.nextPipeline.previousPipeline = (Pipeline)p.previousPipeline;
        else p.previousPipeline.nextPipeline = null;

        p.nextPipeline = null;
        p.previousPipeline = null;
    }
}
