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

package org.dockbox.selene.sponge.util;

import org.dockbox.selene.api.task.Task;
import org.dockbox.selene.api.task.TaskRunner;
import org.dockbox.selene.sponge.Sponge7Application;
import org.spongepowered.api.Sponge;

import java.util.concurrent.TimeUnit;

public class SpongeTaskRunner extends TaskRunner {

    @Override
    public void accept(Task task) {
        Sponge.getScheduler()
                .createTaskBuilder()
                .execute(task::run)
                .name("Selene$" + task.getClass().getSimpleName() + '#' + System.currentTimeMillis())
                .async()
                .submit(Sponge7Application.container());
    }

    @Override
    public void acceptDelayed(Task task, long delay, TimeUnit timeUnit) {
        Sponge.getScheduler()
                .createTaskBuilder()
                .delay(delay, timeUnit)
                .execute(task::run)
                .name("Selene$" + task.getClass().getSimpleName() + '#' + System.currentTimeMillis())
                .async()
                .submit(Sponge7Application.container());
    }
}
