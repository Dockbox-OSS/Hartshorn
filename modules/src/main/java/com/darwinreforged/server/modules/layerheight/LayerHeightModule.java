package com.darwinreforged.server.modules.layerheight;

import com.darwinreforged.server.api.DarwinServer;
import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModule;
import com.darwinreforged.server.api.resources.Permissions;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.text.Text;

@ModuleInfo(id = "darwinlayerheight", name = "Darwin Layer Height", version = "1.0", description = "Easy to use layer heights")
public class LayerHeightModule extends PluginModule {
	public LayerHeightModule() {
	}

	@Override
	public void onServerFinishLoad(GameInitializationEvent event) {
		DarwinServer.registerCommand(layerHeightMain, "layerheight");
		DarwinServer.registerListener(new LayerheightPlaceEventListener());
	}

	//pls update
    CommandSpec layerHeightMain = CommandSpec.builder()
    	    .permission(Permissions.LAYERHEIGHT_USE.p())
    	    .arguments(GenericArguments.integer(Text.of("1 to 8")))
    	    .executor(new LayerheightCommand())
    	    .build();
}
