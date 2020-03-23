package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.commands.MultiCommandExecutor;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
import org.spongepowered.api.Sponge;
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
                .permission("mc.use")
                .executor(new MultiCommandExecutor())
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("commands")))
                .build();

        Sponge.getCommandManager().register(this, multiCommand, "multi", "/multi");
    }


}
