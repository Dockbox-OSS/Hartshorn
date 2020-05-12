package com.darwinreforged.server.core.types.living;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.types.chat.LegacyText;
import com.darwinreforged.server.core.types.chat.Text;
import com.darwinreforged.server.core.util.PlayerUtils;

import java.util.Arrays;

public class Console extends CommandSender {

    public static final Console instance = new Console();

    private Console() {}

    @Override
    public void explainCommand(String message, Command command) {
        if (command != null) {
            sendMessage("> Usage : " + command.usage());
            sendMessage("> Short description : " + command.desc());
            sendMessage("> Error : " + message);
            sendMessage("> Permitted flags : " + Arrays.toString(command.flags()));
        }
    }

    @Override
    public boolean hasPermission(Permissions permission) {
        return true;
    }

    @Override
    public void execute(String cmd) {
        DarwinServer.getUtilChecked(PlayerUtils.class).executeCmd(cmd, this);
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
        sendMessage(LegacyText.fromLegacy(message));
    }

    @Override
    public void sendMessage(String message, Permissions permission) {
        sendMessage(LegacyText.fromLegacy(message));
    }

    @Override
    public void sendMessage(Translations translation, String permission) {
        sendMessage(translation.s());
    }

    @Override
    public void sendMessage(Translations translation, Permissions permission) {
        sendMessage(translation.s());
    }

    @Override
    public void sendMessage(Text text, String permission) {
        sendMessage(text);
    }

    @Override
    public void sendMessage(Text text, Permissions permission) {
        sendMessage(text);
    }
}
