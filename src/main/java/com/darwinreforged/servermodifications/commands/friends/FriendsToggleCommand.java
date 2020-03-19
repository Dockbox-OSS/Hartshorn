package com.darwinreforged.servermodifications.commands.friends;

import com.darwinreforged.servermodifications.objects.FriendsStorage;
import com.darwinreforged.servermodifications.util.FriendsUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FriendsToggleCommand  implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player source = (Player) src;
        FriendsStorage sourceStorage = FriendsUtil.getData(source.getUniqueId());
        sourceStorage.toggle();
        source.sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, "Accept teleports from friends : ", sourceStorage.toggledTeleportsOff ? "Off" : "On"));
        return CommandResult.success();
    }
}
