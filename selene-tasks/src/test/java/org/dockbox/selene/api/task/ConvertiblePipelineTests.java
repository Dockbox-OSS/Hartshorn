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

package org.dockbox.selene.api.task;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.task.pipeline.CancelBehaviour;
import org.dockbox.selene.api.task.pipeline.exceptions.IllegalPipeException;
import org.dockbox.selene.api.task.pipeline.pipelines.ConvertiblePipeline;
import org.dockbox.selene.api.task.pipeline.pipelines.ConvertiblePipelineSource;
import org.dockbox.selene.api.task.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.api.task.pipeline.pipes.InputPipe;
import org.dockbox.selene.api.task.pipeline.pipes.Pipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ConvertiblePipelineTests {

    @Test
    public void simpleConvertablePipelineTest() {
        float output = new ConvertiblePipelineSource<>(Integer.class)
                .addPipe(InputPipe.of(input -> input * 2))
                .convertPipeline(integer -> (float) integer, Float.class)
                .addPipe(InputPipe.of(input -> input / 5F))
                .addPipe(InputPipe.of(input -> input * 2))
                .processUnsafe(18);

        Assertions.assertEquals(14.4F, output);
    }

    @Test
    public void addingPipesToUncancellablePipelineTest() {
        Assertions.assertThrows(IllegalPipeException.class, () ->
                new ConvertiblePipelineSource<>(Integer.class)
                        .addPipe(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 < input) cancelPipeline.run();
                                    return input;
                                }))
                        .addPipe(Pipe.of((input, throwable) -> input - 3))
                        .process(4));
    }

    @Test
    public void convertCancelBehaviourTest() {
        ConvertiblePipeline<Integer, String> pipeline =
                new ConvertiblePipelineSource<>(Integer.class)
                        .setCancelBehaviour(CancelBehaviour.CONVERT)
                        .addPipe(InputPipe.of(input -> input + 1))
                        .addPipe(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 > input) cancelPipeline.run();
                                    return input;
                                }))
                        .addPipe(InputPipe.of(input -> input + 1))
                        .convertPipeline(String::valueOf, String.class)
                        .addPipe(InputPipe.of(input -> input + "1"));

        // Doesn't cancel.
        String output = pipeline.processUnsafe(2);
        Assertions.assertEquals("41", output);

        // Does cancel.
        output = pipeline.processUnsafe(0);
        Assertions.assertEquals("1", output);
    }

    @Test
    public void returnCancelBehaviourTest() {
        ConvertiblePipeline<Float, Integer> pipeline =
                new ConvertiblePipelineSource<>(Float.class)
                        .setCancelBehaviour(CancelBehaviour.RETURN)
                        .addPipe(InputPipe.of(input -> input + 1))
                        .addPipe(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 > input) cancelPipeline.run();
                                    return input;
                                }))
                        .addPipe(InputPipe.of(input -> input + 1))
                        .convertPipeline(Float::intValue, Integer.class)
                        .addPipe(InputPipe.of(input -> input + 1));

        // Doesn't cancel
        int output = pipeline.processUnsafe(2F);
        Assertions.assertEquals(5, output);

        // Does cancel - Will return a Float as its not converted.
        Exceptional<Integer> safeOutput = pipeline.process(0F);
        Assertions.assertTrue(safeOutput.present());
        Assertions.assertEquals(Float.class, safeOutput.type());
    }

    @Test
    public void discardCancelBehaviourTest() {
        ConvertiblePipeline<Float, Integer> pipeline =
                new ConvertiblePipelineSource<>(Float.class)
                        .setCancelBehaviour(CancelBehaviour.DISCARD)
                        .addPipe(InputPipe.of(input -> input + 1))
                        .addPipe(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 > input) cancelPipeline.run();
                                    return input;
                                }))
                        .addPipe(InputPipe.of(input -> input + 1))
                        .convertPipeline(Float::intValue, Integer.class)
                        .addPipe(InputPipe.of(input -> input + 1));

        // Doesn't cancel
        int output = pipeline.processUnsafe(2F);
        Assertions.assertEquals(5, output);

        // Does cancel - Will return an none exceptional as output is discarded.
        Exceptional<Integer> safeOutput = pipeline.process(0F);
        Assertions.assertFalse(safeOutput.present());
    }

    @Test
    public void multipleCancelBehavioursTest() {
        ConvertiblePipeline<Float, Integer> pipeline =
                new ConvertiblePipelineSource<>(Float.class)
                        .setCancelBehaviour(CancelBehaviour.DISCARD)
                        .addPipe(InputPipe.of(input -> input + 1))
                        .addPipe(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 > input) cancelPipeline.run();
                                    return input;
                                }))
                        .addPipe(InputPipe.of(input -> input + 1))
                        .convertPipeline(Float::intValue, Integer.class)
                        .setCancelBehaviour(CancelBehaviour.CONVERT)
                        .addPipe(InputPipe.of(input -> input + 1));

        // Doesn't cancel
        int output = pipeline.processUnsafe(2F);
        Assertions.assertEquals(5, output);

        // Does cancel - Will convert the output as it takes the last set CancelBehaviour.
        output = pipeline.processUnsafe(0F);
        Assertions.assertEquals(1, output);
    }

    @Test
    public void convertiblePipelineCancellableAfterConversionTest() {
        int output =
                new ConvertiblePipelineSource<>(Float.class)
                        .setCancelBehaviour(CancelBehaviour.CONVERT)
                        .addPipe(Pipe.of((input, throwable) -> input + 1F))
                        .convertPipeline(Float::intValue, Integer.class)
                        .addPipe(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    cancelPipeline.run();
                                    return input;
                                }))
                        .addPipe(Pipe.of((input, throwable) -> input + 3))
                        .processUnsafe(3F);

        Assertions.assertEquals(4, output);
    }

    @Test
    public void removePipelineTest() {
        int output = new ConvertiblePipelineSource<>(Integer.class)
                .addPipe(Pipe.of((input, throwable) -> input + 3))
                .convertPipeline(integer -> (float) integer, Float.class)
                .addPipe(Pipe.of(((input, throwable) -> input / 2)))
                .removePipeline(Integer.class)
                .addPipe(Pipe.of((input, throwable) -> input - 3))
                .processUnsafe(4);

        Assertions.assertEquals(4, output);
    }

    @Test
    public void convertiblePipelineSizeTest() {
        int size = new ConvertiblePipelineSource<>(Integer.class)
                .addPipe(Pipe.of((input, throwable) -> input + 3))
                .addPipe(Pipe.of((input, throwable) -> input * 2))
                .convertPipeline(integer -> (float) integer, Float.class)
                .addPipe(Pipe.of((input, throwable) -> input + 1F))
                .size();

        Assertions.assertEquals(4, size);
    }

    @Test
    public void processingCollectionInputsTest() {
        List<Integer> output = new ConvertiblePipelineSource<>(String.class)
                .convertPipeline(Integer::valueOf, Integer.class)
                .addPipe(InputPipe.of(input -> input * input))
                .processAllSafe(Arrays.asList("1", "2", "3", "4", "5"));

        Assertions.assertEquals(Arrays.asList(1, 4, 9, 16, 25), output);
    }
}
