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

package org.dockbox.selene.sponge.util.command;

import com.boydti.fawe.object.FawePlayer;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;

import org.dockbox.selene.core.command.CommandRunnerFunction;
import org.dockbox.selene.core.command.context.CommandValue;
import org.dockbox.selene.core.events.chat.CommandEvent;
import org.dockbox.selene.core.i18n.permissions.AbstractPermission;
import org.dockbox.selene.core.impl.command.AbstractArgumentValue;
import org.dockbox.selene.core.impl.command.SimpleCommandBus;
import org.dockbox.selene.core.impl.command.context.SimpleCommandContext;
import org.dockbox.selene.core.objects.events.Cancellable;
import org.dockbox.selene.core.objects.tuple.Tuple;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.core.util.extension.Extension;
import org.dockbox.selene.core.util.extension.ExtensionManager;
import org.dockbox.selene.sponge.objects.targets.SpongeConsole;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.CommandFlags;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
@Singleton
public class SpongeCommandBus extends SimpleCommandBus<CommandContext, SpongeArgumentTypeValue> {

    private static final Map<String, List<Tuple<String, CommandSpec>>> childsPerAlias = new HashMap<>();

    @Override
    protected SpongeArgumentTypeValue getArgumentValue(@NotNull String type, @NotNull AbstractPermission permission, @NotNull String key) {
        try {
            return new SpongeArgumentTypeValue(type, permission.get(), key);
        } catch (IllegalArgumentException e) {
            return new SpongeArgumentTypeValue(Arguments.OTHER.toString(), permission.get(), key);
        }
    }

    @Override
    public void registerCommandNoArgs(@NotNull String command, @NotNull AbstractPermission permission, @NotNull CommandRunnerFunction runner) {
        Sponge.getCommandManager().register(Selene.getServer(), CommandSpec.builder().permission(permission.get()).executor(this.buildExecutor(runner, command)).build(), command);
    }

    @Override
    protected void registerChildCommand(@NotNull String command, @NotNull CommandRunnerFunction runner, String usagePart, AbstractPermission permission) {
        CommandSpec.Builder spec = CommandSpec.builder();
        spec.permission(permission.get());

        Selene.log().debug("Found child command '" + usagePart + "'");
        String arguments = command.substring(command.indexOf(' ') + 1)
                .replaceFirst(usagePart, "");
        if (arguments.endsWith(" ")) arguments = arguments.substring(0, arguments.length() - 2);

        CommandElement[] elements = this.parseArguments(arguments);

        elements = Arrays.stream(elements).filter(Objects::nonNull).toArray(CommandElement[]::new);
        if (0 < elements.length) spec.executor(this.buildExecutor(runner, command)).arguments(elements);
        else spec.executor(this.buildExecutor(runner, command));

        String alias = command.substring(0, command.indexOf(' '));
        List<Tuple<String, CommandSpec>> aliases = childsPerAlias.getOrDefault(alias, new ArrayList<>());
        aliases.add(new Tuple<>(usagePart, spec.build()));
        childsPerAlias.put(alias, aliases);
    }

    @Override
    protected void registerSingleMethodCommand(@NotNull String command, @NotNull CommandRunnerFunction runner, String commandPart, AbstractPermission permission) {
        CommandSpec.Builder spec = CommandSpec.builder();
        spec.permission(permission.get());

        Selene.log().debug("Found single method command '" + commandPart + "'");
        if (!SimpleCommandBus.Companion.getRegisteredCommands().contains(command.substring(0, command.indexOf(' ')))) {
            Selene.log().debug("Registering single method command '" + commandPart + "' to Sponge");
            spec.executor(this.buildExecutor(runner, command)).arguments(this.parseArguments(command.substring(command.indexOf(' ') + 1)));
            Sponge.getCommandManager().register(Selene.getServer(), spec.build(), command.substring(0, command.indexOf(' ')));
            SimpleCommandBus.Companion.getRegisteredCommands().add(command.substring(0, command.indexOf(' ')));
        }
    }

