package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.commands.LayerheightCommand;
import com.darwinreforged.servermodifications.listeners.LayerheightPlaceEventListener;
import org.spongepowered.api.Sponge;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;

import org.spongepowered.api.event.game.state.GameStartingServerEvent;

import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(id = "darwinlayerheight", name = "Darwin Layer Height", version = "1.0", description = "Easy to use layer heights")
public class LayerheightPlugin {
	public LayerheightPlugin() {
	}

	@Listener
    public void onServerFinishLoad(GameStartingServerEvent event) {
    	Sponge.getCommandManager().register(this, layerHeightMain, "layerheight");
    	Sponge.getEventManager().registerListeners(this, new LayerheightPlaceEventListener());
	}
	//pls update
    CommandSpec layerHeightMain = CommandSpec.builder()
    	    .description(Text.of("Sets a layer height name for a block."))
    	    .permission("layerHeight.use")
    	    .arguments(GenericArguments.integer(Text.of("1 to 8")))
    	    .executor(new LayerheightCommand())
    	    .build();
}
