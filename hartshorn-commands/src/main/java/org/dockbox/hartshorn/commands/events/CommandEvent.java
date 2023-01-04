/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.commands.events;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.events.AbstractTargetEvent;
import org.dockbox.hartshorn.events.parents.Cancellable;

/**
 * The common parent for command-related events.
 */
public abstract class CommandEvent extends AbstractTargetEvent {

    private final CommandContext commandContext;

    protected CommandEvent(final CommandSource source, final CommandContext commandContext) {
        super(source);
        this.commandContext = commandContext;
    }

    public CommandContext commandContext() {
        return this.commandContext;
    }

    /**
     * The event fired before a command is executed.
     */
    public static class Before extends CommandEvent implements Cancellable {

        private boolean cancelled;

        public Before(final CommandSource source, final CommandContext context) {
            super(source, context);
        }

        public boolean cancelled() {
            return this.cancelled;
        }

        public Before cancelled(final boolean cancelled) {
            this.cancelled = cancelled;
            return this;
        }

        @Override
        public @NonNull Before post() {
            return (Before) super.post();
        }

        @Override
        public Before with(final ApplicationContext context) {
            return (Before) super.with(context);
        }
    }

    /**
     * The event fired after a command is executed.
     */
    public static class After extends CommandEvent {
        public After(final CommandSource source, final CommandContext context) {
            super(source, context);
        }
    }
}
