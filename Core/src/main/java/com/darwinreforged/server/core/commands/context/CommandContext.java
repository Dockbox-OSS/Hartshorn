package com.darwinreforged.server.core.commands.context;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.commands.context.parsing.ArgumentParser;
import com.darwinreforged.server.core.commands.context.parsing.FunctionalParser;
import com.darwinreforged.server.core.commands.context.parsing.TypeArgumentParser;
import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.player.PlayerManager;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.util.LocationUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 The type Command context.
 */
public class CommandContext {

    private static final EnumArgumentParser ENUM_ARGUMENT_PARSER = new EnumArgumentParser();

    private final CommandArgument<?>[] args; // Done
    private final CommandFlag<?>[] flags; // Done
    private final CommandSender sender; // Done
    private final DarwinLocation location; // Done
    private final DarwinWorld world;
    private final Permissions[] permissions;

    /**
     Instantiates a new Command context.

     @param args
     the args
     @param sender
     the sender
     @param world
     the world
     @param location
     the location
     @param permissions
     the permissions
     @param flags
     the flags
     */
    public CommandContext(CommandArgument<?>[] args, CommandSender sender, DarwinWorld world, DarwinLocation location, Permissions[] permissions, CommandFlag<?>[] flags) {
        this.args = args;
        this.sender = sender;
        this.world = world;
        this.location = location;
        this.permissions = permissions;
        this.flags = flags;
    }

    /**
     Get args command argument [ ].

     @return the command argument [ ]
     */
    public CommandArgument<?>[] getArgs() {
        return args;
    }

    /**
     Gets sender.

     @return the sender
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     Gets world.

     @return the world
     */
    public DarwinWorld getWorld() {
        return world;
    }

    /**
     Gets location.

     @return the location
     */
    public DarwinLocation getLocation() {
        return location;
    }

    /**
     Get permissions permissions [ ].

     @return the permissions [ ]
     */
    public Permissions[] getPermissions() {
        return permissions;
    }

    /**
     Get flags command flag [ ].

     @return the command flag [ ]
     */
    public CommandFlag<?>[] getFlags() {
        return flags;
    }

    /**
     Gets argument count.

     @return the argument count
     */
    public int getArgumentCount() {
        return args.length;
    }

    /**
     Gets flag count.

     @return the flag count
     */
    public int getFlagCount() {
        return flags.length;
    }

    /**
     Gets string argument.

     @param index
     the index

     @return the string argument
     */
// Native argument parsers
    public Optional<CommandArgument<String>> getStringArgument(int index) {
        CommandArgument<?> value = args[index];
        if (value != null) return Optional.of(new CommandArgument<>(value.getValue().toString(), value.isJoined(), value.getKey()));
        return Optional.empty();
    }

    /**
     Gets bool argument.

     @param index
     the index

     @return the bool argument
     */
    public Optional<CommandArgument<Boolean>> getBoolArgument(int index) {
        return getCommandValueAs(index, Boolean.class, args);
    }

    /**
     Gets int argument.

     @param index
     the index

     @return the int argument
     */
    public Optional<CommandArgument<Integer>> getIntArgument(int index) {
        return getCommandValueAs(index, Integer.class, args);
    }

    /**
     Gets double argument.

     @param index
     the index

     @return the double argument
     */
    public Optional<CommandArgument<Double>> getDoubleArgument(int index) {
        return getCommandValueAs(index, Double.class, args);
    }

    /**
     Gets float argument.

     @param index
     the index

     @return the float argument
     */
    public Optional<CommandArgument<Float>> getFloatArgument(int index) {
        return getCommandValueAs(index, Float.class, args);
    }

    /**
     Gets string argument.

     @param key
     the key

     @return the string argument
     */
    public Optional<CommandArgument<String>> getStringArgument(String key) {
        Optional<CommandArgument<?>> candidate = Arrays.stream(args).filter(val -> val.getKey().equals(key)).findFirst();
        if (candidate.isPresent()) {
            CommandArgument<?> value = candidate.get();
            return Optional.of(new CommandArgument<>(value.getValue().toString(), value.isJoined(), value.getKey()));
        }
        return Optional.empty();
    }

