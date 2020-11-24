package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.pipeline.exceptions.CancelledPipelineException;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineConverterException;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ConvertiblePipeline<P, I> extends AbstractPipeline<P, I> {

    protected ConvertiblePipeline() { }

    private ConvertiblePipeline<P, ?> previousPipeline;
    private ConvertiblePipeline<P, ?> nextPipeline;

    private Function<? super I, ?> converter;

    @Override
    public ConvertiblePipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        return (ConvertiblePipeline<P, I>)super.addPipe(pipe);
    }

    @SafeVarargs
    @Override
    public final ConvertiblePipeline<P, I> addPipes(@NotNull IPipe<I, I>... pipes) {
        return (ConvertiblePipeline<P, I>)super.addPipes(pipes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Exceptional<I> process(@NotNull P input, @Nullable Throwable throwable) {

        Exceptional<I> exceptionalInput;
        if (null != this.previousPipeline) {
            exceptionalInput = this.previousPipeline.processConverted(input, throwable);
        }
        else {
            exceptionalInput = Exceptional.ofNullable((I)input, throwable);
        }

        for (IPipe<I, I> pipe : this.pipes) {
            exceptionalInput = super.processPipe(pipe, exceptionalInput);

            //If the pipelines been cancelled, stop processing any further pipes.
            if (exceptionalInput.errorPresent() &&
                exceptionalInput.getError().getClass().isAssignableFrom(CancelledPipelineException.class))
                break;
        }

        return exceptionalInput;
    }

    @Override
    public void setCancellable(boolean isCancellable) {
        //Only allow this pipeline to be cancellable if theres not a pipeline after this.
        this.isCancellable = null == this.nextPipeline && isCancellable;
    }

    @SuppressWarnings("unchecked")
    private <K> Exceptional<K> processConverted(@NotNull P input, @Nullable Throwable throwable) throws IllegalPipelineConverterException {
        Exceptional<I> output = this.process(input, throwable);
        return (Exceptional<K>) output.map(this.converter);

    }

    public <K> ConvertiblePipeline<P, K> convertPipeline(Function<? super I, ? extends K> converter) {
        this.converter = converter;

        ConvertiblePipeline<P, K> nextPipeline = new ConvertiblePipeline<>();
        nextPipeline.previousPipeline = this;
        this.nextPipeline = nextPipeline;

        this.setCancellable(false);

        return nextPipeline;
    }

    @Override
    public int pipelineSize() {
        int size = this.pipes.size();
        if (null != this.previousPipeline) size += this.previousPipeline.pipelineSize();
        if (null != this.converter) size++;

        return size;
    }
}