    @Override
    @SuppressWarnings("CallToSuspiciousStringMethod")
    protected void registerParentCommand(@NotNull String command, @NotNull CommandRunnerFunction runner, AbstractPermission permission) {
        CommandSpec.Builder spec = CommandSpec.builder();
        spec.permission(permission.get());

        String registeredCmd = command.substring(1);
        if (command.contains(" ")) registeredCmd = command.substring(1, command.indexOf(' '));
        if (!SimpleCommandBus.Companion.getRegisteredCommands().contains(registeredCmd)) {
            List<Tuple<String, CommandSpec>> childs = childsPerAlias.getOrDefault(registeredCmd, new ArrayList<>());
            childs.forEach(child -> {
                if (super.getParentCommandPrefix().equals(child.getFirst())) {
                    spec.executor(child.getSecond().getExecutor());
                } else {
                    spec.child(child.getSecond(), child.getFirst());
                }
            });

            spec.executor(this.buildExecutor(runner, command));

            try {
                Selene.log().debug("Registering '" + registeredCmd + "' to Sponge");
                Sponge.getCommandManager().register(Selene.getServer(), spec.build(), registeredCmd);
            } catch (IllegalArgumentException e) {
                Selene.getServer().except(e.getMessage(), e);
            }
            SimpleCommandBus.Companion.getRegisteredCommands().add(registeredCmd);
        }
    }

    private Object getValue(Object obj) {
        Optional<?> oo = SpongeConversionUtil.autoDetectFromSponge(obj);
        return oo.isPresent() ? oo.get() : obj; // oo.orElse() cannot be cast due to generic ? type
    }

    private CommandElement[] parseArguments(CharSequence argString) {
        List<CommandElement> elements = new LinkedList<>();
        CommandFlags.Builder cflags = null;
        Matcher m = Companion.getArgFinder().matcher(argString);
        while (m.find()) {
            String part = m.group();
            Matcher ma = Companion.getArgument().matcher(part);
            if (ma.matches()) {
                boolean optional = '[' == ma.group(1).charAt(0);
                String argUnp = ma.group(2);
                CommandElement[] result = this.parseArguments(argUnp);
                if (0 == result.length) {
                    SpongeArgumentTypeValue satv = this.argValue(ma.group(2));
                    CommandElement permEl = satv.getArgument();
                    result = new CommandElement[]{permEl};
                }
                CommandElement el = wrap(result);
                if (optional) {
                    elements.add(GenericArguments.optional(el));
                } else {
                    elements.add(el);
                }
            } else {
                Matcher mf = Companion.getFlag().matcher(part);
                if (mf.matches()) {
                    if (null == cflags) cflags = GenericArguments.flags();
                    this.parseFlag(cflags, mf.group(1), mf.group(2));
                } else {
                    Selene.getServer().except("Argument type was not recognized for `" + part + "`");
                }
            }
        }
        if (null == cflags) return elements.toArray(new CommandElement[0]);
        else return new CommandElement[]{cflags.buildWith(wrap(elements.toArray(new CommandElement[0])))};
    }

    private void parseFlag(CommandFlags.Builder flags, String name, String value) {
        if (null == value) {
            int at;
            if (0 <= (at = name.indexOf(':'))) {
                String a = name.substring(0, at), b = name.substring(at + 1);
                flags.permissionFlag(b, a);
            } else {
                flags.flag(name);
            }
        } else {
            AbstractArgumentValue<CommandElement> av = this.argValue(value);
            if (0 <= name.indexOf(':')) {
                Selene.getServer().except("Flag values do not support permissions at flag `" + name + "`. Permit the value instead");
            }
            flags.valueFlag(av.getArgument(), name);
        }
    }


    private static CommandElement wrap(CommandElement... elements) {
        if (0 == elements.length) {
            return GenericArguments.none();
        } else if (1 == elements.length) {
            return elements[0];
        } else {
            return GenericArguments.seq(elements);
        }
    }

    private CommandExecutor buildExecutor(CommandRunnerFunction runner, String command) {
        return (src, args) -> {
            org.dockbox.selene.core.objects.targets.CommandSource sender = SpongeConversionUtil
                    .fromSponge(src)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Command sender is not a console or a player, did a plugin call me?"));

            SimpleCommandContext ctx = this.convertContext(args, sender, command);

            EventBus eb = Selene.getInstance(EventBus.class);
            Cancellable ceb = new CommandEvent.Before(sender, ctx);
            eb.post(ceb);

            if (!ceb.isCancelled()) {
                if (src instanceof Player) {
                    runner.run(sender, ctx);
                } else {
                    runner.run(SpongeConsole.Companion.getInstance(), ctx);
                }

                eb.post(new CommandEvent.After(sender, ctx));
            }

            return CommandResult.success();
        };
    }

