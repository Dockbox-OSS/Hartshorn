package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.pipeline.exceptions.CancelledPipelineException;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineConverterException;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ConvertablePipeline<P, I> extends AbstractPipeline<P, I> {

    private ConvertablePipeline<P, ?> previousPipeline;
    private ConvertablePipeline<P, ?> nextPipeline;

    private Function<? super I, ?> converter;

    @Override
    public ConvertablePipeline<P, I> addPipe(@NotNull IPipe<I, I> pipe) {
        return (ConvertablePipeline<P, I>)super.addPipe(pipe);
    }

    @SafeVarargs
    @Override
    public final ConvertablePipeline<P, I> addPipes(@NotNull IPipe<I, I>... pipes) {
        return (ConvertablePipeline<P, I>)super.addPipes(pipes);
    }

    @Override
    public Exceptional<I> process(@NotNull P input, @Nullable Throwable throwable) {
        try {
            Exceptional<I> exceptionalInput = this.previousPipeline.processConverted(input, throwable);

            for (IPipe<I, I> pipe : this.pipes) {
                exceptionalInput = super.processPipe(pipe, exceptionalInput);

                //If the pipelines been cancelled, stop processing any further pipes.
                if (exceptionalInput.errorPresent() &&
                    exceptionalInput.getError().getClass()
                        .isAssignableFrom(CancelledPipelineException.class)) {
                    break;
                }
            }

            return exceptionalInput;

        } catch (ClassCastException | IllegalPipelineConverterException e) {
            e.printStackTrace();
        }

        return Exceptional.empty();
    }

    @Override
    public void setCancellable(boolean isCancellable) {
        //Only allow this pipeline to be cancellable if theres not a pipeline after this.
        this.isCancellable = null == this.nextPipeline && isCancellable;
    }

    @SuppressWarnings("unchecked")
    private <K> Exceptional<K> processConverted(@NotNull P input, @Nullable Throwable throwable) throws IllegalPipelineConverterException {
        Exceptional<I> output = this.process(input, throwable);

        try {
            return (Exceptional<K>) output.map(this.converter);
        } catch (ClassCastException e) {
            throw new IllegalPipelineConverterException(
                "Converter didn't convert the input to the correct output type.", e);
        }
    }

    public <K> ConvertablePipeline<P, K> convertPipeline(Function<? super I, ? extends K> converter) {
        this.converter = converter;

        ConvertablePipeline<P, K> nextPipeline = new ConvertablePipeline<>();
        nextPipeline.previousPipeline = this;
        this.nextPipeline = nextPipeline;

        this.setCancellable(false);

        return nextPipeline;
    }
}
