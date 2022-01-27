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

package org.dockbox.hartshorn.core.task.pipeline.pipelines;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @deprecated Moved to https://github.com/GuusLieben/JPipelines
 */
@Deprecated(forRemoval = true, since = "22.2")
public class ConvertiblePipelineSource<I> extends ConvertiblePipeline<I, I> {

    /**
     * Calls the super constructor to instantiate a new convertible pipeline.
     *
     * @param inputClass The {@link Class} of the {@code I} input type
     */
    public ConvertiblePipelineSource(final Class<I> inputClass) {
        super(inputClass);
    }

    /**
     * Processes an input by first wrapping it in an {@link Exceptional}.
     *
     * @param input The non-null {@code I} input to be processed by the pipeline
     * @param throwable A nullable {@link Throwable} that may wish to be passed in
     *
     * @return An {@link Exceptional} containing the {@code I} output
     */
    @Override
    public Exceptional<I> process(@NonNull final I input, @Nullable final Throwable throwable) {
        final Exceptional<I> exceptionalInput = Exceptional.of(input, throwable);
        return super.process(exceptionalInput);
    }
}
