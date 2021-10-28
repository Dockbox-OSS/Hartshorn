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
import org.dockbox.hartshorn.core.task.pipeline.pipelines.AbstractPipeline;
import org.dockbox.hartshorn.core.task.pipeline.pipelines.Pipeline;
import org.dockbox.hartshorn.core.task.pipeline.pipes.CancellablePipe;
import org.dockbox.hartshorn.core.task.pipeline.pipes.InputPipe;
import org.dockbox.hartshorn.core.task.pipeline.pipes.Pipe;
import org.dockbox.hartshorn.core.task.pipeline.pipes.StandardPipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class PipelineTests {

    @Test
    public void genericPipelineTest() {
        final int result = new Pipeline<Integer>()
                .add(Pipe.of((input, throwable) -> input + 1))
                .addVarargPipes(
                        Pipe.of((input, throwable) -> input * 2),
                        Pipe.of(((input, throwable) -> input - 3)))
                .add(StandardPipe.of(input -> input.or(-1)))
                .processUnsafe(5);

        Assertions.assertEquals(9, result);
    }

    @Test
    public void addingPipelinesTest() {
        final AbstractPipeline<String, String> pipeline = new Pipeline<String>().add(Pipe.of((input, throwable) -> "- " + input + " -"));

        final String result = new Pipeline<String>()
                .add(Pipe.of((input, throwable) -> input.substring(0, 1).toUpperCase() + input.substring(1)))
                .add(pipeline)
                .processUnsafe("hi world");

        Assertions.assertEquals("- Hi world -", result);
    }

    @Test
    public void passingInputForwardOnErrorTest() {
        final int output = new Pipeline<Integer>()
                .add(InputPipe.of(input -> 1 / input))
                .add(StandardPipe.of(input -> input.or(1)))
                .processUnsafe(0);

        Assertions.assertEquals(0, output);
    }

    @Test
    public void errorCatchingTest() {
        final int output = new Pipeline<Integer>()
                .add(InputPipe.of(input -> 1 / input))
                .add(StandardPipe.of(
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
                        .add(InputPipe.of(input -> input + 1F))
                        .add(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 < input) cancelPipeline.run();
                                    return input;
                                }))
                        .add(InputPipe.of(input -> input / 2F))
                        .processUnsafe(4F));
    }

    @Test
    public void returnCancelBehaviourTest() {
        final float output = new Pipeline<Float>()
                .cancelBehaviour(CancelBehaviour.RETURN)
                .add(InputPipe.of(input -> input + 1F))
                .add(CancellablePipe.of(
                        (cancelPipeline, input, throwable) -> {
                            if (2 < input) cancelPipeline.run();
                            return input;
                        }))
                .add(InputPipe.of(input -> input / 2F))
                .processUnsafe(4F);

        Assertions.assertEquals(5, output);
    }

    @Test
    public void discardCancelBehaviourTest() {
        final Exceptional<Float> output = new Pipeline<Float>()
                .cancelBehaviour(CancelBehaviour.DISCARD)
                .add(InputPipe.of(input -> input + 1F))
                .add(CancellablePipe.of(
                        (cancelPipeline, input, throwable) -> {
                            if (2 < input) cancelPipeline.run();
                            return input;
                        }))
                .add(InputPipe.of(input -> input / 2F))
                .process(4F);

        Assertions.assertFalse(output.present());
    }

    @Test
    public void convertCancelBehaviourTest() {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                new Pipeline<Float>()
                        .cancelBehaviour(CancelBehaviour.CONVERT)
                        .add(InputPipe.of(input -> input + 1F))
                        .add(CancellablePipe.of(
                                (cancelPipeline, input, throwable) -> {
                                    if (2 < input) cancelPipeline.run();
                                    return input;
                                }))
                        .add(InputPipe.of(input -> input / 2F))
                        .processUnsafe(4F));
    }

    @Test
    public void removingPipesTest() {
        final AbstractPipeline<Integer, Integer> pipeline = new Pipeline<Integer>()
                .add(InputPipe.of(input -> input * 2))
                .add(InputPipe.of(input -> input + 3))
                .add(InputPipe.of(input -> input - 1));

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
        final List<Integer> output = new Pipeline<Integer>()
                .add(InputPipe.of(input -> 0 == input % 2 ? input : null))
                .add(InputPipe.of(input -> input * 2))
                .processAllSafe(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assertions.assertEquals(Arrays.asList(4, 8, 12, 16, 20), output);
    }
}
