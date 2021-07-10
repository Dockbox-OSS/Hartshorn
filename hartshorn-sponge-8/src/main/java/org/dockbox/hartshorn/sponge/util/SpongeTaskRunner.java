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

package org.dockbox.hartshorn.sponge.util;

import org.dockbox.hartshorn.api.task.Task;
import org.dockbox.hartshorn.api.task.TaskRunner;
import org.dockbox.hartshorn.sponge.Sponge8Application;
import org.spongepowered.api.Sponge;

import java.util.concurrent.TimeUnit;

public class SpongeTaskRunner extends TaskRunner {

    @Override
    public void accept(Task task) {
        Sponge.asyncScheduler()
                .createExecutor(Sponge8Application.container())
                .schedule(task::run, 0, TimeUnit.SECONDS)
                .run();
    }

    @Override
    public void acceptDelayed(Task task, long delay, TimeUnit timeUnit) {
        Sponge.asyncScheduler()
                .createExecutor(Sponge8Application.container())
                .schedule(task::run, delay, timeUnit)
                .run();
    }
}
