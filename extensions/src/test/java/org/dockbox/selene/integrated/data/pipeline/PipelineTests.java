package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineException;
import org.dockbox.selene.integrated.data.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.ExceptionalPipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.InputPipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.Pipe;
import org.junit.Assert;
import org.junit.Test;

public class PipelineTests {

    @Test
    public void genericPipelineTest() {
        int result = new Pipeline<Integer>()
            .addPipe(Pipe.of((input, throwable) -> input + 1))
            .addVarargPipes(
                Pipe.of((input, throwable) -> input * 2),
                Pipe.of(((input, throwable) -> input - 3)))
            .addPipe(ExceptionalPipe.of(input -> input.orElse(-1)))
            .processUnsafe(5);

        Assert.assertEquals(9, result);
    }

    @Test
    public void addingPipelinesTest() {
        AbstractPipeline<String, String> pipeline = new Pipeline<String>()
            .addPipe(Pipe.of((input, throwable) -> "- " + input + " -"));

        String result = new Pipeline<String>()
            .addPipe(Pipe.of((input, throwable) -> input.substring(0, 1).toUpperCase() + input.substring(1)))
            .addPipeline(pipeline)
            .processUnsafe("hi world");

        Assert.assertEquals("- Hi world -", result);
    }

    @Test
    public void passingInputForwardOnErrorTest() {
        int output = new Pipeline<Integer>()
            .addPipe(InputPipe.of(input -> 1 / input))
            .addPipe(ExceptionalPipe.of(input ->  input.orElse(1)))
            .processUnsafe(0);

        Assert.assertEquals(0, output);
    }

    @Test
    public void errorCatchingTest() {
        int output = new Pipeline<Integer>()
            .addPipe(InputPipe.of(input -> 1 / input))
            .addPipe(ExceptionalPipe.of(input -> {
                if (input.errorPresent()) return -1;
                else return input.orElse(1);
            })).processUnsafe(0);

        Assert.assertEquals(-1, output);
    }

    @Test(expected = IllegalPipelineException.class)
    public void uncancellablePipelineTest() {
        new Pipeline<Float>()
            .addPipe(InputPipe.of(input -> input + 1F))
            .addPipe(CancellablePipe.of((cancelPipeline, input, throwable) -> {
                if (2 < input) cancelPipeline.run();
                return input;
            }))
            .addPipe(InputPipe.of(input -> input / 2F))
            .processUnsafe(4F);
    }

    @Test
    public void cancellablePipelineTest() {
        float output = new Pipeline<Float>()
            .setCancellable(true)
            .addPipe(InputPipe.of(input -> input + 1F))
            .addPipe(CancellablePipe.of((cancelPipeline, input, throwable) -> {
                if (2 < input) cancelPipeline.run();
                return input;
            }))
            .addPipe(InputPipe.of(input -> input / 2F))
            .processUnsafe(4F);

        Assert.assertEquals(5, output, 0);
    }

    @Test
    public void removingPipesTest() {
        AbstractPipeline<Integer, Integer> pipeline = new Pipeline<Integer>()
            .addPipe(InputPipe.of(input -> input * 2))
            .addPipe(InputPipe.of(input -> input + 3))
            .addPipe(InputPipe.of(input -> input - 1));

        int output = pipeline.processUnsafe(8);
        Assert.assertEquals(18, output);

        pipeline.removeLastPipe();
        pipeline.removePipeAt(0);
        output = pipeline.processUnsafe(8);

        Assert.assertEquals(11, output);

    }
}
