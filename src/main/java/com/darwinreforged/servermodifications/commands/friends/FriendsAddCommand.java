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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class FriendsAddCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        //get the online target for adding a friend and the source player
        User target = (User) args.getOne("online player").get();
        Player source = (Player) src;

        //get their storage options
        FriendsStorage targetStorage = FriendsUtil.getData(target.getUniqueId());
        FriendsStorage sourceStorage = FriendsUtil.getData(source.getUniqueId());

        if (targetStorage == null) {
            //System.out.println("Target storage is null, returning");
            return CommandResult.success();
        }

        if (sourceStorage.getFriends().contains(target.getUniqueId())) {
            source.sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.RED, " You are already friends with that player"));
            return CommandResult.success();
        }

        //if the target has a friend request from the source, add them as a friend to both source and target
        if (sourceStorage.getRequests().contains(target.getUniqueId())) {
            sourceStorage.addFriend(target.getUniqueId());
            targetStorage.addFriend(source.getUniqueId());
            if (target.isOnline()) {
                Sponge.getServer().getPlayer(target.getName()).get().sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, "You are now friends with ", TextColors.DARK_AQUA, source.getName()));
            }
            if (source.isOnline()) {
                Sponge.getServer().getPlayer(source.getName()).get().sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, "You are now friends with ", TextColors.DARK_AQUA, target.getName()));
            }
        }
        //if not give the target a friend request and a message;
        else {
            targetStorage.addRequest(source.getUniqueId());
            source.sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, "A friend request was sent to ", TextColors.DARK_AQUA, target.getName()));
            if (target.isOnline()) {
                Player targetM = Sponge.getServer().getPlayer(target.getName()).get();
                targetM.sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.DARK_AQUA, source.getName(), TextColors.AQUA, " has requested to befriend you."));
                Text.Builder accept = Text.builder();
                accept.append(Text.of(TextColors.WHITE, TextStyles.UNDERLINE, "Accept")).onClick(TextActions.runCommand("/friend add " + source.getName())).onHover(TextActions.showText(Text.of("Click me to accept this request"))).build();
                Text.Builder reject = Text.builder();
                reject.append(Text.of(TextColors.WHITE, TextStyles.UNDERLINE, "Deny")).onClick(TextActions.runCommand("/friend remove " + source.getName())).onHover(TextActions.showText(Text.of("Click me to reject this request"))).build();
                targetM.sendMessage(Text.of(accept.build(), Text.of(" - "), reject.build()));
            }
        }

        //save both of the files
        FriendsStorageManager.save(target.getUniqueId(), targetStorage);
        FriendsStorageManager.save(source.getUniqueId(), sourceStorage);

        return CommandResult.success();
    }

}
