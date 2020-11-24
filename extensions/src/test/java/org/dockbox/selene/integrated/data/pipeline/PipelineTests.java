package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.integrated.data.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.ExceptionalPipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.NonmodifingPipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.Pipe;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class PipelineTests {

    private Pipeline<Integer, String> getPipeline() {
        return new Pipeline<String, String>().of(
            NonmodifingPipe.of(
                (input, throwable) -> System.out.println(String.format("First pipe found: %s", input)))
        ).addPipe(
            Pipe.of((input, throwable) -> {
                int num = Integer.parseInt(input);
                return num + num;
            })
        ).addPipe(
            ExceptionalPipe.of(exceptional -> {
                exceptional.ifErrorPresent(Throwable::printStackTrace);
                return exceptional.orElseGet(() -> -1);
            })
        ).addPipe(
            CancellablePipe.of((cancelPipeline, input, throwable) -> {
                if (4 > input) {
                    cancelPipeline.run();
                    System.out.println("Cancelled pipeline");
                }
                return input;
            })
        ).addPipe(
            Pipe.of((input, throwable) -> {
                System.out.println(String.format("At the end of the pipeline with: %s and %s",
                    input, throwable));
                return input.toString();
            })
        );
    }

    @Test
    public void cancelPipelineTest() {
        Object output = this.getPipeline().process("1");
        Assert.assertEquals(Integer.class, output.getClass());
        Assert.assertEquals(2, output);
    }

    @Test
    public void addingMultiplePipesTest() {
        String output = (String)this.getPipeline().of(Arrays.asList(
            Pipe.of((input, throwable) -> Integer.parseInt((String) input)),
            NonmodifingPipe.of((input, throwable) -> System.out.println(input.getClass()))
        )).addPipe(
            ExceptionalPipe.of(exceptional -> {
                System.out.println(exceptional);
                exceptional.ifErrorPresent(Throwable::printStackTrace);
                return exceptional.isPresent() ? exceptional.get().toString() : "Empty";
            })
        ).addPipeline(
            this.getPipeline().getFirstPipeline()
        ).process("3").get();

        Assert.assertEquals("12", output);
    }

    @Test
    public void test() {
        Pipeline<Integer, String> pipeline = new Pipeline<Integer, Integer>().of(
            Pipe.of((input, throwable) -> input + 1
        )).addPipe(
            CancellablePipe.of((cancelPipeline, input, throwable) -> {
                if (3 < input)  cancelPipeline.run();
                return input;
            }
        )).addPipe(
            Pipe.of((input, throwable) -> String.valueOf(input)
        ));

        //This works fine
        String output1 = pipeline.process(1).get();

        //This creates a ClassCastException
        String output2 = pipeline.process(4).orElseGet(() -> ":)");
    }

    @Test
    public void fixedPipelineTest() {
        new FixedPipeline<Integer>().addPipe(
            Pipe.of((input, throwable) -> input + 1)
        ).addPipe(
            CancellablePipe.of(
                (cancelPipeline, input, throwable) -> input - 2)
        ).process(4);
    }

    @Test
    public void pipelineInteratorTest() {
        Pipeline<Integer, String> pipeline = this.getPipeline();
        pipeline.process("3");

        int count = 0;

        for (Pipeline<?, ?> pipe : pipeline) {
            count++;
        }

        Assert.assertEquals(5, count);
    }

    @Test
    public void convertablePipelineTest() {
        float output = new ConvertiblePipeline<Integer, Integer>()
            .addPipe(
                Pipe.of((input, throwable) -> input + 2)
            ).convertPipeline(
                integer -> (float)integer
            ).addPipe(
                Pipe.of((input, throwable) -> input / 2F)
            ).processUnsafely(1);

        float output2 = new ConvertiblePipelineSource<Integer>()
            .addPipe(
                Pipe.of((input, throwable) -> input + 2)
            ).convertPipeline(
                integer -> (float)integer
            ).addPipe(
                Pipe.of((input, throwable) -> input / 2F)
            ).processUnsafely(1);

        Assert.assertEquals(1.5F, output, 0.0);
        Assert.assertEquals(1.5F, output2, 0.0);
    }
}
