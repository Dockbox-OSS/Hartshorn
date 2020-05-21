package com.darwinreforged.server.sponge.implementations;

import com.boydti.fawe.object.FawePlayer;
import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.commands.CommandBus;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.player.PlayerManager;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.tuple.Tuple;
import com.darwinreforged.server.core.types.living.Console;
import com.google.common.collect.Multimap;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;

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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import static com.darwinreforged.server.core.DarwinServer.error;

@SuppressWarnings("unchecked")
public class SpongeCommandBus extends CommandBus<CommandContext, SpongeArgumentTypeValue> {

    private static final Map<String, List<Tuple<String, CommandSpec>>> childsPerAlias = new HashMap<>();

    @Override
    protected SpongeArgumentTypeValue getArgumentValue(String type, String permission, String key) {
        try {
            return new SpongeArgumentTypeValue(type, permission, key);
        } catch (IllegalArgumentException e) {
            return new SpongeArgumentTypeValue(Arguments.OTHER.toString(), permission, key);
        }
    }

    @Override
    public void registerCommandNoArgs(String command, String permission, CommandRunner runner) {
        Sponge.getCommandManager().register(DarwinServer.getServer(), CommandSpec.builder().permission(permission).executor(buildExecutor(runner)).build(), command);
    }

    @Override
    public void registerCommandArgsAndOrChild(String command, String permission, CommandRunner runner) {
        CommandSpec.Builder spec = CommandSpec.builder();
        if (permission != null) spec.permission(permission);
        else spec.permission(Permissions.ADMIN_BYPASS.p());

        String[] parts = command.split(" ");
        String part = parts.length > 1 ? parts[1] : null;
        // Child command
        if (part != null && subcommand.matcher(part).matches()) {
            String arguments = command.substring(command.indexOf(' ') + 1)
                    .replaceFirst(part, "");
            if (arguments.endsWith(" ")) arguments = arguments.substring(0, arguments.length() - 2);

            CommandElement[] elements = parseArguments(arguments);

            elements = Arrays.stream(elements).filter(Objects::nonNull).toArray(CommandElement[]::new);
            if (elements.length > 0) spec.executor(buildExecutor(runner)).arguments(elements);
            else spec.executor(buildExecutor(runner));

            String alias = command.substring(0, command.indexOf(' '));
            if (part.equals("")) part = "@m";
            List<Tuple<String, CommandSpec>> aliases = childsPerAlias.getOrDefault(alias, new ArrayList<>());
            aliases.add(new Tuple<>(part, spec.build()));
            childsPerAlias.put(alias, aliases);

            // Parent command
        } else if (command.startsWith("*")) {
            String registeredCmd = command.substring(1);
            if (command.contains(" ")) registeredCmd = command.substring(1, command.indexOf(' '));
            if (!REGISTERED_COMMANDS.contains(registeredCmd)) {
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
                    error("Could not access executor field", e);
                    spec.executor((src, args) -> CommandResult.success());
                }

                try {
                    Sponge.getCommandManager().register(DarwinServer.getServer(), spec.build(), registeredCmd);
                } catch (IllegalArgumentException e) {
                    error(e.getMessage(), e);
                }
                REGISTERED_COMMANDS.add(registeredCmd);
            }
            // Single method command
        } else {
            if (!REGISTERED_COMMANDS.contains(command.substring(0, command.indexOf(' ')))) {
                spec.executor(buildExecutor(runner)).arguments(parseArguments(command.substring(command.indexOf(' ') + 1)));
                Sponge.getCommandManager().register(DarwinServer.getServer(), spec.build(), command.substring(0, command.indexOf(' ')));
                REGISTERED_COMMANDS.add(command.substring(0, command.indexOf(' ')));
            }

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected com.darwinreforged.server.core.commands.context.CommandContext convertContext(CommandContext ctx) {
        Multimap<String, Object> parsedArgs;
        try {
            Field parsedArgsF = ctx.getClass().getDeclaredField("parsedArgs");
            if (!parsedArgsF.isAccessible()) parsedArgsF.setAccessible(true);
            parsedArgs = (Multimap<String, Object>) parsedArgsF.get(ctx);
        } catch (IllegalAccessException | ClassCastException | NoSuchFieldException e) {
            error("Could not load parsed arguments from Sponge command context", e);
            return null;
        }

        parsedArgs.asMap().forEach(((s, o) -> {
            o.forEach(obj -> {
                // TODO : Parse Darwin specific objects. Native types and FAWE Objects are correctly handled beforehand.
                // Perhaps replace Sponge's default parser with a Darwin specific parser?
                // Items to parse :
                //Entity
                //EntityOrSource
                //Location
                //Player
                //PlayerOrSource
                //User
                //UserOrSource
                //World
            });
        }));

        return null;
    }

    private CommandElement[] parseArguments(String argString) {
        List<CommandElement> elements = new LinkedList<>();
        CommandFlags.Builder cflags = null;
        Matcher m = argFinder.matcher(argString);
        while (m.find()) {
            String part = m.group();
            Matcher ma = argument.matcher(part);
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
                Matcher mf = flag.matcher(part);
                if (mf.matches()) {
                    if (cflags == null) cflags = GenericArguments.flags();
                    parseFlag(cflags, mf.group(1), mf.group(2));
                } else {
                    error("Argument type was not recognized for `" + part + "`");
                }
            }
        }
        if (cflags==null) return elements.toArray(new CommandElement[0]);
        else return new CommandElement[]{ cflags.buildWith(wrap(elements.toArray(new CommandElement[0]))) };
    }

    private void parseFlag(CommandFlags.Builder flags, String name, String value) {
        if (value == null) {
            int at;
            if ((at=name.indexOf(':'))>=0) {
                String a=name.substring(0, at), b=name.substring(at+1);
                flags.permissionFlag(b, a);
            } else {
                flags.flag(name);
            }
        } else {
            ArgumentTypeValue<CommandElement> av = argValue(value);
            if (name.indexOf(':') >= 0) {
                error("Flag values do not support permissions at flag `" + name + "`. Permit the value instead");
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

    private CommandExecutor buildExecutor(CommandRunner runner) {
        return (src, args) -> {
            com.darwinreforged.server.core.commands.context.CommandContext ctx = convertContext(args);
            if (src instanceof Player) {
                DarwinPlayer dp = DarwinServer.getUtilChecked(PlayerManager.class).getPlayer(((Player) src).getUniqueId(), src.getName());
                runner.run(dp, ctx);
            } else if (src instanceof ConsoleSource) {
                runner.run(Console.instance, ctx);
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
                DarwinServer.error("Failed to parse WorldEdit argument", e);
            }
            return null;
        }

        @Override
        public @NotNull List<String> complete(
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
            String id = args.next();
            Object module = DarwinServer.getModule(id);
            if (module.getClass().isAnnotationPresent(Module.class)) return module;
            return null;
        }

        @Override
        public @NotNull List<String> complete(
                @NotNull CommandSource src,
                @NotNull CommandArgs args,
                @NotNull CommandContext context) {
            return DarwinServer.getAllModuleInfo().stream().map(Module::id).collect(Collectors.toList());
        }
    }

}
