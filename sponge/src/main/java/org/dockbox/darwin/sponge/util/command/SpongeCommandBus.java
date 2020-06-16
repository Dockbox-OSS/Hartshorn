package org.dockbox.darwin.sponge.util.command;

import com.boydti.fawe.object.FawePlayer;
import com.google.common.collect.Multimap;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;

import org.dockbox.darwin.core.command.AbstractArgumentValue;
import org.dockbox.darwin.core.command.CommandRunnerFunction;
import org.dockbox.darwin.core.command.SimpleCommandBus;
import org.dockbox.darwin.core.command.context.CommandValue;
import org.dockbox.darwin.core.objects.module.ModuleInformation;
import org.dockbox.darwin.core.objects.tuple.Tuple;
import org.dockbox.darwin.core.objects.tuple.Vector3D;
import org.dockbox.darwin.core.server.CoreServer;
import org.dockbox.darwin.core.util.module.ModuleLoader;
import org.dockbox.darwin.sponge.objects.location.SpongeLocation;
import org.dockbox.darwin.sponge.objects.location.SpongePlayer;
import org.dockbox.darwin.sponge.objects.location.SpongeWorld;
import org.dockbox.darwin.sponge.objects.targets.SpongeConsole;
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
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class SpongeCommandBus extends SimpleCommandBus<CommandContext, SpongeArgumentTypeValue> {

    private static final Map<String, List<Tuple<String, CommandSpec>>> childsPerAlias = new HashMap<>();

    @Override
    protected SpongeArgumentTypeValue getArgumentValue(@NotNull String type, String permission, @NotNull String key) {
        try {
            return new SpongeArgumentTypeValue(type, permission, key);
        } catch (IllegalArgumentException e) {
            return new SpongeArgumentTypeValue(Arguments.OTHER.toString(), permission, key);
        }
    }

    @Override
    public void registerCommandNoArgs(@NotNull String command, String permission, @NotNull CommandRunnerFunction runner) {
        Sponge.getCommandManager().register(CoreServer.getServer(), CommandSpec.builder().permission(permission).executor(buildExecutor(runner, command)).build(), command);
    }

    @Override
    public void registerCommandArgsAndOrChild(@NotNull String command, String permission, @NotNull CommandRunnerFunction runner) {
        CommandSpec.Builder spec = CommandSpec.builder();
        if (permission != null) spec.permission(permission);
        else spec.permission("admin.bypass"); // TODO

        String[] parts = command.split(" ");
        String part = parts.length > 1 ? parts[1] : null;
        // Child command
        if (part != null && SimpleCommandBus.Companion.getSubcommand().matcher(part).matches()) {
            String arguments = command.substring(command.indexOf(' ') + 1)
                    .replaceFirst(part, "");
            if (arguments.endsWith(" ")) arguments = arguments.substring(0, arguments.length() - 2);

            CommandElement[] elements = parseArguments(arguments);

            elements = Arrays.stream(elements).filter(Objects::nonNull).toArray(CommandElement[]::new);
            if (elements.length > 0) spec.executor(buildExecutor(runner, command)).arguments(elements);
            else spec.executor(buildExecutor(runner, command));

            String alias = command.substring(0, command.indexOf(' '));
            if (part.equals("")) part = "@m";
            List<Tuple<String, CommandSpec>> aliases = childsPerAlias.getOrDefault(alias, new ArrayList<>());
            aliases.add(new Tuple<>(part, spec.build()));
            childsPerAlias.put(alias, aliases);

            // Parent command
        } else if (command.startsWith("*")) {
            String registeredCmd = command.substring(1);
            if (command.contains(" ")) registeredCmd = command.substring(1, command.indexOf(' '));
            if (!SimpleCommandBus.Companion.getRegisteredCommands().contains(registeredCmd)) {
                List<Tuple<String, CommandSpec>> childs = childsPerAlias.getOrDefault(registeredCmd, new ArrayList<>());
                childs.forEach(child -> {
                    if (child.getFirst().equals("@m")) {
                        spec.executor(child.getSecond().getExecutor());
                    } else {
                        spec.child(child.getSecond(), child.getFirst());
                    }
                });

                try {
                    Field executorF = spec.getClass().getDeclaredField("executor");
                    executorF.setAccessible(true);
                    if (executorF.get(spec) == null) spec.executor((src, args) -> CommandResult.success());
                } catch (Throwable e) {
                    CoreServer.getServer().except("Could not access executor field", e);
                    spec.executor((src, args) -> CommandResult.success());
                }

                try {
                    Sponge.getCommandManager().register(CoreServer.getServer(), spec.build(), registeredCmd);
                } catch (IllegalArgumentException e) {
                    CoreServer.getServer().except(e.getMessage(), e);
                }
                SimpleCommandBus.Companion.getRegisteredCommands().add(registeredCmd);
            }
            // Single method command
        } else {
            if (!SimpleCommandBus.Companion.getRegisteredCommands().contains(command.substring(0, command.indexOf(' ')))) {
                spec.executor(buildExecutor(runner, command)).arguments(parseArguments(command.substring(command.indexOf(' ') + 1)));
                Sponge.getCommandManager().register(CoreServer.getServer(), spec.build(), command.substring(0, command.indexOf(' ')));
                SimpleCommandBus.Companion.getRegisteredCommands().add(command.substring(0, command.indexOf(' ')));
            }

        }
    }

    private Object getValue(Object obj) {
        if (obj instanceof User) {
            return new SpongePlayer(((User) obj).getUniqueId(), ((User) obj).getName());

        } else if (obj instanceof Entity) {
            // TODO : Regular Entity wrapper
            throw new UnsupportedOperationException();

        } else if (obj instanceof Location) {
            Location<Extent> sloc = (Location<Extent>) obj;
            World sworld = (World) sloc.getExtent();

            Vector3D locv = new Vector3D(sloc.getX(), sloc.getY(), sloc.getZ());
            org.dockbox.darwin.core.objects.location.World dworld = new SpongeWorld(sworld.getUniqueId(), sworld.getName());
            return new SpongeLocation(locv, dworld);

        } else if (obj instanceof World) {
            return new SpongeWorld(((World) obj).getUniqueId(), ((World) obj).getName());

        } else {
            return obj;
        }
    }

    @NotNull
    @Override
    protected org.dockbox.darwin.core.command.context.CommandContext convertContext(CommandContext ctx, org.dockbox.darwin.core.objects.targets.@NotNull CommandSource sender, String command) {
        Multimap<String, Object> parsedArgs;
        try {
            Field parsedArgsF = ctx.getClass().getDeclaredField("parsedArgs");
            if (!parsedArgsF.isAccessible()) parsedArgsF.setAccessible(true);
            parsedArgs = (Multimap<String, Object>) parsedArgsF.get(ctx);
        } catch (IllegalAccessException | ClassCastException | NoSuchFieldException e) {
            CoreServer.getServer().except("Could not load parsed arguments from Sponge command context", e);
            return null;
        }

        List<CommandValue.Argument<?>> arguments = new ArrayList<>();
        List<CommandValue.Flag<?>> flags = new ArrayList<>();

        parsedArgs.asMap().forEach((s, o) -> o.forEach(obj -> {
            if (Pattern.compile("-(-?" + s + ")").matcher(command).find())
                flags.add(new CommandValue.Flag<>(getValue(obj), s));
            else arguments.add(new CommandValue.Argument<>(getValue(obj), s));
        }));

        org.dockbox.darwin.core.command.context.CommandContext darwinCtx;
        if (sender instanceof org.dockbox.darwin.core.objects.user.Player) {
            org.dockbox.darwin.core.objects.location.Location loc = ((org.dockbox.darwin.core.objects.user.Player) sender).getLocation();
            org.dockbox.darwin.core.objects.location.World world = ((org.dockbox.darwin.core.objects.user.Player) sender).getLocation().getWorld();
            darwinCtx = new org.dockbox.darwin.core.command.context.CommandContext(
                    arguments.toArray(new CommandValue.Argument<?>[0]),
                    flags.toArray(new CommandValue.Flag<?>[0]),
                    sender, loc, world,
                    new String[0]
            );
        } else {
            darwinCtx = new org.dockbox.darwin.core.command.context.CommandContext(
                    arguments.toArray(new CommandValue.Argument<?>[0]),
                    flags.toArray(new CommandValue.Flag<?>[0]),
                    sender, null, null,
                    new String[0]
            );
        }
        return darwinCtx;
    }

    private CommandElement[] parseArguments(String argString) {
        List<CommandElement> elements = new LinkedList<>();
        CommandFlags.Builder cflags = null;
        Matcher m = Companion.getArgFinder().matcher(argString);
        while (m.find()) {
            String part = m.group();
            Matcher ma = Companion.getArgument().matcher(part);
            if (ma.matches()) {
                boolean optional = ma.group(1).charAt(0) == '[';
                String argUnp = ma.group(2);
                CommandElement[] result = parseArguments(argUnp);
                if (result.length == 0) {
                    SpongeArgumentTypeValue satv = argValue(ma.group(2));
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
                    if (cflags == null) cflags = GenericArguments.flags();
                    parseFlag(cflags, mf.group(1), mf.group(2));
                } else {
                    CoreServer.getServer().except("Argument type was not recognized for `" + part + "`");
                }
            }
        }
        if (cflags == null) return elements.toArray(new CommandElement[0]);
        else return new CommandElement[]{cflags.buildWith(wrap(elements.toArray(new CommandElement[0])))};
    }

    private void parseFlag(CommandFlags.Builder flags, String name, String value) {
        if (value == null) {
            int at;
            if ((at = name.indexOf(':')) >= 0) {
                String a = name.substring(0, at), b = name.substring(at + 1);
                flags.permissionFlag(b, a);
            } else {
                flags.flag(name);
            }
        } else {
            AbstractArgumentValue<CommandElement> av = argValue(value);
            if (name.indexOf(':') >= 0) {
                CoreServer.getServer().except("Flag values do not support permissions at flag `" + name + "`. Permit the value instead");
            }
            flags.valueFlag(av.getArgument(), name);
        }
    }


    private static CommandElement wrap(CommandElement... elements) {
        if (elements.length == 0) {
            return GenericArguments.none();
        } else if (elements.length == 1) {
            return elements[0];
        } else {
            return GenericArguments.seq(elements);
        }
    }

    private CommandExecutor buildExecutor(CommandRunnerFunction runner, String command) {
        return (src, args) -> {
            org.dockbox.darwin.core.objects.targets.CommandSource sender;
            if (src instanceof Player) sender = new SpongePlayer(((Player) src).getUniqueId(), src.getName());
            else if (src instanceof ConsoleSource) sender = SpongeConsole.instance;
            else sender = null;

            org.dockbox.darwin.core.command.context.CommandContext ctx = convertContext(args, sender, command);

            if (src instanceof Player) {
                runner.run(sender, ctx);
            } else if (src instanceof ConsoleSource) {
                runner.run(SpongeConsole.instance, ctx);
            } else {
                src.sendMessage(Text.of(TextColors.RED, "Could not determine if you are a player or console, what are you?"));
            }
            return CommandResult.success();
        };
    }

    public static class FaweArgument extends CommandElement {

        enum FaweTypes {
            REGION, EDIT_SESSION, PATTERN, MASK
        }

        private final FaweTypes type;

        protected FaweArgument(@Nullable Text key, FaweTypes type) {
            super(key);
            this.type = type;
        }

        @Nullable
        @Override
        protected Object parseValue(@NotNull CommandSource source, @NotNull CommandArgs args) throws ArgumentParseException {
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
                CoreServer.getServer().except("Failed to parse WorldEdit argument", e);
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

    public static class ModuleArgument extends CommandElement {

        protected ModuleArgument(@Nullable Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(@NotNull CommandSource source, CommandArgs args) throws ArgumentParseException {
            ModuleInformation module = CoreServer.getInstance(ModuleLoader.class).getModuleInformation(args.next());
            return module.getModule();
        }

        @NotNull
        @Override
        public List<String> complete(
                @NotNull CommandSource src,
                @NotNull CommandArgs args,
                @NotNull CommandContext context) {
            return StreamSupport.stream(
                    CoreServer.getInstance(ModuleLoader.class).getAllRegistrations().spliterator(),
                    false
            ).map(reg -> reg.getInformation().getModule().id())
                    .collect(Collectors.toList());
        }
    }

}
