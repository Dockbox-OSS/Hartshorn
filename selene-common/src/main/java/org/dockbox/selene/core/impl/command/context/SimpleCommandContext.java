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

package org.dockbox.selene.core.impl.command.context;

import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.command.context.CommandValue;
import org.dockbox.selene.core.command.context.CommandValue.Argument;
import org.dockbox.selene.core.command.context.CommandValue.Flag;
import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public class SimpleCommandContext implements CommandContext {

    public static final SimpleCommandContext EMPTY = new SimpleCommandContext(
            "", new Argument[0], new Flag[0],
            null, Exceptional.empty(), Exceptional.empty(),
            new String[0]);

    private final String usage;
    private final CommandValue.Argument<?>[] args;
    private final CommandValue.Flag<?>[] flags;
    private final String[] permissions;

    // Location and world are snapshots of the location of our CommandSource at the time the command was processed.
    // This way developers can ensure location data does not change while the command is being performed.
    private final CommandSource sender;
    private final Exceptional<Location> location;
    private final Exceptional<World> world;

    public SimpleCommandContext(
            String usage,
            Argument<?>[] args,
            Flag<?>[] flags,
            CommandSource sender,
            Exceptional<Location> location,
            Exceptional<World> world,
            String[] permissions
    ) {
        this.usage = usage;
        this.args = args;
        this.flags = flags;
        this.sender = sender;
        this.location = location;
        this.world = world;
        this.permissions = permissions;
    }


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
    public <T> Exceptional<Argument<T>> argument(@NonNls @NotNull String key) {
        return Exceptional.of(
                Arrays.stream(this.args)
                        .filter(arg -> arg.getKey().equals(key))
                        .findFirst()
        ).map(arg -> (Argument<T>) arg);
    }

    @Override
    public <T> T get(@NonNls String key) {
        return Arrays.stream(SeleneUtils.merge(this.args, this.flags))
                .filter(arg -> arg.getKey().equals(key))
                .findFirst()
                .map(arg -> (T) arg.getValue())
                .orElse(null);
    }

    @Override
    public <T> Exceptional<T> optional(String key) {
        return Exceptional.ofNullable(this.get(key));
    }

    @NotNull
    @Override
    public <T> Exceptional<Flag<T>> flag(@NonNls @NotNull String key) {
        return Exceptional.of(
                Arrays.stream(this.flags)
                        .filter(flag -> flag.getKey().equals(key))
                        .findFirst()
        ).map(flag -> (Flag<T>) flag);
    }
    @Override
    public boolean has(@NonNls @NotNull String key) {
        return Arrays.stream(SeleneUtils.merge(this.args, this.flags)).anyMatch(arg -> arg.getKey().equals(key));
    }

    @Override
    public CommandSource sender() {
        return this.sender;
    }

    @Override
    public Exceptional<Location> location() {
        return this.location;
    }

    @Override
    public Exceptional<World> world() {
        return this.world;
    }

    @Override
    public String[] permissions() {
        return this.permissions;
    }
}
