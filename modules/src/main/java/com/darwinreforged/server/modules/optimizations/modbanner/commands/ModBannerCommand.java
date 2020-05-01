package com.darwinreforged.server.modules.optimizations.modbanner.commands;

import com.darwinreforged.server.sponge.DarwinServer;
import com.darwinreforged.server.modules.optimizations.modbanner.ModBannerModule;
import com.darwinreforged.server.modules.optimizations.modbanner.util.ModBannerHelper;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModBannerCommand
        implements CommandCallable {

    HashMap<String, String> subCommands = new HashMap<>();

    public ModBannerCommand() {
        if (subCommands.isEmpty()) {
            subCommands.put("add", "Add mod to blacklist");
            subCommands.put("remove", "Remove mod from backlist");
            subCommands.put("list", "List all blacklisted mods");
            subCommands.put("reload", "Reload blaclist (Not needed if you do /add)");
        }
    }

    @Override
    public CommandResult process(CommandSource source, String arguments)
            throws CommandException {
        String[] args = arguments.split(" ");
        DarwinServer.getModule(ModBannerModule.class).ifPresent(module -> {
            switch (args[0].toLowerCase()) {
                case "add":
                    if (source.hasPermission("modbanner.commmand.add") || source.hasPermission("modbanner.commmand.manage")) {
                        if (args.length >= 2) {
                            module.cfgManager.blackList.add(args[1]);
                            module.cfgManager.save();
                            module.reloadConfiguration();
                            source.sendMessage(ModBannerHelper.format("&a" + args[1] + " added to blacklist!"));
                        } else {
                            source.sendMessage(ModBannerHelper
                                    .format("&cPlease add the mod name /modbanner add <mod>"));
                        }
                    }
                    break;
                case "remove":
                    if (source.hasPermission("modbanner.commmand.remove") || source.hasPermission("modbanner.commmand.manage")) {
                        if (args.length >= 2) {
                            if (module.cfgManager.blackList.contains(args[1])) {
                                module.cfgManager.blackList.remove(args[1]);
                                source.sendMessage(ModBannerHelper.format("&a" + args[1] + " removed from blacklist!"));
                                module.cfgManager.save();
                                module.reloadConfiguration();
                            } else {
                                source.sendMessage(ModBannerHelper.format("&cCan't find mod " + args[1]));
                            }
                        } else {
                            source.sendMessage(ModBannerHelper
                                    .format("&cPlease add the mod name /modbanner remove <mod>"));
                        }
                    }
                    break;
                case "list":
                    if (source.hasPermission("modbanner.commmand.list") || source.hasPermission("modbanner.commmand.manage")) {
                        List<Text> t = new ArrayList<>();
                        for (String bl : module.cfgManager.blackList) {
                            t.add(ModBannerHelper.format(bl));
                        }
                        PaginationList.builder()
                                .title(ModBannerHelper.format("BlackListed Mods"))
                                .contents(t)
                                .padding(Text.of("-")).sendTo(source);
                    }
                    break;
                case "reload":
                    if (source.hasPermission("modbanner.commmand.reload")) {
                        module.reloadConfiguration();
                        source.sendMessage(ModBannerHelper.format("&aConfiguration reloaded!"));
                    }
                    break;
                default:
                    source.sendMessage(getHelp(source).get());
            }
        });

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
        Text.Builder rs = Text.builder();
        for (Entry<String, String> a : subCommands.entrySet()) {
            rs.append(ModBannerHelper.format("&c/modbanner " + a.getKey() + " (" + a.getValue() + ")\n"));
        }
        return Optional.of(rs.build());
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of(TextColors.RED,
                "modbanner <" + (subCommands.keySet().stream().collect(Collectors.joining("|"))) + ">");
    }
}
