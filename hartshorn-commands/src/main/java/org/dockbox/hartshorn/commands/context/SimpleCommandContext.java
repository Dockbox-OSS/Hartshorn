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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("unchecked")
@AllArgsConstructor
@Deprecated
public class SimpleCommandContext implements CommandContext {

    public static final SimpleCommandContext EMPTY = new SimpleCommandContext(
            "",
            new CommandParameter[0],
            new CommandParameter[0],
            null,
            new String[0]);

    private final String usage;
    private final CommandParameter<?>[] args;
    private final CommandParameter<?>[] flags;

    // Location and world are snapshots of the location of our CommandSource at the time the command
    // was processed.
    // This way developers can ensure location data does not change while the command is being
    // performed.
    @Getter
    private final CommandSource sender;
    @Getter
    private final String[] permissions;

    @NotNull
    @Override
    public String alias() {
        return this.usage.split(" ")[0];
    }

    @Override
    public int arguments() {
        return this.args.length;
    }

    @Override
    public int flags() {
        return this.flags.length;
    }

    @NotNull
    @Override
    public <T> Exceptional<CommandParameter<T>> argument(@NonNls @NotNull String key) {
        return Exceptional.of(Arrays.stream(this.args)
                .filter(arg -> arg.getKey().equals(key))
                .findFirst()
        ).map(arg -> (CommandParameter<T>) arg);
    }

    @Override
    public <T> T get(@NonNls String key) {
        return Arrays.stream(HartshornUtils.merge(this.args, this.flags))
                .map(CommandParameter.class::cast)
                .filter(arg -> arg.getKey().equals(key))
                .findFirst()
                .map(arg -> (T) arg.getValue())
                .orElse(null);
    }

    @Override
    public <T> Exceptional<T> optional(String key) {
        return Exceptional.of((T) this.get(key));
    }

    @NotNull
    @Override
    public <T> Exceptional<CommandParameter<T>> flag(@NonNls @NotNull String key) {
        return Exceptional.of(Arrays.stream(this.flags)
                .filter(flag -> flag.getKey().equals(key))
                .findFirst()
        ).map(flag -> (CommandParameter<T>) flag);
    }

    @Override
    public boolean has(@NonNls @NotNull String key) {
        return Arrays.stream(HartshornUtils.merge(this.args, this.flags))
                .map(CommandParameter.class::cast)
                .anyMatch(arg -> arg.getKey().equals(key));
    }
}
