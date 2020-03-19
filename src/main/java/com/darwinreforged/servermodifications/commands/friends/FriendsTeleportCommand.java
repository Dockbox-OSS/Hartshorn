package com.darwinreforged.servermodifications.commands.friends;

import com.darwinreforged.servermodifications.util.FriendsStorage;
import com.darwinreforged.servermodifications.util.FriendsUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FriendsTeleportCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        //get the online target for adding a friend and the source player
        Player target = (Player) args.getOne("online player").get();
        Player source = (Player) src;

        //get their storage options
        FriendsStorage targetStorage = FriendsUtil.getData(target.getUniqueId());
        //PlayerStorage sourceStorage = FriendsUtil.getData(source.getUniqueId());


        if (targetStorage.isFriend(source.getUniqueId())) {
            if (!targetStorage.toggledTeleportsOff) {
                source.setLocationAndRotation(target.getLocation(), target.getRotation());
                target.sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, source.getName(), TextColors.DARK_AQUA, " teleported to your location."));
            }
        } else {
            source.sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.RED, "You are not friends with that user so you cannot teleport to them."));
        }
        return CommandResult.success();
    }
}
