/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.api.task.pipeline;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.task.pipeline.exceptions.IllegalPipeException;
import org.dockbox.selene.api.task.pipeline.pipelines.AbstractPipeline;
import org.dockbox.selene.api.task.pipeline.pipelines.Pipeline;
import org.dockbox.selene.api.task.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.api.task.pipeline.pipes.InputPipe;
import org.dockbox.selene.api.task.pipeline.pipes.Pipe;
import org.dockbox.selene.api.task.pipeline.pipes.StandardPipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class PipelineTests {

    @Test
    public void genericPipelineTest() {
        int result = new Pipeline<Integer>()
                .addPipe(Pipe.of((input, throwable) -> input + 1))
                .addVarargPipes(
                        Pipe.of((input, throwable) -> input * 2),
                        Pipe.of(((input, throwable) -> input - 3)))
                .addPipe(StandardPipe.of(input -> input.or(-1)))
                .processUnsafe(5);

        Assertions.assertEquals(9, result);
    }

    @Test
    public void addingPipelinesTest() {
        AbstractPipeline<String, String> pipeline = new Pipeline<String>().addPipe(Pipe.of((input, throwable) -> "- " + input + " -"));

        String result = new Pipeline<String>()
                .addPipe(Pipe.of((input, throwable) -> input.substring(0, 1).toUpperCase() + input.substring(1)))
                .addPipeline(pipeline)
                .processUnsafe("hi world");

        Assertions.assertEquals("- Hi world -", result);
    }

    @Test
    public void passingInputForwardOnErrorTest() {
        int output = new Pipeline<Integer>()
                .addPipe(InputPipe.of(input -> 1 / input))
                .addPipe(StandardPipe.of(input -> input.or(1)))
                .processUnsafe(0);

        Assertions.assertEquals(0, output);
    }

    @Test
    public void errorCatchingTest() {
        int output = new Pipeline<Integer>()
                .addPipe(InputPipe.of(input -> 1 / input))
                .addPipe(StandardPipe.of(
                        input -> {
                            if (input.caught()) return -1;
                            else return input.or(1);
                        }))
                .processUnsafe(0);

        Assertions.assertEquals(-1, output);
    }

    @Test
    public void uncancellablePipelineTest() {
        Assertions.assertThrows(IllegalPipeException.class, () ->
                new Pipeline<Float>()
                        .addPipe(InputPipe.of(input -> input + 1F))
                        .addPipe(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 < input) cancelPipeline.run();
                                    return input;
                                }))
                        .addPipe(InputPipe.of(input -> input / 2F))
                        .processUnsafe(4F));
    }

    @Test
    public void returnCancelBehaviourTest() {
        float output = new Pipeline<Float>()
                .setCancelBehaviour(CancelBehaviour.RETURN)
                .addPipe(InputPipe.of(input -> input + 1F))
                .addPipe(CancellablePipe.of(
                        (cancelPipeline, input, throwable) -> {
                            if (2 < input) cancelPipeline.run();
                            return input;
                        }))
                .addPipe(InputPipe.of(input -> input / 2F))
                .processUnsafe(4F);

        Assertions.assertEquals(5, output);
    }

    @Test
    public void discardCancelBehaviourTest() {
        Exceptional<Float> output = new Pipeline<Float>()
                .setCancelBehaviour(CancelBehaviour.DISCARD)
                .addPipe(InputPipe.of(input -> input + 1F))
                .addPipe(CancellablePipe.of(
                        (cancelPipeline, input, throwable) -> {
                            if (2 < input) cancelPipeline.run();
                            return input;
                        }))
                .addPipe(InputPipe.of(input -> input / 2F))
                .process(4F);

        Assertions.assertFalse(output.present());
    }

    @Test
    public void convertCancelBehaviourTest() {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                new Pipeline<Float>()
                        .setCancelBehaviour(CancelBehaviour.CONVERT)
                        .addPipe(InputPipe.of(input -> input + 1F))
                        .addPipe(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 < input) cancelPipeline.run();
                                    return input;
                                }))
                        .addPipe(InputPipe.of(input -> input / 2F))
                        .processUnsafe(4F));
    }

    @Test
    public void removingPipesTest() {
        AbstractPipeline<Integer, Integer> pipeline = new Pipeline<Integer>()
                .addPipe(InputPipe.of(input -> input * 2))
                .addPipe(InputPipe.of(input -> input + 3))
                .addPipe(InputPipe.of(input -> input - 1));

        int output = pipeline.processUnsafe(8);
        Assertions.assertEquals(18, output);

        pipeline.removeLastPipe();
        pipeline.removePipeAt(0);
        output = pipeline.processUnsafe(8);

        Assertions.assertEquals(11, output);
    }

    @SuppressWarnings("ReturnOfNull")
    @Test
    public void processingCollectionInputsTest() {
        List<Integer> output = new Pipeline<Integer>()
                .addPipe(InputPipe.of(input -> 0 == input % 2 ? input : null))
                .addPipe(InputPipe.of(input -> input * 2))
                .processAllSafe(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assertions.assertEquals(Arrays.asList(4, 8, 12, 16, 20), output);
    }
}