    /**
     Gets bool argument.

     @param key
     the key

     @return the bool argument
     */
    public Optional<CommandArgument<Boolean>> getBoolArgument(String key) {
        return getCommandValueAs(key, Boolean.class, args);
    }

    /**
     Gets int argument.

     @param key
     the key

     @return the int argument
     */
    public Optional<CommandArgument<Integer>> getIntArgument(String key) {
        return getCommandValueAs(key, Integer.class, args);
    }

    /**
     Gets double argument.

     @param key
     the key

     @return the double argument
     */
    public Optional<CommandArgument<Double>> getDoubleArgument(String key) {
        return getCommandValueAs(key, Double.class, args);
    }

    /**
     Gets float argument.

     @param key
     the key

     @return the float argument
     */
    public Optional<CommandArgument<Float>> getFloatArgument(String key) {
        return getCommandValueAs(key, Float.class, args);
    }

    /**
     Gets argument as world.

     @param index
     the index

     @return the argument as world
     */
// Special type argument parsers
    public Optional<DarwinWorld> getArgumentAsWorld(int index) {
        return getStringArgument(index).flatMap(name -> DarwinServer.getUtilChecked(LocationUtils.class).getWorld(name.getValue()));
    }

    /**
     Gets argument as world.

     @param key
     the key

     @return the argument as world
     */
    public Optional<DarwinWorld> getArgumentAsWorld(String key) {
        return getStringArgument(key).flatMap(name -> DarwinServer.getUtilChecked(LocationUtils.class).getWorld(name.getValue()));
    }

    /**
     Gets argument as online player.

     @param index
     the index

     @return the argument as online player
     */
    public Optional<DarwinPlayer> getArgumentAsOnlinePlayer(int index) {
        return getStringArgument(index).flatMap(name -> DarwinServer.getUtilChecked(PlayerManager.class).getPlayer(name.getValue()));
    }

    /**
     Gets argument as online player.

     @param key
     the key

     @return the argument as online player
     */
    public Optional<DarwinPlayer> getArgumentAsOnlinePlayer(String key) {
        return getStringArgument(key).flatMap(name -> DarwinServer.getUtilChecked(PlayerManager.class).getPlayer(name.getValue()));
    }

    /**
     Gets enum argument.

     @param <T>
     the type parameter
     @param key
     the key
     @param enumType
     the enum type

     @return the enum argument
     */
    public <T extends Enum<?>> Optional<T> getEnumArgument(String key, Class<T> enumType) {
        return getStringArgument(key).flatMap(arg -> ENUM_ARGUMENT_PARSER.parse(arg, enumType));
    }

    /**
     Gets enum argument.

     @param <T>
     the type parameter
     @param index
     the index
     @param enumType
     the enum type

     @return the enum argument
     */
    public <T extends Enum<?>> Optional<T> getEnumArgument(int index, Class<T> enumType) {
        return getStringArgument(index).flatMap(arg -> ENUM_ARGUMENT_PARSER.parse(arg, enumType));
    }

    /**
     Gets argument and parse.

     @param <T>
     the type parameter
     @param index
     the index
     @param parser
     the parser

     @return the argument and parse
     */
    public <T> Optional<T> getArgumentAndParse(int index, FunctionalParser<T> parser) {
        return getStringArgument(index).flatMap(parser::parse);
    }

    /**
     Gets argument and parse.

     @param <T>
     the type parameter
     @param index
     the index
     @param parser
     the parser

     @return the argument and parse
     */
    public <T> Optional<T> getArgumentAndParse(int index, ArgumentParser parser) {
        return getStringArgument(index).flatMap(parser::parse);
    }

    /**
     Gets argument and parse.

     @param <T>
     the type parameter
     @param index
     the index
     @param parser
     the parser
     @param type
     the type

     @return the argument and parse
     */
    public <T> Optional<T> getArgumentAndParse(int index, TypeArgumentParser parser, Class<T> type) {
        return getStringArgument(index).flatMap(arg -> parser.parse(arg, type));
    }

