package com.darwinreforged.server.sponge.implementations;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.commands.CommandBus;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.tuple.Tuple;
import com.google.common.collect.Multimap;

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
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class SpongeCommandBus extends CommandBus<CommandSource, CommandContext, SpongeArgumentTypeValue> {

    private static final Map<String, List<Tuple<String, CommandSpec>>> childsPerAlias = new HashMap<>();

    @Override
    protected SpongeArgumentTypeValue getArgumentValue(String type, String permission, String key) {
        return new SpongeArgumentTypeValue(type, permission, key);
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

        String part = command.split(" ")[1];
        if (subcommand.matcher(part).matches()) {
            spec.executor(buildExecutor(runner)).arguments(parseArguments(command.substring(command.indexOf(' ' + 1)).replaceFirst(part + ' ', "")));
            String alias = command.substring(command.indexOf(' ') + 1);
            childsPerAlias.putIfAbsent(alias, new ArrayList<>());
            childsPerAlias.get(alias).add(new Tuple<>(part, spec.build()));

        } else if (command.startsWith("*")) {
            List<Tuple<String, CommandSpec>> childs = childsPerAlias.getOrDefault(command.substring(1), new ArrayList<>());
            childs.forEach(child -> spec.child(child.getSecond(), child.getFirst()));
            Sponge.getCommandManager().register(DarwinServer.getServer(), spec.build(), command.substring(1, command.indexOf(' ')));

        } else {
            spec.executor(buildExecutor(runner)).arguments(parseArguments(command.substring(command.indexOf(' ') + 1)));
            Sponge.getCommandManager().register(DarwinServer.getServer(), spec.build(), command.substring(0, command.indexOf(' ')));
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
            DarwinServer.error("Could not load parsed arguments from Sponge command context", e);
            return null;
        }
        parsedArgs.asMap().forEach(((s, o) -> {
            DarwinServer.getLog().info("S:" + s + "  /  O:" + o.getClass().toGenericString());
        }));

        DarwinServer.getLog().info("Finished listing " + parsedArgs.asMap().size() + " args/vals");
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
                CommandElement[] result = parseArguments(ma.group(2));
                if (result.length==0) result = new CommandElement[]{argValue(ma.group(2)).getPermissionArgument()};
                elements.add(optional?GenericArguments.optional(wrap(result)):wrap(result));
            } else {
                Matcher mf = flag.matcher(part);
                if (mf.matches()) {
                    if (cflags == null) cflags = GenericArguments.flags();
                    parseFlag(cflags, mf.group(1), mf.group(2));
                } else {
                    DarwinServer.error("Argument type was not recognized for `"+part+"`");
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
                DarwinServer.error("Flag values do not support permissions at flag `" + name + "`. Permit the value instead");
            }
            flags.valueFlag(av.getPermissionArgument(), name);
        }
    }


    private static CommandElement wrap(CommandElement... elements) {
        if (elements.length == 0) return GenericArguments.none();
        return elements.length == 1 ? elements[0] : GenericArguments.seq(elements);
    }

    private CommandExecutor buildExecutor(CommandRunner runner) {
        return (src, args) -> {
            // TODO : Wrap runner
            DarwinServer.getLog().info("Starting command!");
            com.darwinreforged.server.core.commands.context.CommandContext ctx = convertContext(args);
            // ...
            return CommandResult.success();
        };
    }

    public static class FaweArgument extends CommandElement {

        protected FaweArgument(@Nullable Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(@NotNull CommandSource source, CommandArgs args) throws ArgumentParseException {
            // TODO : Implement parsers for FAWE types
            return null;
        }

        @Override
        public @NotNull List<String> complete(
                @NotNull CommandSource src,
                @NotNull CommandArgs args,
                @NotNull CommandContext context) {
            return null;
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
