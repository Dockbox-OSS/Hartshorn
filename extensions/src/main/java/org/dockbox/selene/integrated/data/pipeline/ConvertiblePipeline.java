package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipeException;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineConverterException;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ConvertiblePipeline<P, I> extends AbstractPipeline<P, I> {

    private ConvertiblePipeline<P, ?> previousPipeline;
    private ConvertiblePipeline<P, ?> nextPipeline;

    private Function<? super I, ?> converter;

    protected ConvertiblePipeline() { }

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
            //As this is
            exceptionalInput = Exceptional.ofNullable((I)input, throwable);
        }

        return super.process(exceptionalInput);
    }

    @Override
    public ConvertiblePipeline<P, I> setCancellable(boolean isCancellable) {
        //Only allow this pipeline to be cancellable if theres not a pipeline after this.
        this.isCancellable = null == this.nextPipeline && isCancellable;
        return this;
    }

    @SuppressWarnings("unchecked")
    private <K> Exceptional<K> processConverted(@NotNull P input, @Nullable Throwable throwable) throws IllegalPipeException {
        Exceptional<I> result = this.process(input, throwable);
        Exceptional<K> output = (Exceptional<K>) result.map(this.converter);

        //If the mapper returns null (If it wasn't already null)
        if (result.isPresent() && !output.isPresent()) {
            throw new IllegalPipelineConverterException(
                String.format("The pipeline converter returned null. [Input: %s, Output: %s]",
                    result, output));
        }
        return output;
    }

    public <K> ConvertiblePipeline<P, K> convertPipeline(Function<? super I, ? extends K> converter) {
        this.converter = converter;

        ConvertiblePipeline<P, K> nextPipeline = new ConvertiblePipeline<>();
        nextPipeline.previousPipeline = this;
        this.nextPipeline = nextPipeline;

        //If the current pipeline is cancellable, make the next pipeline cancellable.
        nextPipeline.setCancellable(this.isCancellable);
        //As it is no longer the final pipeline, this is no longer cancellable.
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
