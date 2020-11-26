/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.integrated.data.pipeline;

import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineException;
import org.dockbox.selene.integrated.data.pipeline.exceptions.IllegalPipelineConverterException;
import org.dockbox.selene.integrated.data.pipeline.pipes.CancellablePipe;
import org.dockbox.selene.integrated.data.pipeline.pipes.Pipe;
import org.junit.Assert;
import org.junit.Test;

public class ConvertiblePipelineTests {

    @Test
    public void simpleConvertablePipelineTest() {
        float output = new ConvertiblePipelineSource<>(Integer.class)
            .addPipe(
                Pipe.of((input, throwable) -> input * 2)
            ).convertPipeline(
                integer -> (float)integer, Float.class
            ).addPipe(
                Pipe.of((input, throwable) -> input / 6F)
            ).addPipe(
                Pipe.of((input, throwable) -> input * 2)
            ).processUnsafe(18);

        Assert.assertEquals(12F, output, 0.0);
    }

    @Test(expected = IllegalPipelineConverterException.class)
    public void illegalPipelineConverterTest() {
        new ConvertiblePipelineSource<>(String.class)
            .addPipe(
                Pipe.of((input, throwable) -> input + "ing")
            ).convertPipeline(
                string -> null, Integer.class
            ).process("Look");
    }

    @Test(expected = IllegalPipelineException.class)
    public void convertiblePipelineIllegalPipeExceptionTest() {
        new ConvertiblePipelineSource<>(Integer.class)
            .addPipe(
                CancellablePipe.of((cancelPipeline, input, throwable) -> {
                    if (2 < input) cancelPipeline.run();
                    return input;
                })
            ).addPipe(
                Pipe.of((input, throwable) -> input - 3)
            ).process(4);
    }

    @Test(expected = IllegalPipelineException.class)
    public void illegalPipeExceptionWhenConvertingPipelineTest() {
        new ConvertiblePipelineSource<>(Integer.class)
            .setCancellable(true)
            .addPipe(
                CancellablePipe.of((cancelPipeline, input, throwable) -> {
                    if (2 < input) cancelPipeline.run();
                    return input;
                })
            ).convertPipeline(Object::toString, String.class)
            .addPipe(
                Pipe.of((input, throwable) -> input + " - Test Suffix")
            ).process(1);
    }

    @Test
    public void convertiblePipelineCancellableTest() {
        int output = new ConvertiblePipelineSource<>(Integer.class)
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
            ).processUnsafe(3);

        Assert.assertEquals(4, output);
    }

    @Test
    public void convertiblePipelineCancellableAfterConversionTest() {
        int output = new ConvertiblePipelineSource<>(Float.class)
            .setCancellable(true)
            .addPipe(
                Pipe.of((input, throwable) -> input + 1F)
            ).convertPipeline(Float::intValue, Integer.class)
            .addPipe(
                CancellablePipe.of((cancelPipeline, input, throwable) -> {
                    cancelPipeline.run();
                    return input;
                })
            ).addPipe(
                Pipe.of((input, throwable) -> input + 3)
            ).processUnsafe(3F);

        Assert.assertEquals(4, output);
    }

    @Test
    public void removePipelineTest() {
        int output = new ConvertiblePipelineSource<>(Integer.class)
            .addPipe(
                Pipe.of((input, throwable) -> input + 3)
            ).convertPipeline(
                integer -> (float)integer, Float.class
            ).addPipe(
                Pipe.of(((input, throwable) -> input / 2))
            ).removePipeline(Integer.class)
            .addPipe(
                Pipe.of((input, throwable) -> input - 3)
            ).processUnsafe(4);

        Assert.assertEquals(4, output);
    }

    @Test
    public void convertiblePipelineSizeTest() {
        int size = new ConvertiblePipelineSource<>(Integer.class)
            .addPipe(
                Pipe.of((input, throwable) -> input + 3)
            ).addPipe(
                Pipe.of((input, throwable) -> input * 2)
            ).convertPipeline(integer -> (float)integer, Float.class)
            .addPipe(
                Pipe.of((input, throwable) -> input + 1F)
            ).size();

        Assert.assertEquals(4, size);
    }
}
