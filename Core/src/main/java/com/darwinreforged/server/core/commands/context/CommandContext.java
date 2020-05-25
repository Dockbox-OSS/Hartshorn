package com.darwinreforged.server.core.commands.context;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.commands.context.parsing.ArgumentParser;
import com.darwinreforged.server.core.commands.context.parsing.FunctionalParser;
import com.darwinreforged.server.core.commands.context.parsing.TypeArgumentParser;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class CommandContext {

    private static final EnumArgumentParser ENUM_ARGUMENT_PARSER = new EnumArgumentParser();

    private final CommandArgument<?>[] args; // Done
    private final CommandFlag<?>[] flags; // Done
    private final CommandSender sender; // Done
    private final DarwinLocation location; // Done
    private final DarwinWorld world;
    private final Permissions[] permissions;

    public CommandContext(CommandArgument<?>[] args, CommandSender sender, DarwinWorld world, DarwinLocation location, Permissions[] permissions, CommandFlag<?>[] flags) {
        this.args = args;
        this.sender = sender;
        this.world = world;
        this.location = location;
        this.permissions = permissions;
        this.flags = flags;
    }

    public CommandArgument<?>[] getArgs() {
        return args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public DarwinWorld getWorld() {
        return world;
    }

    public DarwinLocation getLocation() {
        return location;
    }

    public Permissions[] getPermissions() {
        return permissions;
    }

    public CommandFlag<?>[] getFlags() {
        return flags;
    }

    public int getArgumentCount() {
        return args.length;
    }

    public int getFlagCount() {
        return flags.length;
    }

    public <T> Optional<CommandArgument<T>> getArgument(String key, Class<T> type) {
        return Arrays.stream(args).filter(arg -> arg.getKey().equals(key)).findFirst().map(arg -> (CommandArgument<T>) arg);
    }

    public <T> Optional<CommandFlag<T>> getFlag(String key, Class<T> type) {
        return Arrays.stream(flags).filter(flag -> flag.getKey().equals(key)).findFirst().map(flag -> (CommandFlag<T>) flag);
    }

    // Native argument parsers

    public Optional<CommandArgument<String>> getStringArgument(String key) {
        Optional<CommandArgument<?>> candidate = Arrays.stream(args).filter(val -> val.getKey().equals(key)).findFirst();
        if (candidate.isPresent()) {
            CommandArgument<?> value = candidate.get();
            return Optional.of(new CommandArgument<>(value.getValue().toString(), value.isJoined(), value.getKey()));
        }
        return Optional.empty();
    }

    public Optional<CommandArgument<Boolean>> getBoolArgument(String key) {
        return getCommandValueAs(key, Boolean.class, args);
    }

    public Optional<CommandArgument<Integer>> getIntArgument(String key) {
        return getCommandValueAs(key, Integer.class, args);
    }

    public Optional<CommandArgument<Double>> getDoubleArgument(String key) {
        return getCommandValueAs(key, Double.class, args);
    }

    public Optional<CommandArgument<Float>> getFloatArgument(String key) {
        return getCommandValueAs(key, Float.class, args);
    }

    // Special type argument parsers

    public <T> Optional<T> getArgumentAndParse(String key, FunctionalParser<T> parser) {
        return getStringArgument(key).flatMap(parser::parse);
    }

    public <T> Optional<T> getArgumentAndParse(String key, ArgumentParser parser) {
        return getStringArgument(key).flatMap(parser::parse);
    }

    public <T> Optional<T> getArgumentAndParse(String key, TypeArgumentParser parser, Class<T> type) {
        return getStringArgument(key).flatMap(arg -> parser.parse(arg, type));
    }

    // Native flag parsers

    public Optional<CommandFlag<String>> getStringFlag(String key) {
        Optional<CommandFlag<?>> candidate = Arrays.stream(flags).filter(val -> val.getKey().equals(key)).findFirst();
        if (candidate.isPresent()) {
            CommandFlag<?> value = candidate.get();
            return Optional.of(new CommandFlag<>(value.getValue().toString(), value.getKey()));
        }
        return Optional.empty();
    }

    public Optional<CommandFlag<Boolean>> getBoolFlag(String key) {
        return getCommandValueAs(key, Boolean.class, flags);
    }

    public Optional<CommandFlag<Integer>> getIntFlag(String key) {
        return getCommandValueAs(key, Integer.class, flags);
    }

    public Optional<CommandFlag<Double>> getDoubleFlag(String key) {
        return getCommandValueAs(key, Double.class, flags);
    }

    public Optional<CommandFlag<Float>> getFloatFlag(String key) {
        return getCommandValueAs(key, Float.class, flags);
    }

    // Special type flag parsers
    public <T extends Enum<?>> Optional<T> getEnumFlag(String key, Class<T> enumType) {
        return getStringFlag(key).flatMap(flag -> ENUM_ARGUMENT_PARSER.parse(flag, enumType));
    }

    public <T> Optional<T> getFlagAndParse(String key, FunctionalParser<T> parser) {
        return getStringFlag(key).flatMap(parser::parse);
    }

    public <T> Optional<T> getFlagAndParse(String key, ArgumentParser parser) {
        return getStringFlag(key).flatMap(parser::parse);
    }

    public <T> Optional<T> getFlagAndParse(String key, TypeArgumentParser parser, Class<T> type) {
        return getStringFlag(key).flatMap(arg -> parser.parse(arg, type));
    }

    private <T, A extends AbstractCommandValue<T>> Optional<A> getCommandValueAs(String key, Class<T> type, AbstractCommandValue<?>[] values) {
        Optional<AbstractCommandValue<?>> candidate = Arrays.stream(values).filter(val -> val.getKey().equals(key)).findFirst();
        if (candidate.isPresent()) {
            AbstractCommandValue<?> value = candidate.get();
            if (value.getValue().getClass().equals(type)) return Optional.of((A) value);
        }
        return Optional.empty();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final class EnumArgumentParser extends TypeArgumentParser {

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
