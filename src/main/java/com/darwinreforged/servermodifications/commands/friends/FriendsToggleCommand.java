package com.darwinreforged.servermodifications.commands.friends;

import com.darwinreforged.servermodifications.objects.FriendsStorage;
import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.FriendsUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class FriendsToggleCommand  implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player source = (Player) src;
        FriendsStorage sourceStorage = FriendsUtil.getData(source.getUniqueId());
        sourceStorage.toggle();
        PlayerUtils.tell(source, Translations.ACCEPTING_TP.f(sourceStorage.toggledTeleportsOff ? "Off" : "On"));
        return CommandResult.success();
    }
}