    /**
     Gets argument and parse.

     @param <T>
     the type parameter
     @param key
     the key
     @param parser
     the parser

     @return the argument and parse
     */
    public <T> Optional<T> getArgumentAndParse(String key, FunctionalParser<T> parser) {
        return getStringArgument(key).flatMap(parser::parse);
    }

    /**
     Gets argument and parse.

     @param <T>
     the type parameter
     @param key
     the key
     @param parser
     the parser

     @return the argument and parse
     */
    public <T> Optional<T> getArgumentAndParse(String key, ArgumentParser parser) {
        return getStringArgument(key).flatMap(parser::parse);
    }

    /**
     Gets argument and parse.

     @param <T>
     the type parameter
     @param key
     the key
     @param parser
     the parser
     @param type
     the type

     @return the argument and parse
     */
    public <T> Optional<T> getArgumentAndParse(String key, TypeArgumentParser parser, Class<T> type) {
        return getStringArgument(key).flatMap(arg -> parser.parse(arg, type));
    }

    /**
     Gets string flag.

     @param index
     the index

     @return the string flag
     */
// Native flag parsers
    public Optional<CommandFlag<String>> getStringFlag(int index) {
        CommandFlag<?> value = flags[index];
        if (value != null) return Optional.of(new CommandFlag<>(value.getValue().toString(), value.getKey()));
        return Optional.empty();
    }

    /**
     Gets bool flag.

     @param index
     the index

     @return the bool flag
     */
    public Optional<CommandFlag<Boolean>> getBoolFlag(int index) {
        return getCommandValueAs(index, Boolean.class, flags);
    }

    /**
     Gets int flag.

     @param index
     the index

     @return the int flag
     */
    public Optional<CommandFlag<Integer>> getIntFlag(int index) {
        return getCommandValueAs(index, Integer.class, flags);
    }

    /**
     Gets double flag.

     @param index
     the index

     @return the double flag
     */
    public Optional<CommandFlag<Double>> getDoubleFlag(int index) {
        return getCommandValueAs(index, Double.class, flags);
    }

    /**
     Gets float flag.

     @param index
     the index

     @return the float flag
     */
    public Optional<CommandFlag<Float>> getFloatFlag(int index) {
        return getCommandValueAs(index, Float.class, flags);
    }

    /**
     Gets string flag.

     @param key
     the key

     @return the string flag
     */
    public Optional<CommandFlag<String>> getStringFlag(String key) {
        Optional<CommandFlag<?>> candidate = Arrays.stream(flags).filter(val -> val.getKey().equals(key)).findFirst();
        if (candidate.isPresent()) {
            CommandFlag<?> value = candidate.get();
            return Optional.of(new CommandFlag<>(value.getValue().toString(), value.getKey()));
        }
        return Optional.empty();
    }

    /**
     Gets bool flag.

     @param key
     the key

     @return the bool flag
     */
    public Optional<CommandFlag<Boolean>> getBoolFlag(String key) {
        return getCommandValueAs(key, Boolean.class, flags);
    }

    /**
     Gets int flag.

     @param key
     the key

     @return the int flag
     */
    public Optional<CommandFlag<Integer>> getIntFlag(String key) {
        return getCommandValueAs(key, Integer.class, flags);
    }

    /**
     Gets double flag.

     @param key
     the key

     @return the double flag
     */
    public Optional<CommandFlag<Double>> getDoubleFlag(String key) {
        return getCommandValueAs(key, Double.class, flags);
    }

    /**
     Gets float flag.

     @param key
     the key

     @return the float flag
     */
    public Optional<CommandFlag<Float>> getFloatFlag(String key) {
        return getCommandValueAs(key, Float.class, flags);
    }

    /**
     Gets enum flag.

     @param <T>
     the type parameter
     @param key
     the key
     @param enumType
     the enum type

     @return the enum flag
     */
// Special type flag parsers
    public <T extends Enum<?>> Optional<T> getEnumFlag(String key, Class<T> enumType) {
        return getStringFlag(key).flatMap(flag -> ENUM_ARGUMENT_PARSER.parse(flag, enumType));
    }

