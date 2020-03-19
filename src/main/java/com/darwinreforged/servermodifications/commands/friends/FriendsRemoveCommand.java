package com.darwinreforged.servermodifications.commands.friends;

import com.darwinreforged.servermodifications.util.FriendsStorage;
import com.darwinreforged.servermodifications.util.FriendsStorageManager;
import com.darwinreforged.servermodifications.util.FriendsUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FriendsRemoveCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        //get the online target for adding a friend and the source player
        User target = (User) args.getOne("online/offline player").get();
        Player source = (Player) src;

        //get their storage options
        FriendsStorage targetStorage = FriendsUtil.getData(target.getUniqueId());
        FriendsStorage sourceStorage = FriendsUtil.getData(source.getUniqueId());

        sourceStorage.removeFriend(target.getUniqueId());
        targetStorage.removeFriend(source.getUniqueId());
        if (target.isOnline()) {
            Sponge.getServer().getPlayer(target.getName()).get().sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, "Rejected ", TextColors.DARK_AQUA, source.getName(), TextColors.AQUA, " as a friend."));
        }
        if (source.isOnline()) {
            Sponge.getServer().getPlayer(source.getName()).get().sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, "Rejected ", TextColors.DARK_AQUA, target.getName(), TextColors.AQUA, " as a friend."));
        }
        FriendsStorageManager.save(target.getUniqueId(), targetStorage);
        FriendsStorageManager.save(source.getUniqueId(), sourceStorage);
        return CommandResult.success();
    }

}
