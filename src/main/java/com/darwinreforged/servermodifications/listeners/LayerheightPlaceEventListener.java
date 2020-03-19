package com.darwinreforged.servermodifications.listeners;

import com.intellectualcrafters.plot.object.Plot;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableLayeredData;
import org.spongepowered.api.data.manipulator.mutable.block.LayeredData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;


public class LayerheightPlaceEventListener {
	@Listener
	public void onBlockPlace(ChangeBlockEvent.Place event) {
		Optional<Player> cause = event.getCause().first(Player.class);
		Player player;

		if (cause.isPresent()) {
			player = cause.get();
			for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
				if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
					ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();
					if (item.get(Keys.DISPLAY_NAME).isPresent()) {
						String name = item.get(Keys.DISPLAY_NAME).get().toPlain();
						if (name.toLowerCase().contains("layer height tool")) {
							BlockSnapshot block = transaction.getFinal();
							Location<World> loc = block.getLocation().get();
							com.intellectualcrafters.plot.object.Location plotLoc = new com.intellectualcrafters.plot.object.Location();
							plotLoc.setX(loc.getBlockX());
							plotLoc.setY(loc.getBlockY());
							plotLoc.setZ(loc.getBlockZ());
							plotLoc.setWorld(player.getLocation().getExtent().getName());
							if (Plot.getPlot(plotLoc) != null) {
								Plot plot = Plot.getPlot(plotLoc);
								if (plot.isAdded(player.getUniqueId()) || player.hasPermission("plots.admin.build.other")) {
									Text name2 = item.get(Keys.DISPLAY_NAME).get();
									String temp = name2.toPlain();
									temp = temp.replaceAll("Layer Height Tool: ", "");
									temp = temp.replaceAll(" ", "");
									int height = Integer.parseInt(temp);
									BlockState state = BlockTypes.SNOW_LAYER.getDefaultState();
									Optional<ImmutableLayeredData> data = state.get(ImmutableLayeredData.class);
									if (data.isPresent()) {
										LayeredData snowData = data.get().asMutable();
										snowData.set(Keys.LAYER, height);
										BlockState newState = state.with(snowData.asImmutable()).get();
										loc.setBlock(newState);
									}
								}
							} else {
								player.sendMessage(Text.of("Not in a plot"));
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
}
