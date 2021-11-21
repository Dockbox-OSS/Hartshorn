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

import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TaskRunnerTests extends ApplicationAwareTest {

    private final CountDownLatch lock = new CountDownLatch(1);

    @Test
    void testTaskRunsSync() {
        final TaskRunner runner = this.context().get(TaskRunner.class);
        final boolean[] activated = { false };
        final Task task = () -> activated[0] = true;
        runner.accept(task);

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testTaskRunsDelayed() throws InterruptedException {
        final TaskRunner runner = this.context().get(TaskRunner.class);
        final boolean[] activated = { false };
        final Task task = () -> activated[0] = true;
        runner.acceptDelayed(task, 5, TimeUnit.MILLISECONDS);

        this.lock.await(25, TimeUnit.MILLISECONDS);

        Assertions.assertTrue(activated[0]);
    }
}
