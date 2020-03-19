package com.darwinreforged.servermodifications.commands.friends;

import com.darwinreforged.servermodifications.util.FriendsStorage;
import com.darwinreforged.servermodifications.util.FriendsUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.UUID;

public class FriendsListCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player source = (Player) src;
        FriendsStorage sourceStorage = FriendsUtil.getData(source.getUniqueId());
        PaginationList.Builder builder = PaginationList.builder();
        ArrayList<UUID> temp = sourceStorage.getFriends();
        ArrayList<UUID> temp2 = sourceStorage.getRequests();
        ArrayList<Text> contents = new ArrayList<Text>();
        for (UUID id : temp) {
            if (FriendsUtil.getUser(id).isPresent()) {
                Text.Builder sendRemove = Text.builder();
                Text.Builder sendFriend = Text.builder();
                sendFriend.append(Text.of(TextColors.AQUA, FriendsUtil.getUser(id).get().getName(), " "));
                sendFriend.build();
                sendRemove.append(Text.of(TextColors.RED, "[X]")).onClick(TextActions.runCommand("/friend remove " + FriendsUtil.getUser(id).get().getName()));
                sendFriend.append(sendRemove.build());
                contents.add(sendFriend.build());
                sendFriend.removeAll();
            }
        }
        for (UUID id : temp2) {
            if (FriendsUtil.getUser(id).isPresent()) {
                contents.add(Text.of(TextColors.AQUA, FriendsUtil.getUser(id).get().getName(), TextColors.DARK_AQUA, " - Request"));
            }
        }
        PaginationList.builder()
                .title(Text.of(TextColors.AQUA, "Friends"))
                .contents(contents)
                .padding(Text.of(TextColors.DARK_AQUA, TextStyles.STRIKETHROUGH, "="))
                .sendTo(source);
        return CommandResult.success();
    }
}
