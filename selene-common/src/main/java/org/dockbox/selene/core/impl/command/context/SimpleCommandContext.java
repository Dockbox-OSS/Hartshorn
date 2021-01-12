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

import org.dockbox.selene.core.annotations.command.FromSource;
import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.command.context.CommandValue;
import org.dockbox.selene.core.command.context.CommandValue.Argument;
import org.dockbox.selene.core.command.context.CommandValue.Flag;
import org.dockbox.selene.core.command.parsing.TypeParser;
import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.targets.Locatable;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.Reflect;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public String getAlias() {
        return this.usage.split(" ")[0];
    }

    @Override
    public int getArgumentCount() {
        return this.args.length;
    }

    @Override
    public int getFlagCount() {
        return this.flags.length;
    }

    @NotNull
    @Override
    public Exceptional<Argument<String>> getArgument(@NonNls @NotNull String key) {
        return Exceptional.of(
                Arrays.stream(this.args)
                        .filter(arg -> arg.getKey().equals(key))
                        .findFirst()
        ).map(arg -> new Argument<>(arg.getValue().toString(), arg.getKey()));
    }

    @NotNull
    @Override
    public <T> Exceptional<Argument<T>> getArgument(@NonNls @NotNull String key, @NotNull Class<T> type) {
        return Exceptional.of(
                Arrays.stream(this.args)
                        .filter(arg -> arg.getKey().equals(key))
                        .findFirst()
        ).map(arg -> (Argument<T>) arg);
    }

    @NotNull
    @Override
    public <T> Exceptional<T> getArgumentAndParse(@NotNull String key, @NotNull TypeParser<T> parser) {
        Exceptional<Argument<String>> optionalArg = this.getArgument(key);
        return optionalArg.map(parser::parse).map(Exceptional::orNull);
    }

    @NotNull
    @Override
    public Exceptional<Flag<String>> getFlag(@NonNls @NotNull String key) {
        return Exceptional.of(
                Arrays.stream(this.flags)
                        .filter(flag -> flag.getKey().equals(key))
                        .findFirst()
        ).map(flag -> new Flag<>(flag.getValue().toString(), flag.getKey()));
    }

    @NotNull
    @Override
    public <T> Exceptional<Flag<T>> getFlag(@NonNls @NotNull String key, @NotNull Class<T> type) {
        return Exceptional.of(
                Arrays.stream(this.flags)
                        .filter(flag -> flag.getKey().equals(key))
                        .findFirst()
        ).map(flag -> (Flag<T>) flag);
    }

    @NotNull
    @Override
    public <T> Exceptional<T> getFlagAndParse(@NotNull String key, @NotNull TypeParser<T> parser) {
        @NotNull Exceptional<Flag<String>> optionalFlag = this.getFlag(key);
        return optionalFlag.map(parser::parse).map(Exceptional::orNull);
    }

    @Override
    public boolean hasArgument(@NonNls @NotNull String key) {
        return Arrays.stream(this.args).anyMatch(arg -> arg.getKey().equals(key));
    }

    @Override
    public boolean hasFlag(@NonNls @NotNull String key) {
        return Arrays.stream(this.flags).anyMatch(flag -> flag.getKey().equals(key));
    }

    @NotNull
    @Override
    public <T> Exceptional<CommandValue<T>> getValue(@NotNull String key, @NotNull Class<T> type, CommandValue.@NotNull Type valType) {
        CommandValue<?>[] arr = new CommandValue[0];
        switch (valType) {
            case ARGUMENT:
                arr = SeleneUtils.shallowCopy(this.args);
                break;
            case FLAG:
                arr = SeleneUtils.shallowCopy(this.flags);
                break;
            case BOTH:
                arr = SeleneUtils.addAll(
                        SeleneUtils.shallowCopy(this.args),
                        SeleneUtils.shallowCopy(this.flags));
                break;
        }
        return this.getValueAs(key, type, arr);
    }

    private <T, A extends CommandValue<T>> Exceptional<A> getValueAs(@NonNls String key, Class<T> type, CommandValue<?>[] values) {
        Exceptional<CommandValue<?>> candidate = Exceptional.of(Arrays.stream(values)
                .filter(val -> val.getKey().equals(key))
                .findFirst()
        );
        if (candidate.isPresent()) {
            CommandValue<?> cv = candidate.get();
            if (Reflect.isAssignableFrom(type, cv.getValue().getClass())) return Exceptional.of((A) cv);
        }
        return Exceptional.empty();
    }

    @NotNull
    @Override
    public <T> Exceptional<T> tryCreate(@NotNull Class<T> type) {
        Map<String, Object> values = SeleneUtils.emptyMap();
        for (Argument<?> arg : this.args) values.put(arg.getKey(), arg.getValue());
        for (Flag<?> flag : this.flags) values.put(flag.getKey(), flag.getValue());

        return Reflect.tryCreateFromRaw(type, field -> {
            if (field.isAnnotationPresent(FromSource.class)) {
                if (Reflect.isAssignableFrom(Player.class, field.getType())) {
                    if (this.sender instanceof Player) return this.sender;
                } else if (Reflect.isAssignableFrom(World.class, field.getType())) {
                    if (this.sender instanceof Locatable) return this.world;
                } else if (Reflect.isAssignableFrom(Location.class, field.getType())) {
                    if (this.sender instanceof Locatable) return this.location;
                } else if (Reflect.isAssignableFrom(CommandSource.class, field.getType())) {
                    return this.sender;
                } else {
                    Selene.log().warn("Field '" + field.getName() + "' has @FromSource annotation but cannot be provided [" + field.getType().getCanonicalName() + "]");
                }
            }
            return values.getOrDefault(Reflect.getFieldPropertyName(field), null);
        }, true);
    }

    public String getUsage() {
        return this.usage;
    }

    public @UnmodifiableView @NotNull List<Argument<?>> getArgs() {
        return SeleneUtils.asUnmodifiableList(this.args);
    }

    public @UnmodifiableView @NotNull List<Flag<?>> getFlags() {
        return SeleneUtils.asUnmodifiableList(this.flags);
    }

    public @UnmodifiableView @NotNull List<String> getPermissions() {
        return SeleneUtils.asUnmodifiableList(this.permissions);
    }

    public CommandSource getSender() {
        return this.sender;
    }

    public Exceptional<Location> getLocation() {
        return this.location;
    }

    public Exceptional<World> getWorld() {
        return this.world;
    }
}
