package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipeException;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineConverterException;
import org.dockbox.selene.integrated.data.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.ExceptionalPipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.Pipe;
import org.junit.Assert;
import org.junit.Test;

public class PipelineTests {

    @Test
    public void fixedPipelineTest() {
        new Pipeline<Integer>().addPipe(
            Pipe.of((input, throwable) -> input + 1)
        ).addPipe(
            CancellablePipe.of(
                (cancelPipeline, input, throwable) -> input - 2)
        ).process(4);
    }

    @Test
    public void convertablePipelineTest() {
        new ConvertiblePipelineSource<Integer>()
            .addPipe(
                Pipe.of((input, throwable) -> null)
            ).addPipe(
                ExceptionalPipe.of(exceptional -> {
                    System.out.println(exceptional);
                    return exceptional.get() + 2;
                })
            ).convertPipeline(
                integer -> (float)integer
            ).addPipe(
                Pipe.of((input, throwable) -> input / 2F)
            ).process(1)
            .ifPresent(System.out::println)
            .ifErrorPresent(System.out::println);

        //Assert.assertEquals(1.5F, output2, 0.0);
    }

    @Test
    public void simpleConvertablePipelineTest() {
        float output = new ConvertiblePipelineSource<Integer>()
            .addPipe(
                Pipe.of((input, throwable) -> input * 2)
            ).convertPipeline(
                integer -> (float)integer
            ).addPipe(
                Pipe.of((input, throwable) -> input / 6F)
            ).addPipe(
                Pipe.of((input, throwable) -> input * 2)
            ).processUnsafely(18);

        Assert.assertEquals(12F, output, 0.0);
    }

    @Test(expected = IllegalPipelineConverterException.class)
    public void illegalPipelineConverterTest() {
        new ConvertiblePipelineSource<String>()
            .addPipe(
                Pipe.of((input, throwable) -> input + "ing")
            ).convertPipeline(
                string -> null
            ).process("Look");
    }

    @Test(expected = IllegalPipeException.class)
    public void convertiblePipelineIllegalPipeExceptionTest() {
        new ConvertiblePipelineSource<Integer>()
            .addPipe(
                CancellablePipe.of((cancelPipeline, input, throwable) -> {
                    if (2 < input) cancelPipeline.run();
                    return input;
                })
            ).addPipe(
                Pipe.of((input, throwable) -> input - 3)
            ).process(4);
    }

    @Test(expected = IllegalPipeException.class)
    public void illegalPipeExceptionWhenConvertingPipelineTest() {
        new ConvertiblePipelineSource<Integer>()
            .setCancellable(true)
            .addPipe(
                CancellablePipe.of((cancelPipeline, input, throwable) -> {
                    if (2 < input) cancelPipeline.run();
                    return input;
                })
            ).convertPipeline(Object::toString)
            .addPipe(
                Pipe.of((input, throwable) -> input + " - Test Suffix")
            ).process(1);
    }

    @Test
    public void convertiblePipelineCancellableTest() {
        int output = new ConvertiblePipelineSource<Integer>()
            .setCancellable(true)
            .addPipe(
                Pipe.of((input, throwable) -> input + 1)
            ).addPipe(
                CancellablePipe.of((cancelPipeline, input, throwable) -> {
                    if (2 < input) cancelPipeline.run();
                    return input;
                })
            ).addPipe(
                Pipe.of((input, throwable) -> input + 4)
            ).processUnsafely(3);

        Assert.assertEquals(4, output);
    }
}