    /**
     Gets enum flag.

     @param <T>
     the type parameter
     @param index
     the index
     @param enumType
     the enum type

     @return the enum flag
     */
    public <T extends Enum<?>> Optional<T> getEnumFlag(int index, Class<T> enumType) {
        return getStringFlag(index).flatMap(flag -> ENUM_ARGUMENT_PARSER.parse(flag, enumType));
    }


    /**
     Gets flag and parse.

     @param <T>
     the type parameter
     @param index
     the index
     @param parser
     the parser

     @return the flag and parse
     */
    public <T> Optional<T> getFlagAndParse(int index, FunctionalParser<T> parser) {
        return getStringFlag(index).flatMap(parser::parse);
    }

    /**
     Gets flag and parse.

     @param <T>
     the type parameter
     @param index
     the index
     @param parser
     the parser

     @return the flag and parse
     */
    public <T> Optional<T> getFlagAndParse(int index, ArgumentParser parser) {
        return getStringFlag(index).flatMap(parser::parse);
    }

    /**
     Gets flag and parse.

     @param <T>
     the type parameter
     @param index
     the index
     @param parser
     the parser
     @param type
     the type

     @return the flag and parse
     */
    public <T> Optional<T> getFlagAndParse(int index, TypeArgumentParser parser, Class<T> type) {
        return getStringFlag(index).flatMap(arg -> parser.parse(arg, type));
    }

    /**
     Gets flag and parse.

     @param <T>
     the type parameter
     @param key
     the key
     @param parser
     the parser

     @return the flag and parse
     */
    public <T> Optional<T> getFlagAndParse(String key, FunctionalParser<T> parser) {
        return getStringFlag(key).flatMap(parser::parse);
    }

    /**
     Gets flag and parse.

     @param <T>
     the type parameter
     @param key
     the key
     @param parser
     the parser

     @return the flag and parse
     */
    public <T> Optional<T> getFlagAndParse(String key, ArgumentParser parser) {
        return getStringFlag(key).flatMap(parser::parse);
    }

    /**
     Gets flag and parse.

     @param <T>
     the type parameter
     @param key
     the key
     @param parser
     the parser
     @param type
     the type

     @return the flag and parse
     */
    public <T> Optional<T> getFlagAndParse(String key, TypeArgumentParser parser, Class<T> type) {
        return getStringFlag(key).flatMap(arg -> parser.parse(arg, type));
    }

    // Dynamic values
    @SuppressWarnings("unchecked")
    private <T, A extends AbstractCommandValue<T>> Optional<A> getCommandValueAs(int index, Class<T> type, AbstractCommandValue<?>[] values) {
        AbstractCommandValue<?> value = values[index];
        if (value != null && value.getValue().getClass().equals(type)) return Optional.of((A) value);
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <T, A extends AbstractCommandValue<T>> Optional<A> getCommandValueAs(String key, Class<T> type, AbstractCommandValue<?>[] values) {
        Optional<AbstractCommandValue<?>> candidate = Arrays.stream(values).filter(val -> val.getKey().equals(key)).findFirst();
        if (candidate.isPresent()) {
            AbstractCommandValue<?> value = candidate.get();
            if (value.getValue().getClass().equals(type)) return Optional.of((A) value);
        }
        return Optional.empty();
    }

    /**
     The type Enum argument parser.
     */
    public static final class EnumArgumentParser extends TypeArgumentParser {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public <A> Optional<A> parse(AbstractCommandValue<String> commandValue, Class<A> type) {
            if (type.isEnum()) {
                String value = commandValue.getValue();
                try {
                    Class<Enum> enumType = (Class<Enum>) type;
                    return Optional.of((A) Enum.valueOf(enumType, value));
                } catch (IllegalArgumentException | NullPointerException e) {
                    DarwinServer.getLog().warn("Attempted to get value of '" + value + "' and caught error : " + e.getMessage());
                }
            }
            return Optional.empty();
        }
    }

}
