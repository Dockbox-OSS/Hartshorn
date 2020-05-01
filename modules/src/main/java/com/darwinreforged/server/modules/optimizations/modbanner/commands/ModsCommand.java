package com.darwinreforged.server.modules.optimizations.modbanner.commands;

import com.darwinreforged.server.modules.optimizations.modbanner.ModData;
import com.darwinreforged.server.modules.optimizations.modbanner.util.ModBannerHelper;
import com.darwinreforged.server.modules.optimizations.modbanner.util.PlayerModsUtil;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ModsCommand
        implements CommandCallable {

    HashMap<String, String> subCommands = new HashMap<>();

    public ModsCommand() {
        if (subCommands.isEmpty()) {
            subCommands.put("player", "See last mods player use");
            subCommands.put("mod", "Get all players who specific mod");
        }
    }

    @Override
    public CommandResult process(CommandSource source, String arguments)
            throws CommandException {
        String[] args = arguments.split(" ");

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("player")) {
                if (source.hasPermission("modbanner.command.modinfo.player")) {
                    source.sendMessage(ModBannerHelper.format(args[1] + " last know mods: " + PlayerModsUtil
                            .getLastPlayerData(args[1])
                            .stream()
                            .map(ModData::getCompleteData)
                            .collect(Collectors
                                    .joining(", "))));
                }
            } else if (args[0].equalsIgnoreCase("mod")) {
                if (source.hasPermission("modbanner.command.modinfo.mod")) {
                    source.sendMessage(ModBannerHelper.format("Users who use " + args[1] + ": " + PlayerModsUtil
                            .getPlayersWhoUseMod(args[1])
                            .stream()
                            .collect(Collectors
                                    .joining(", "))));
                }
            } else {
                source.sendMessage(ModBannerHelper.format("&cInvalid args"));
                source.sendMessage(getHelp(source).get());
            }
        } else {
            source.sendMessage(ModBannerHelper.format("&cInvalid args"));
            source.sendMessage(getHelp(source).get());
        }

        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
            throws CommandException {
        return new ArrayList<>(subCommands.keySet());
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of(ModBannerHelper.format("&c/mods player <player>\n&c/mods mod <mod>"));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of(TextColors.RED,
                "modinfo <" + (subCommands.keySet().stream().collect(Collectors.joining("|"))) + ">");
    }
}
