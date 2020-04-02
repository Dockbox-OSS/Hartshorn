package com.darwinreforged.servermodifications.commands.friends;

import com.darwinreforged.servermodifications.objects.FriendsStorage;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.FriendsUtil;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class FriendsTeleportCommand
        implements CommandExecutor {

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
                PlayerUtils.tell(target, Translations.FRIEND_TELEPORTED.f(source.getName()));
            }
        } else {
            PlayerUtils.tell(source, Translations.NO_TP_NOT_FRIENDS.s());
        }
        return CommandResult.success();
    }
}
