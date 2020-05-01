package com.darwinreforged.server.modules.extensions.teleport.friends.commands;

import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.sponge.utils.PlayerUtils;
import com.darwinreforged.server.modules.extensions.teleport.friends.util.FriendsStorage;
import com.darwinreforged.server.modules.extensions.teleport.friends.util.FriendsUtil;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class FriendsToggleCommand
        implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player source = (Player) src;
        FriendsStorage sourceStorage = FriendsUtil.getData(source.getUniqueId());
        sourceStorage.toggle();
        PlayerUtils.tell(source, Translations.ACCEPTING_TP
                .f(sourceStorage.toggledTeleportsOff ? Translations.DEFAULT_OFF
                        .s() : Translations.DEFAULT_ON
                        .s()));
        return CommandResult.success();
    }
}
