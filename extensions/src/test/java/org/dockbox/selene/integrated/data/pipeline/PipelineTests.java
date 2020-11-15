package org.dockbox.selene.integrated.data.pipeline;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class PipelineTests {

    private Pipeline<?, String> getPipeline() {
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
            CancelablePipe.of((cancelPipeline, input, throwable) -> {
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
        ).process("3");

        Assert.assertEquals("12", output);
    }
}
