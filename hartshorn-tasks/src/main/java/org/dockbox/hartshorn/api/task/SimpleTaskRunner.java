package org.dockbox.hartshorn.api.task;

import org.dockbox.hartshorn.di.annotations.inject.Binds;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Binds(TaskRunner.class)
public class SimpleTaskRunner extends TaskRunner {

    @Override
    public void accept(final Task task) {
        task.run();
    }

    @Override
    public void acceptDelayed(final Task task, final long delay, final TimeUnit timeUnit) {
        final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(task::run, delay, timeUnit);
    }
}
