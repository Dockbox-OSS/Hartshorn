package com.darwinreforged.servermodifications.plugins;

import com.intellectualcrafters.plot.object.Plot;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@Plugin(id = "zdarwinwater", name = "ca.coulis.zdarwinwater.ZDarwinWater", version = "1.0", description = "Change flowing water to static water")
public class WaterFlowPlugin {

    public WaterFlowPlugin() {
    }

    @Listener(order = Order.PRE)
    public void onLiquidPlaced(ChangeBlockEvent.Place event) {
        Optional<Player> player = event.getCause().first(Player.class);
        Location<World> loc = event.getTransactions().get(0).getOriginal().getLocation().get();
        com.intellectualcrafters.plot.object.Location pLoc = new com.intellectualcrafters.plot.object.Location(
                loc.getExtent().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ());
        Plot plot = Plot.getPlot(pLoc);

        if (!player.isPresent())
            return;

        if (plot != null) {
            if (!plot.getTrusted().contains(player.get().getUniqueId()) && !plot.getOwners().contains(player.get().getUniqueId()) && !plot.getMembers().contains(player.get().getUniqueId()) )
                if (!player.get().hasPermission("plots.admin.build.other"))
                    event.setCancelled(true);
        } else {
            if (!player.get().hasPermission("plots.admin.build.road"))
                event.setCancelled(true);
        }
    }

    @Listener(order = Order.POST)
    public void onLiquidFlow(ChangeBlockEvent.Pre event) {
        if (event.getLocations().isEmpty()) return;

        Location<World> loc = event.getLocations().get(0);
        com.intellectualcrafters.plot.object.Location pLoc = new com.intellectualcrafters.plot.object.Location(
                loc.getExtent().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ());
        Plot plot = Plot.getPlot(pLoc);
        BlockSnapshot snapshot = loc.getExtent().createSnapshot(loc.getBlockPosition());

        Optional<Player> player = event.getCause().first(Player.class);
        Optional<MatterProperty> matter = snapshot.getState().getProperty(MatterProperty.class);

        if(player.isPresent()) {
            if (!plot.getTrusted().contains(player.get().getUniqueId()) && !plot.getOwners().contains(player.get().getUniqueId()) && !plot.getMembers().contains(player.get().getUniqueId()) )
                if (!player.get().hasPermission("plots.admin.build.other"))
                    event.setCancelled(true);
        }

        if (matter.isPresent() && matter.get().getValue() == Matter.LIQUID) {
            if (plot == null || !plot.getFlag(com.intellectualcrafters.plot.flag.Flags.LIQUID_FLOW, false)) {
                event.setCancelled(true);
            }
        }

        if (plot == null)
            event.setCancelled(true);
    }

}
