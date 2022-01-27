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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import lombok.Getter;

@HartshornTest
public class TaskRunnerTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    private final CountDownLatch lock = new CountDownLatch(1);

    @Test
    void testTaskRunsSync() {
        final TaskRunner runner = this.applicationContext().get(TaskRunner.class);
        final boolean[] activated = { false };
        final Task task = () -> activated[0] = true;
        runner.accept(task);

        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testTaskRunsDelayed() throws InterruptedException {
        final TaskRunner runner = this.applicationContext().get(TaskRunner.class);
        final boolean[] activated = { false };
        final Task task = () -> activated[0] = true;
        runner.acceptDelayed(task, 5, TimeUnit.MILLISECONDS);

        this.lock.await(25, TimeUnit.MILLISECONDS);

        Assertions.assertTrue(activated[0]);
    }
}
