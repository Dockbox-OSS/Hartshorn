package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.integrated.data.pipeline.exceptions.CancelledPipelineException;
import org.dockbox.selene.integrated.data.pipeline.pipes.IPipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConvertiblePipelineSource<I> extends ConvertiblePipeline<I, I> {

    @Override
    public Exceptional<I> process(@NotNull I input, @Nullable Throwable throwable) {
        Exceptional<I> exceptionalInput = Exceptional.ofNullable(input, throwable);

        for (IPipe<I, I> pipe : this.pipes) {
            exceptionalInput = super.processPipe(pipe, exceptionalInput);

            //If the pipelines been cancelled, stop processing any further pipes.
            if (exceptionalInput.errorPresent() &&
                exceptionalInput.getError().getClass().isAssignableFrom(CancelledPipelineException.class))
                break;
        }

        return exceptionalInput;
    }
}
