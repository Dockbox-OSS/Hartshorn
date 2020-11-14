package org.dockbox.selene.integrated.data.pipeline;


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

    public Pipeline<?, ?> of(IPipe<?, ?>... pipes) {
        if (0 != pipes.length) {
            Pipeline lastPipeline = this;

            if (null == this.currentPipe) this.currentPipe = (IPipe<I, O>) pipes[0];

            for (IPipe<?, ?> pipe : pipes) {
                lastPipeline = lastPipeline.addPipe(pipe);
            }
            return lastPipeline;
        }
        return this;
    }

    public <K> Pipeline<O, K> addPipe(IPipe<O, K> nextPipe) {
        Pipeline<O, K> nextPipeline = new Pipeline<>();
        nextPipeline.of(nextPipe);

        nextPipeline.previousPipeline = this;
        this.nextPipeline = nextPipeline;
        return nextPipeline;
    }

    public Pipeline<?, ?> addPipeline(Pipeline<O, ?> nextPipeline) {
        nextPipeline.previousPipeline = this;
        this.nextPipeline = nextPipeline;

        // Its possible that the next pipeline has subsequent pipes. We want to return the last pipeline
        // so that further #addPipe / #addPipeline calls don't overwrite any existing pipeline.
        return this.lastPipeline();
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
        if (this.isCancelled) {
            if (null != this.nextPipeline) {
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
        Pipeline firstPipeline = this.firstPipeline();

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

    public Pipeline<?, ?> firstPipeline() {
        Pipeline<?, ?> firstPipeline = this;

        while (null != firstPipeline.previousPipeline) {
            firstPipeline = firstPipeline.previousPipeline;
        }
        return firstPipeline;
    }

    public Pipeline<?, ?> lastPipeline() {
        Pipeline<?, ?> lastPipeline = this;

        while (null != lastPipeline.nextPipeline) {
            lastPipeline = lastPipeline.nextPipeline;
        }
        return lastPipeline;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    private void pipelineCancelled(Object finalOutput) {
        this.output = (O)finalOutput;
        if (null != this.nextPipeline) {
            this.nextPipeline.pipelineCancelled(finalOutput);
        }
    }
}
