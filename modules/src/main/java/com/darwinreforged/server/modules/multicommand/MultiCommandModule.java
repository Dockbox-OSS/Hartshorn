package com.darwinreforged.server.modules.multicommand;


import com.darwinreforged.server.api.DarwinServer;
import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModule;
import com.darwinreforged.server.api.resources.Permissions;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.text.Text;

@ModuleInfo(
        id = "multicommand",
        name = "Multi Command",
        version = "0.1.1",
        description = "Execute multiple commands at once with a single command",
        authors = {
                "DiggyNevs"
        }
)
public class MultiCommandModule extends PluginModule {

    public MultiCommandModule() {
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandSpec multiCommand = CommandSpec.builder()
                .permission(Permissions.MULTI_CMD_USE.p())
                .executor(new MultiCommandExecutor())
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("commands")))
                .build();

        DarwinServer.registerCommand(multiCommand, "multi");
    }


}
