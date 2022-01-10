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

import org.dockbox.hartshorn.core.task.pipeline.pipelines.Pipeline;
import org.dockbox.hartshorn.core.task.pipeline.pipes.EqualPipe;

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
