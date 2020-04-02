package com.darwinreforged.servermodifications.commands.friends;

import com.darwinreforged.servermodifications.objects.FriendsStorage;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.FriendsStorageManager;
import com.darwinreforged.servermodifications.util.todo.FriendsUtil;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

public class FriendsAddCommand
        implements CommandExecutor {

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
            PlayerUtils.tell(source, Translations.ALREADY_FRIENDS.f(target.getName()));
            return CommandResult.success();
        }

        //if the target has a friend request from the source, add them as a friend to both source and target
        if (sourceStorage.getRequests().contains(target.getUniqueId())) {
            sourceStorage.addFriend(target.getUniqueId());
            targetStorage.addFriend(source.getUniqueId());
            if (target.isOnline() && target.getPlayer().isPresent())
                PlayerUtils.tell(target.getPlayer().get(), Translations.FRIEND_ADDED.f(source.getName()));
            if (source.isOnline() && source.getPlayer().isPresent())
                PlayerUtils.tell(source.getPlayer().get(), Translations.FRIEND_ADDED.f(target.getName()));
        }
        //if not give the target a friend request and a message;
        else {
            targetStorage.addRequest(source.getUniqueId());
            PlayerUtils.tell(source, Translations.REQUEST_SENT.f(target.getName()));

            if (target.isOnline()) {
                Player targetM = Sponge.getServer().getPlayer(target.getName()).get();
                PlayerUtils.tell(targetM, Translations.REQUEST_RECEIVED.f(source.getName()));

                Text.Builder accept = Text.builder();
                accept.append(Translations.FRIEND_ACCEPT_BUTTON.t())
                        .onClick(TextActions.runCommand("/friend add " + source.getName()))
                        .onHover(TextActions.showText(Translations.FRIEND_ACCEPT_BUTTON_HOVER.ft(source.getName())))
                        .build();
                Text.Builder reject = Text.builder();
                reject.append(Translations.FRIEND_DENY_BUTTON.t())
                        .onClick(TextActions.runCommand("/friend remove " + source.getName()))
                        .onHover(TextActions.showText(Translations.FRIEND_DENY_BUTTON_HOVER.ft(source.getName())))
                        .build();

                PlayerUtils.tell(targetM, Text.of(accept.build(), Translations.DEFAULT_SEPARATOR.t(), reject.build()));
            }
        }

        //save both of the files
        FriendsStorageManager.save(target.getUniqueId(), targetStorage);
        FriendsStorageManager.save(source.getUniqueId(), sourceStorage);

        return CommandResult.success();
    }

}
