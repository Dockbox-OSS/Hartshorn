package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.commands.MultiCommandExecutor;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(
        id = "multicommand",
        name = "Multi Command",
        version = "0.1.1",
        description = "Execute multiple commands at once with a single command",
        authors = {
                "DiggyNevs"
        }
)
public class MultiCommandPlugin {

    @Inject
    private Logger logger;

    static MultiCommandPlugin instance;

    public static MultiCommandPlugin getInstance() {
        return instance;
    }

    public MultiCommandPlugin() {
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        MultiCommandPlugin.instance = this;

        CommandSpec multiCommand = CommandSpec.builder()
                .permission("mc.use")
                .executor(new MultiCommandExecutor())
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("commands")))
                .build();

        Sponge.getCommandManager().register(this, multiCommand, "multi", "/multi");
    }


}
