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

package org.dockbox.hartshorn.core.task.pipeline;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.task.pipeline.pipelines.ConvertiblePipeline;
import org.dockbox.hartshorn.core.task.pipeline.pipelines.Pipeline;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @deprecated Moved to https://github.com/GuusLieben/JPipelines
 */
@Deprecated(forRemoval = true, since = "22.2")
public enum CancelBehaviour {
    NON_CANCELLABLE,
    DISCARD(output -> null),
    CONVERT((output, converter) -> (null == converter) ? output : converter.apply(output)),
    RETURN(output -> output);

    @Nullable
    private final Function<Object, Object> function;
    @Nullable
    private final BiFunction<Object, Function<Object, Object>, Object> biFunction;

    CancelBehaviour() {
        this.function = null;
        this.biFunction = null;
    }

    CancelBehaviour(@NonNull final Function<Object, Object> function) {
        this.function = function;
        this.biFunction = null;
    }

    CancelBehaviour(@NonNull final BiFunction<Object, Function<Object, Object>, Object> function) {
        this.function = null;
        this.biFunction = function;
    }

    /**
     * Determines what should be returned by a {@link Pipeline} when it's cancelled by calling the
     * {@link Function} on it.
     *
     * @param output The output of the pipeline to be acted upon by the cancel behaviour
     *
     * @return The output after it has been acted upon
     * @throws UnsupportedOperationException If the cancel behaviour has no {@link Function}
     */
    public Object act(final Object output) {
        if (null == this.function) {
            throw new UnsupportedOperationException("The provided cancel behaviour is not supported by this pipeline.");
        }
        return this.function.apply(output);
    }

    /**
     * Determines what should be returned by a {@link ConvertiblePipeline} when it's cancelled by
     * calling the cancellable behaviours {@link Function} or {@link BiFunction} on it.
     *
     * @param output The output of the pipeline to be acted upon by the cancel behaviour
     * @param converter The converter of the current pipeline
     *
     * @return The output after it has been acted upon
     * @throws UnsupportedOperationException If the cancel behaviour has no {@link Function} or {@link BiFunction}
     */
    public Object act(final Object output, @Nullable final Function<Object, Object> converter) {
        if (null != this.function) {
            return this.function.apply(output);
        }

        if (null != this.biFunction) {
            return this.biFunction.apply(output, converter);
        }
        throw new UnsupportedOperationException("The provided cancel behaviour is not supported by this pipeline.");
    }
}
