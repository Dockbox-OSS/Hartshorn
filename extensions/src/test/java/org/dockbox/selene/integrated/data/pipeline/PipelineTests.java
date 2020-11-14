package org.dockbox.selene.integrated.data.pipeline;

import org.junit.Assert;
import org.junit.Test;

public class PipelineTests {

    @Test
    public void pipelineTest() {
        int output = new Pipeline<String, String>().of(
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
                System.out.println(exceptional);
                return exceptional.orElseGet(() -> -1);
            })
        ).addPipe(
            CancelablePipe.of((cancelPipeline, input, throwable) -> {
                System.out.println(input);
                if (4 > input) {
                    cancelPipeline.run();
                    System.out.println("Cancelled pipeline");
                }
                return input;
            })
        ).addPipe(
            NonmodifingPipe.of((input, throwable) ->
                System.out.println(String.format("At the end of the pipeline with: %s and %s",
                    input, throwable)))
        ).process("1");

        Assert.assertEquals(output, 8);
    }
}
