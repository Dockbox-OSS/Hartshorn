package com.darwinreforged.server.core.types.living;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.chat.LegacyText;
import com.darwinreforged.server.core.chat.Text;
import com.darwinreforged.server.core.types.living.state.GameModes;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;
import com.darwinreforged.server.core.util.PlayerUtils;

import java.util.Optional;
import java.util.UUID;

/**
 The type Darwin player.
 */
public class DarwinPlayer extends CommandSender {

    /**
     Instantiates a new Darwin player.

     @param uuid
     the uuid
     @param name
     the name
     */
    public DarwinPlayer(UUID uuid, String name) {
        super.uuid = uuid;
        super.name = name;
    }

    /**
     Is online boolean.

     @return the boolean
     */
    public boolean isOnline() {
        return DarwinServer.getUtilChecked(PlayerUtils.class).isOnline(this);
    }

    /**
     Tell if permitted.

     @param text
     the text
     @param permission
     the permission
     */
    public void tellIfPermitted(String text, String permission) {
        if (hasPermission(permission)) sendMessage(text);
    }

    /**
     Kick.
     */
    public void kick() {
        DarwinServer.getUtilChecked(PlayerUtils.class).kick(this);
    }

    public boolean hasPermission(Permissions permission) {
        return !hasPermission(permission.p());
    }

    /**
     Has permission boolean.

     @param permission
     the permission

     @return the boolean
     */
    public boolean hasPermission(String permission) {
        return DarwinServer.getUtilChecked(PlayerUtils.class).hasPermission(this, permission);
    }

    /**
     Gets location.

     @return the location
     */
    public Optional<DarwinLocation> getLocation() {
        return DarwinServer.getUtilChecked(PlayerUtils.class).getLocation(this);
    }

    /**
     Gets world.

     @return the world
     */
    public Optional<DarwinWorld> getWorld() {
        return getLocation().map(DarwinLocation::getWorld);
    }

    /**
     Gets game mode.

     @return the game mode
     */
    public GameModes getGameMode() {
        return DarwinServer.getUtilChecked(PlayerUtils.class).getGameMode(this);
    }

    /**
     Sets game mode.

     @param mode
     the mode
     */
    public void setGameMode(GameModes mode) {
        DarwinServer.getUtilChecked(PlayerUtils.class).setGameMode(mode, this);
    }

    @Override
    public void execute(String cmd) {
        DarwinServer.getUtilChecked(PlayerUtils.class).executeCmd(cmd, this);
    }

    @Override
    public void explainCommand(String message, Command command) {
        sendMessage(message);
        if (command != null) {
            boolean flagEmpty = (command.flags().length == 0 || command.flags()[0].equals(""));
            sendMessage(Translations.CU_TITLE.f(command.aliases()[0]));
            sendMessage(Translations.CU_USAGE.f(command.usage()));
            if (!flagEmpty) sendMessage(Translations.CU_FLAGS.f(String.join(", ", command.flags())));
            sendMessage(Translations.CU_DESCRIPTION.f(String.join(", ", command.desc())));
        }
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(LegacyText.fromLegacy(message));
    }

    @Override
    public void sendMessage(Translations translation) {
        sendMessage(translation.s());
    }

    @Override
    public void sendMessage(Text text) {
        DarwinServer.getUtilChecked(PlayerUtils.class).tell(this, text);
    }

    @Override
    public void sendMessage(String message, String permission) {
        sendMessage(LegacyText.fromLegacy(message), permission);
    }

    @Override
    public void sendMessage(String message, Permissions permission) {
        sendMessage(message, permission.p());
    }

    @Override
    public void sendMessage(Translations translation, String permission) {
        sendMessage(translation.s(), permission);
    }

    @Override
    public void sendMessage(Translations translation, Permissions permission) {
        sendMessage(translation.s(), permission.p());
    }

    @Override
    public void sendMessage(Text text, String permission) {
        if (hasPermission(permission)) sendMessage(text);
    }

    @Override
    public void sendMessage(Text text, Permissions permission) {
        sendMessage(text, permission.p());
    }
}