    @NotNull
    @Override
    protected SimpleCommandContext convertContext(CommandContext ctx,
                                                  @NotNull org.dockbox.selene.core.objects.targets.CommandSource sender,
                                                  @org.jetbrains.annotations.Nullable String command) {
        Multimap<String, Object> parsedArgs;
        try {
            Field parsedArgsF = ctx.getClass().getDeclaredField("parsedArgs");
            if (!parsedArgsF.isAccessible()) parsedArgsF.setAccessible(true);
            parsedArgs = (Multimap<String, Object>) parsedArgsF.get(ctx);
        } catch (IllegalAccessException | ClassCastException | NoSuchFieldException e) {
            Selene.getServer().except("Could not load parsed arguments from Sponge command context", e);
            return SimpleCommandContext.Companion.getEMPTY();
        }

        List<CommandValue.Argument<?>> arguments = new ArrayList<>();
        List<CommandValue.Flag<?>> flags = new ArrayList<>();

        assert null != command : "Context carrier command was null";
        parsedArgs.asMap().forEach((s, o) -> o.forEach(obj -> {
            if (Pattern.compile("-(-?" + s + ")").matcher(command).find())
                flags.add(new CommandValue.Flag<>(this.getValue(obj), s));
            else arguments.add(new CommandValue.Argument<>(this.getValue(obj), s));
        }));

        return this.getContext(sender, arguments, flags, command);
    }

    @NotNull
    private SimpleCommandContext getContext(org.dockbox.selene.core.objects.targets.@NotNull CommandSource sender, List<CommandValue.Argument<?>> arguments, List<CommandValue.Flag<?>> flags, String command) {
        SimpleCommandContext seleneCtx;
        if (sender instanceof org.dockbox.selene.core.objects.user.Player) {
            org.dockbox.selene.core.objects.location.Location loc = ((org.dockbox.selene.core.objects.user.Player) sender).getLocation();
            org.dockbox.selene.core.objects.location.World world = ((org.dockbox.selene.core.objects.user.Player) sender).getLocation().getWorld();
            seleneCtx = new SimpleCommandContext(
                    command,
                    arguments.toArray(new CommandValue.Argument<?>[0]),
                    flags.toArray(new CommandValue.Flag<?>[0]),
                    sender, Optional.of(loc), Optional.of(world),
                    new String[0]
            );
        } else {
            seleneCtx = new SimpleCommandContext(
                    command,
                    arguments.toArray(new CommandValue.Argument<?>[0]),
                    flags.toArray(new CommandValue.Flag<?>[0]),
                    sender, Optional.empty(), Optional.empty(),
                    new String[0]
            );
        }
        return seleneCtx;
    }

    public static class FaweArgument extends CommandElement {

        enum FaweTypes {
            REGION, EDIT_SESSION, PATTERN, MASK
        }

        private final FaweTypes type;

        FaweArgument(@Nullable Text key, FaweTypes type) {
            super(key);
            this.type = type;
        }

        @Nullable
        @Override
        protected Object parseValue(@NotNull CommandSource source, @NotNull CommandArgs args) {
            try {
                FawePlayer<?> fawePlayer = FawePlayer.wrap(source);
                ParserContext pctx = new ParserContext();
                pctx.setActor(fawePlayer.getPlayer());
                pctx.setWorld(fawePlayer.getWorld());
                pctx.setSession(fawePlayer.getSession());

                switch (this.type) {
                    case REGION:
                        return fawePlayer.getSelection();
                    case EDIT_SESSION:
                        return fawePlayer.getNewEditSession();
                    case PATTERN:
                        String patternRaw = args.getRaw();
                        return WorldEdit.getInstance().getPatternFactory().parseFromInput(patternRaw, pctx);
                    case MASK:
                        String maskRaw = args.getRaw();
                        return WorldEdit.getInstance().getMaskFactory().parseFromInput(maskRaw, pctx);
                }
            } catch (Throwable e) {
                Selene.getServer().except("Failed to parse WorldEdit argument", e);
            }
            return null;
        }

        @NotNull
        @Override
        public List<String> complete(
                @NotNull CommandSource src,
                @NotNull CommandArgs args,
                @NotNull CommandContext context) {
            return new ArrayList<>();
        }
    }

    public static class ExtensionArgument extends CommandElement {

        ExtensionArgument(@Nullable Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(@NotNull CommandSource source, CommandArgs args) throws ArgumentParseException {
            Optional<Extension> octx = Selene.getInstance(ExtensionManager.class).getHeader(args.next());
            return octx.orElse(null);
        }

        @NotNull
        @Override
        public List<String> complete(
                @NotNull CommandSource src,
                @NotNull CommandArgs args,
                @NotNull CommandContext context) {
            return Selene.getInstance(ExtensionManager.class).getRegisteredExtensionIds();
        }
    }

}
