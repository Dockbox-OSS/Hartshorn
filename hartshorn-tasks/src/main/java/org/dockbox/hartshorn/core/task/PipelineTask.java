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

import org.dockbox.hartshorn.core.task.pipeline.pipelines.Pipeline;
import org.dockbox.hartshorn.core.task.pipeline.pipes.EqualPipe;

@Deprecated(forRemoval = true, since = "22.2")
public abstract class PipelineTask extends AbstractTask {

    private final Pipeline<Void> pipeline;

    protected PipelineTask() {
        this.pipeline = new Pipeline<>();
        this.pipeline.add((EqualPipe<Void>) this::init);
        this.pipeline.add((EqualPipe<Void>) PipelineTask.this::perform);
        this.pipeline.add((EqualPipe<Void>) this::complete);
    }

    @Override
    public void run() {
        this.pipeline.process(null);
    }

    @Override
    public void cancel() {
        this.pipeline.cancel();
    }
}
