/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    public void addingPipesToNonCancellablePipelineTest() {
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

        // Does cancel - Will return a Float as it's not converted.
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

        // Does cancel - Will return a none exceptional as output is discarded.
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
