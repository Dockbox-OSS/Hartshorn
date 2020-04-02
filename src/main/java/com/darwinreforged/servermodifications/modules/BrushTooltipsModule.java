package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.listeners.BrushTooltipListener;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
import com.darwinreforged.servermodifications.resources.Permissions;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.text.Text;

@ModuleInfo(
        id = "brushtooltips",
        name = "Brush Tooltips",
        version = "0.1.10b",
        description =
                "Show tooltips on WorldEdit brushes, FAWE integration enabled")
public class BrushTooltipsModule extends PluginModule {

    public BrushTooltipsModule() {
    }

    @Override
    public void onServerFinishLoad(GameInitializationEvent event) {
        DarwinServer.registerListener(new BrushTooltipListener());
        DarwinServer.registerCommand(btMain, "bt");
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        BrushTooltipListener.brushAliases.put("s", "sphere");
        BrushTooltipListener.brushAliases.put("c", "cylinder");
    }

    private CommandSpec btRefresh = CommandSpec.builder()
            .description(Text.of("Refreshes BT for player"))
            .permission(Permissions.BRUSH_TT_REFRESH.p())
            .executor(new BrushTooltipRefresh())
            .build();

    private CommandSpec btMain = CommandSpec.builder()
            .description(Text.of("Main BT command"))
            .permission(Permissions.BRUSH_TT_USE.p())
            .child(btRefresh)
            .build();

    private static class BrushTooltipRefresh implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) {
            if (src instanceof Player) BrushTooltipListener.updateInventoryTooltips((Player) src);
            return CommandResult.success();
        }
    }
}
