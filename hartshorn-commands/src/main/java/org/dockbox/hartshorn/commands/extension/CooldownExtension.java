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

package org.dockbox.hartshorn.commands.extension;

import org.dockbox.hartshorn.commands.CommandResources;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Cooldown;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandExecutorContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.util.Identifiable;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;

/**
 * Extends a command by providing a cooldown on its execution. If a command is
 * performed multiple times its execution is cancelled if it is repeated too
 * quickly. The delay between commands is defined by a {@link Cooldown}
 * decorator on the command.
 */
@Component
public class CooldownExtension implements CommandExecutorExtension {

    @Inject
    private CommandResources resources;

    private final Map<Object, CooldownEntry> activeCooldowns = new ConcurrentHashMap<>();

    @Override
    public ExtensionResult execute(final CommandContext context, final CommandExecutorContext executorContext) {
        final CommandSource sender = context.source();
        if (!(sender instanceof Identifiable)) return ExtensionResult.accept();

        final String id = this.id((Identifiable) sender, context);
        if (this.inCooldown(id)) {
            context.applicationContext().log().debug("Executor with ID '%s' is in active cooldown, rejecting command execution of %s".formatted(id, context.command()));
            return ExtensionResult.reject(this.resources.cooldownActive());
        }
        else {
            final Cooldown cooldown = executorContext.element().annotations().get(Cooldown.class).get();
            this.cooldown(id, cooldown.duration(), cooldown.unit());
            return ExtensionResult.accept();
        }
    }

    @Override
    public boolean extend(final CommandExecutorContext context) {
        return context.element().annotations().has(Cooldown.class);
    }

    /**
     * Places an object in the cooldown queue for a given amount of time. If the object is already in
     * the cooldown queue it will not be overwritten and the existing queue position with be kept.
     *
     * @param o The object to place in cooldown
     * @param duration The duration
     * @param timeUnit The time unit in which the duration is kept
     */
    protected void cooldown(final Object o, final long duration, final TemporalUnit timeUnit) {
        if (this.inCooldown(o)) return;
        this.activeCooldowns.put(o, new CooldownEntry(LocalDateTime.now(), duration, timeUnit));
    }

    /**
     * Returns true if an object is in an active cooldown queue. Otherwise false
     *
     * @param o The object
     *
     * @return true if an object is in an active cooldown queue. Otherwise false
     */
    protected boolean inCooldown(final Object o) {
        if (this.activeCooldowns.containsKey(o)) {
            final LocalDateTime now = LocalDateTime.now();
            final CooldownEntry cooldown = this.activeCooldowns.get(o);
            final LocalDateTime timeCooledDown = cooldown.startTime();
            final long duration = cooldown.duration();
            final TemporalUnit timeUnit = cooldown.timeUnit();

            final LocalDateTime endTime = timeCooledDown.plus(duration, timeUnit);

            return endTime.isAfter(now);

        }
        else return false;
    }

    private record CooldownEntry(LocalDateTime startTime, long duration, TemporalUnit timeUnit) {
    }
}
