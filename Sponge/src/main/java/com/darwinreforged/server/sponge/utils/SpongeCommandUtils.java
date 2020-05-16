package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.commands.CommandBus.CommandRunner;
import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.player.PlayerManager;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.living.Console;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.util.CommandUtils;
import com.darwinreforged.server.core.util.LocationUtils;
import com.google.common.collect.Multimap;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.CommandFlags;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author dags <dags@dags.me>
 */
@UtilityImplementation(CommandUtils.class)
public class SpongeCommandUtils extends CommandUtils<CommandSource, CommandContext> {

    @Override
    @Deprecated
    public void executeCommand(CommandSender sender, String command) {
        if (sender instanceof DarwinPlayer) {
            Sponge.getServer().getPlayer(sender.getUniqueId()).ifPresent(p -> Sponge.getCommandManager().process(p, command));
        } else if (sender instanceof Console) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
        }
    }

    @Override
    @Deprecated
    public boolean handleCommandSend(CommandSource source, String command) {
        boolean cancel = false;
        if (source instanceof Player) {
            DarwinPlayer player = PlayerManager.getPlayer(((Player) source).getUniqueId(), source.getName());
            DarwinLocation loc = player.getLocation().orElseGet(LocationUtils::getEmptyWorld);
            cancel = getBus().process(command, player, loc);
        } else if (source instanceof ConsoleSource) {
            cancel = getBus().process(command, Console.instance, LocationUtils.getEmptyWorld());
        }
        return cancel;
    }

    @Override
    public void registerSingleCommand(String command, String permission, CommandRunner runner) {
        Sponge.getCommandManager().register(DarwinServer.getServer(), CommandSpec.builder().permission(permission).executor(buildExecutor(runner)).build(), command);
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
            DarwinServer.getLog().info("S:"+s + "  /  O:" + o.getClass().toGenericString());
        }));

        DarwinServer.getLog().info("Finished listing " + parsedArgs.asMap().size() + " args/vals");
        return null;
    }

    @Override
    public void registerCommandWithSubs(String command, String permission, CommandRunner runner) {
        CommandSpec.Builder spec = CommandSpec.builder();
        if (permission != null) spec.permission(permission);
        else spec.permission(Permissions.ADMIN_BYPASS.p());
        spec.executor(buildExecutor(runner)).arguments(parseArguments(command.substring(command.indexOf(' ')+1)));
        Sponge.getCommandManager().register(DarwinServer.getServer(), spec.build(), command.substring(0, command.indexOf(' ')));
    }

    private static CommandElement[] parseArguments(String argString) {
        List<CommandElement> elements = new LinkedList<>();
        CommandFlags.Builder cflags = null;
        Matcher m = argFinder.matcher(argString);
        while (m.find()) {
            String part = m.group();
            Matcher ma = argument.matcher(part);
            if (ma.matches()) {
                boolean optional=ma.group(1).charAt(0) == '[';
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

    private static void parseFlag(CommandFlags.Builder flags, String name, String value) {
        if (value == null) {
            int at;
            if ((at=name.indexOf(':'))>=0) {
                String a=name.substring(0, at), b=name.substring(at+1);
                flags.permissionFlag(b, a);
            } else {
                flags.flag(name);
            }
        } else {
            ArgumentValue av = argValue(value);
            if (name.indexOf(':')>=0) {
                DarwinServer.error("Flag values do not support permissions at flag `"+name+"`. Permit the value instead");
            }
            flags.valueFlag(av.getPermissionArgument(), name);
        }
    }

    private static class ArgumentValue {
        CommandElement element; String permission;
        public ArgumentValue(CommandElement element, String permission) { this.element = element; this.permission = permission; }
        public CommandElement getPermissionArgument() { return permission==null?element:GenericArguments.requiringPermission(element, permission); }
    }

    private static ArgumentValue argValue(String valueString) {
        String type;
        String key;
        String permission;
        Matcher vm = value.matcher(valueString);
        if (!vm.matches()) DarwinServer.error("Unknown argument specification `"+valueString+"`, use Type or Name{Type} or Name{Type:Permission}");
        key = vm.group(1);
        type = vm.group(2);
        permission = vm.group(3);
        if (type==null) type=key;

        switch(type.toLowerCase()) {
            case "bool":
                return new ArgumentValue(GenericArguments.bool(Text.of(key)), permission);
            case "double":
                return new ArgumentValue(GenericArguments.doubleNum(Text.of(key)), permission);
            case "entity":
                return new ArgumentValue(GenericArguments.entity(Text.of(key)), permission);
            case "entityorsrouce":
                return new ArgumentValue(GenericArguments.entityOrSource(Text.of(key)), permission);
            case "integer":
                return new ArgumentValue(GenericArguments.integer(Text.of(key)), permission);
            case "location":
                return new ArgumentValue(GenericArguments.location(Text.of(key)), permission);
            case "long":
                return new ArgumentValue(GenericArguments.longNum(Text.of(key)), permission);
            case "player":
                return new ArgumentValue(GenericArguments.player(Text.of(key)), permission);
            case "playerorsource":
                return new ArgumentValue(GenericArguments.playerOrSource(Text.of(key)), permission);
            case "plugin":
                return new ArgumentValue(GenericArguments.plugin(Text.of(key)), permission);
            case "remainingstring":
                return new ArgumentValue(GenericArguments.remainingJoinedStrings(Text.of(key)), permission);
            case "string":
                return new ArgumentValue(GenericArguments.string(Text.of(key)), permission);
            case "user":
                return new ArgumentValue(GenericArguments.user(Text.of(key)), permission);
            case "userorsource":
                return new ArgumentValue(GenericArguments.userOrSource(Text.of(key)), permission);
            case "uuid":
                return new ArgumentValue(GenericArguments.uuid(Text.of(key)), permission);
            case "vector":
                return new ArgumentValue(GenericArguments.vector3d(Text.of(key)), permission);
            case "world":
                return new ArgumentValue(GenericArguments.world(Text.of(key)), permission);
            default:
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends CatalogType> clazz = (Class<? extends CatalogType>) Class.forName(type);
                    return new ArgumentValue(GenericArguments.catalogedElement(Text.of(key), clazz), permission);
                } catch (Exception e) {
                    DarwinServer.error("No argument of type `"+type+"` can be read", e);
                    return null;
                }
        }
    }

    private static CommandElement wrap(CommandElement... elements) {
        if (elements.length==0) return GenericArguments.none();
        return elements.length==1?elements[0]:GenericArguments.seq(elements);
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


}
