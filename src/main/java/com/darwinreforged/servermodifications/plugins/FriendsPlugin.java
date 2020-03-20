package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.commands.friends.*;
import com.darwinreforged.servermodifications.objects.FriendsStorage;
import com.darwinreforged.servermodifications.util.todo.FriendsStorageManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.UUID;

@Plugin(id = "darwinfriends", name = "Darwin Friends", version = "1.0", description = "Friend system for Darwin Reforged")
public class FriendsPlugin {

    public static HashMap<UUID, FriendsStorage> users = new HashMap<>();

    public FriendsPlugin() {
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        FriendsStorageManager.setup();
        //StorageManager.load();
    }

    @Listener
    public void onServerFinishLoad(GameStartedServerEvent event) {
        Sponge.getCommandManager().register(this, friendsCommand, "friend");
        Sponge.getEventManager().registerListeners(this, new loginEvent());
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        //StorageManager.save();
    }

    public class loginEvent {
        private void checkLogin(ClientConnectionEvent.Join event, Player player) {
            //when they login load them if they have a file
            if (FriendsStorageManager.load(player.getUniqueId()) != null) {
                FriendsPlugin.users.put(player.getUniqueId(), FriendsStorageManager.load(player.getUniqueId()));
            }
        }
    }

    CommandSpec teleportCommand = CommandSpec.builder()
            .description(Text.of("Main command for Darwin Friends"))
            .permission("DarwinFriends.use")
            .arguments(GenericArguments.player(Text.of("online player")))
            .executor(new FriendsTeleportCommand())
            .build();
    CommandSpec listCommand = CommandSpec.builder()
            .description(Text.of("Main command for Darwin Friends"))
            .permission("DarwinFriends.use")
            .executor(new FriendsListCommand())
            .build();
    CommandSpec toggleCommand = CommandSpec.builder()
            .description(Text.of("Main command for Darwin Friends"))
            .permission("DarwinFriends.use")
            .executor(new FriendsToggleCommand())
            .build();
    CommandSpec addCommand = CommandSpec.builder()
            .description(Text.of("Main command for Darwin Friends"))
            .permission("DarwinFriends.use")
            .arguments(GenericArguments.player(Text.of("online player")))
            .executor(new FriendsAddCommand())
            .build();
    CommandSpec removeCommand = CommandSpec.builder()
            .description(Text.of("Main command for Darwin Friends"))
            .permission("DarwinFriends.use")
            .arguments(GenericArguments.user(Text.of("online/offline player")))
            .executor(new FriendsRemoveCommand())
            .build();
    CommandSpec friendsCommand = CommandSpec.builder()
            .description(Text.of("Main command for Darwin Friends"))
            .permission("DarwinFriends.use")
            .child(addCommand, "add")
            .child(removeCommand, "remove")
            .child(listCommand, "list")
            .child(toggleCommand, "toggle")
            .child(teleportCommand, "tp")
            .build();
}
