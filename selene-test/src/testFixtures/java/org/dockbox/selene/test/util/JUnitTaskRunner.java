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

package org.dockbox.selene.test.util;

import org.dockbox.selene.api.task.Task;
import org.dockbox.selene.api.task.TaskRunner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JUnitTaskRunner extends TaskRunner {

    @Override
    public void accept(Task task) {
        Executors.newSingleThreadExecutor().submit(task::run);
    }

    @Override
    public void acceptDelayed(Task task, long delay, TimeUnit timeUnit) {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(task::run, delay, timeUnit);
    }
}
