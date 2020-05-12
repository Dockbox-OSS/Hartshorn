package com.darwinreforged.server.core.commands.context;

import com.darwinreforged.server.core.types.living.CommandSender;
import com.darwinreforged.server.core.types.living.DarwinPlayer;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.util.LocationUtils;
import com.darwinreforged.server.core.util.PlayerUtils;

import java.util.Arrays;
import java.util.Optional;

public class CommandContext {

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

    public Optional<CommandArgument<String>> getStringArgument(int index) {
        return getArgumentAs(index, String.class);
    }

    public Optional<CommandArgument<Boolean>> getBoolArgument(int index) {
        return getArgumentAs(index, Boolean.class);
    }

    public Optional<CommandArgument<Integer>> getIntArgument(int index) {
        return getArgumentAs(index, Integer.class);
    }

    public Optional<CommandArgument<Double>> getDoubleArgument(int index) {
        return getArgumentAs(index, Double.class);
    }

    public Optional<CommandArgument<Float>> getFloatArgument(int index) {
        return getArgumentAs(index, Float.class);
    }

    public Optional<CommandArgument<String>> getStringArgument(String key) {
        return getArgumentAs(key, String.class);
    }

    public Optional<CommandArgument<Boolean>> getBoolArgument(String key) {
        return getArgumentAs(key, Boolean.class);
    }

    public Optional<CommandArgument<Integer>> getIntArgument(String key) {
        return getArgumentAs(key, Integer.class);
    }

    public Optional<CommandArgument<Double>> getDoubleArgument(String key) {
        return getArgumentAs(key, Double.class);
    }

    public Optional<CommandArgument<Float>> getFloatArgument(String key) {
        return getArgumentAs(key, Float.class);
    }

    public Optional<DarwinWorld> getArgumentAsWorld(int index) {
        return getStringArgument(index).flatMap(name -> DarwinServer.getUtilChecked(LocationUtils.class).getWorld(name.getArgument()));
    }

    public Optional<DarwinWorld> getArgumentAsWorld(String key) {
        return getStringArgument(key).flatMap(name -> DarwinServer.getUtilChecked(LocationUtils.class).getWorld(name.getArgument()));
    }

    public Optional<DarwinPlayer> getArgumentAsOnlinePlayer(int index) {
        return getStringArgument(index).flatMap(name -> DarwinServer.getUtilChecked(PlayerUtils.class).getPlayer(name.getArgument()));
    }

    public Optional<DarwinPlayer> getArgumentAsOnlinePlayer(String key) {
        return getStringArgument(key).flatMap(name -> DarwinServer.getUtilChecked(PlayerUtils.class).getPlayer(name.getArgument()));
    }

    public Optional<CommandFlag<?>> getFlag(String key) {
        return Arrays.stream(flags).filter(flag -> flag.getKey().equals(key)).findFirst();
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<CommandArgument<T>> getArgumentAs(int index, Class<T> type) {
        CommandArgument<?> arg = args[index];
        if (arg != null && arg.getArgument().getClass().equals(type)) return Optional.of((CommandArgument<T>) arg);
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<CommandArgument<T>> getArgumentAs(String key, Class<T> type) {
        Optional<CommandArgument<?>> argumentCandidate = Arrays.stream(args).filter(arg -> arg.getKey().equals(key)).findFirst();
        if (argumentCandidate.isPresent()) {
            CommandArgument<?> arg = argumentCandidate.get();
            if (arg.getArgument().getClass().equals(type)) return Optional.of((CommandArgument<T>) arg);
        }
        return Optional.empty();
    }
}
