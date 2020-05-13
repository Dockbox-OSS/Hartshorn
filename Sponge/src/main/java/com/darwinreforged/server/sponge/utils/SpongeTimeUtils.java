package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.util.TimeUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task.Builder;

import java.util.concurrent.TimeUnit;

@UtilityImplementation(TimeUtils.class)
public class SpongeTimeUtils extends TimeUtils {

    @Override
    public Scheduler schedule() {
        return new SpongeScheduler();
    }

    public static final class SpongeScheduler extends Scheduler {

        private Builder builder;

        public SpongeScheduler() {
            this.builder = Sponge.getScheduler().createTaskBuilder();
        }

        @Override
        public Scheduler async() {
            builder = builder.async();
            return this;
        }

        @Override
        public Scheduler name(String name) {
            builder = builder.name(name);
            return this;
        }

        @Override
        public Scheduler delay(long delay, TimeUnit unit) {
            builder = builder.delay(delay, unit);
            return this;
        }

        @Override
        public Scheduler delayTicks(long delay) {
            builder = builder.delayTicks(delay);
            return this;
        }

        @Override
        public Scheduler interval(long delay, TimeUnit unit) {
            builder = builder.interval(delay, unit);
            return this;
        }

        @Override
        public Scheduler intervalTicks(long delay) {
            builder = builder.intervalTicks(delay);
            return this;
        }

        @Override
        public Scheduler execute(Runnable runnable) {
            builder = builder.execute(runnable);
            return this;
        }

        @Override
        public void submit() {
            builder.submit(DarwinServer.getServer());
        }
    }
}
