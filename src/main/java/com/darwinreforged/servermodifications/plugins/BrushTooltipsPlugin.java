package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.listeners.BrushTooltipListener;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(
        id = "brushtooltips",
        name = "Brush Tooltips",
        version = "0.1.10b",
        description =
                "Show tooltips on WorldEdit brushes, FAWE integration enabled")
public class BrushTooltipsPlugin {
    // General logger
    @Inject
    private Logger logger;

    public BrushTooltipsPlugin() {
    }

    public Logger getLogger() {
        return logger;
    }

    @Listener
    public void onServerFinishLoad(GameInitializationEvent event) {
        // Register our listener to WorldEdit (native) through FAWE
        System.out.println("Does this even trigger");
        Sponge.getEventManager().registerListeners(this, new BrushTooltipListener());
        Sponge.getCommandManager().register(this, btMain, "bt");
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        BrushTooltipListener.brushAliases.put("s", "sphere");
        BrushTooltipListener.brushAliases.put("c", "cylinder");
        logger.info("Loaded Brush Tooltips");
    }

    private CommandSpec btRefresh = CommandSpec.builder()
            .description(Text.of("Refreshes BT for player"))
            .permission("bt.refresh")
            .executor(new BrushTooltipRefresh())
            .build();

    private CommandSpec btMain = CommandSpec.builder()
            .description(Text.of("Main Dave command"))
            .child(btRefresh)
            .build();

    private class BrushTooltipRefresh implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) {
            if (src instanceof Player) BrushTooltipListener.updateInventoryTooltips((Player) src);
            return CommandResult.success();
        }
    }
}
