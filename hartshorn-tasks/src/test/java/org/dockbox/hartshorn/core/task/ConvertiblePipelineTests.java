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

package org.dockbox.hartshorn.core.task;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.task.pipeline.CancelBehaviour;
import org.dockbox.hartshorn.core.task.pipeline.exceptions.IllegalPipeException;
import org.dockbox.hartshorn.core.task.pipeline.pipelines.ConvertiblePipeline;
import org.dockbox.hartshorn.core.task.pipeline.pipelines.ConvertiblePipelineSource;
import org.dockbox.hartshorn.core.task.pipeline.pipes.CancellablePipe;
import org.dockbox.hartshorn.core.task.pipeline.pipes.InputPipe;
import org.dockbox.hartshorn.core.task.pipeline.pipes.Pipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ConvertiblePipelineTests {

    @Test
    public void simpleConvertablePipelineTest() {
        final float output = new ConvertiblePipelineSource<>(Integer.class)
                .add(InputPipe.of(input -> input * 2))
                .convertPipeline(integer -> (float) integer, Float.class)
                .add(InputPipe.of(input -> input / 5F))
                .add(InputPipe.of(input -> input * 2))
                .processUnsafe(18);

        Assertions.assertEquals(14.4F, output);
    }

    @Test
    public void addingPipesToUncancellablePipelineTest() {
        Assertions.assertThrows(IllegalPipeException.class, () ->
                new ConvertiblePipelineSource<>(Integer.class)
                        .add(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 < input) cancelPipeline.run();
                                    return input;
                                }))
                        .add(Pipe.of((input, throwable) -> input - 3))
                        .process(4));
    }

    @Test
    public void convertCancelBehaviourTest() {
        final ConvertiblePipeline<Integer, String> pipeline =
                new ConvertiblePipelineSource<>(Integer.class)
                        .cancelBehaviour(CancelBehaviour.CONVERT)
                        .add(InputPipe.of(input -> input + 1))
                        .add(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 > input) cancelPipeline.run();
                                    return input;
                                }))
                        .add(InputPipe.of(input -> input + 1))
                        .convertPipeline(String::valueOf, String.class)
                        .add(InputPipe.of(input -> input + "1"));

        // Doesn't cancel.
        String output = pipeline.processUnsafe(2);
        Assertions.assertEquals("41", output);

        // Does cancel.
        output = pipeline.processUnsafe(0);
        Assertions.assertEquals("1", output);
    }

    @Test
    public void returnCancelBehaviourTest() {
        final ConvertiblePipeline<Float, Integer> pipeline =
                new ConvertiblePipelineSource<>(Float.class)
                        .cancelBehaviour(CancelBehaviour.RETURN)
                        .add(InputPipe.of(input -> input + 1))
                        .add(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 > input) cancelPipeline.run();
                                    return input;
                                }))
                        .add(InputPipe.of(input -> input + 1))
                        .convertPipeline(Float::intValue, Integer.class)
                        .add(InputPipe.of(input -> input + 1));

        // Doesn't cancel
        final int output = pipeline.processUnsafe(2F);
        Assertions.assertEquals(5, output);

        // Does cancel - Will return a Float as its not converted.
        final Exceptional<Integer> safeOutput = pipeline.process(0F);
        Assertions.assertTrue(safeOutput.present());
        Assertions.assertEquals(Float.class, safeOutput.type());
    }

    @Test
    public void discardCancelBehaviourTest() {
        final ConvertiblePipeline<Float, Integer> pipeline =
                new ConvertiblePipelineSource<>(Float.class)
                        .cancelBehaviour(CancelBehaviour.DISCARD)
                        .add(InputPipe.of(input -> input + 1))
                        .add(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 > input) cancelPipeline.run();
                                    return input;
                                }))
                        .add(InputPipe.of(input -> input + 1))
                        .convertPipeline(Float::intValue, Integer.class)
                        .add(InputPipe.of(input -> input + 1));

        // Doesn't cancel
        final int output = pipeline.processUnsafe(2F);
        Assertions.assertEquals(5, output);

        // Does cancel - Will return an none exceptional as output is discarded.
        final Exceptional<Integer> safeOutput = pipeline.process(0F);
        Assertions.assertFalse(safeOutput.present());
    }

    @Test
    public void multipleCancelBehavioursTest() {
        final ConvertiblePipeline<Float, Integer> pipeline =
                new ConvertiblePipelineSource<>(Float.class)
                        .cancelBehaviour(CancelBehaviour.DISCARD)
                        .add(InputPipe.of(input -> input + 1))
                        .add(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 > input) cancelPipeline.run();
                                    return input;
                                }))
                        .add(InputPipe.of(input -> input + 1))
                        .convertPipeline(Float::intValue, Integer.class)
                        .cancelBehaviour(CancelBehaviour.CONVERT)
                        .add(InputPipe.of(input -> input + 1));

        // Doesn't cancel
        int output = pipeline.processUnsafe(2F);
        Assertions.assertEquals(5, output);

        // Does cancel - Will convert the output as it takes the last set CancelBehaviour.
        output = pipeline.processUnsafe(0F);
        Assertions.assertEquals(1, output);
    }

    @Test
    public void convertiblePipelineCancellableAfterConversionTest() {
        final int output =
                new ConvertiblePipelineSource<>(Float.class)
                        .cancelBehaviour(CancelBehaviour.CONVERT)
                        .add(Pipe.of((input, throwable) -> input + 1F))
                        .convertPipeline(Float::intValue, Integer.class)
                        .add(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    cancelPipeline.run();
                                    return input;
                                }))
                        .add(Pipe.of((input, throwable) -> input + 3))
                        .processUnsafe(3F);

        Assertions.assertEquals(4, output);
    }

    @Test
    public void removePipelineTest() {
        final int output = new ConvertiblePipelineSource<>(Integer.class)
                .add(Pipe.of((input, throwable) -> input + 3))
                .convertPipeline(integer -> (float) integer, Float.class)
                .add(Pipe.of(((input, throwable) -> input / 2)))
                .remove(Integer.class)
                .add(Pipe.of((input, throwable) -> input - 3))
                .processUnsafe(4);

        Assertions.assertEquals(4, output);
    }

    @Test
    public void convertiblePipelineSizeTest() {
        final int size = new ConvertiblePipelineSource<>(Integer.class)
                .add(Pipe.of((input, throwable) -> input + 3))
                .add(Pipe.of((input, throwable) -> input * 2))
                .convertPipeline(integer -> (float) integer, Float.class)
                .add(Pipe.of((input, throwable) -> input + 1F))
                .size();

        Assertions.assertEquals(4, size);
    }

    @Test
    public void processingCollectionInputsTest() {
        final List<Integer> output = new ConvertiblePipelineSource<>(String.class)
                .convertPipeline(Integer::valueOf, Integer.class)
                .add(InputPipe.of(input -> input * input))
                .processAllSafe(Arrays.asList("1", "2", "3", "4", "5"));

        Assertions.assertEquals(Arrays.asList(1, 4, 9, 16, 25), output);
    }
}
