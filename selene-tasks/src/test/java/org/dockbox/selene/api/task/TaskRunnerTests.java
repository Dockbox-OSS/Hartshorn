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

package org.dockbox.selene.api.task;

import org.dockbox.selene.di.Provider;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ExtendWith(SeleneJUnit5Runner.class)
public class TaskRunnerTests {

    private final CountDownLatch lock = new CountDownLatch(1);

    @Test
    void testTaskRunsSync() {
        TaskRunner runner = Provider.provide(TaskRunner.class);
        final boolean[] activated = { false };
        Task task = () -> activated[0] = true;
        runner.accept(task);

        Assertions.assertTrue(activated[0]);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testTaskRunsDelayed() throws InterruptedException {
        TaskRunner runner = Provider.provide(TaskRunner.class);
        final boolean[] activated = { false };
        Task task = () -> activated[0] = true;
        runner.acceptDelayed(task, 5, TimeUnit.MILLISECONDS);

        this.lock.await(25, TimeUnit.MILLISECONDS);

        Assertions.assertTrue(activated[0]);
    }
}
