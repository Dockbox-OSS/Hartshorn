package com.darwinreforged.server.core.types.living;

import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.resources.Permissions;

/**
 The type Command sender.
 */
public abstract class CommandSender extends Target implements MessageReceiver {

    /**
     Explain command.

     @param message
     the message
     @param command
     the command
     */
    public abstract void explainCommand(String message, Command command);

    /**
     Has permission boolean.

     @param permission
     the permission

     @return the boolean
     */
    public abstract boolean hasPermission(Permissions permission);
}
