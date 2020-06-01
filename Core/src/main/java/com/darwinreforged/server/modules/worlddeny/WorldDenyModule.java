package com.darwinreforged.server.modules.worlddeny;

import com.darwinreforged.server.core.events.internal.player.PlayerMoveEvent;
import com.darwinreforged.server.core.events.internal.player.PlayerTeleportEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.translations.Translation;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;

import java.util.Optional;

@Module(id = "worlddeny", name = "World Deny", description = "Denies teleportation actions if the player is not added to a world", authors = {"GuusLieben", "TheCrunchy"})
public class WorldDenyModule {

    private final Translation WD_NOT_PERMITTED = Translation.create("error_not_permitted", "$4You are not allowed to teleport to that world as you are denied from it!");

    @Listener
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTarget() instanceof DarwinPlayer) {
            DarwinLocation location = event.getNewLocation();
            boolean cancel = doCancelTeleport((DarwinPlayer) event.getTarget(), location);
            if (cancel) {
                event.setCancelled(true);
                ((DarwinPlayer) event.getTarget()).sendMessage(WD_NOT_PERMITTED, false);
            }
        }
    }

    @Listener
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTarget() instanceof DarwinPlayer) {
            Optional<DarwinLocation> locationCandidate = ((DarwinPlayer) event.getTarget()).getLocation();
            if (locationCandidate.isPresent()) {
                boolean cancel = doCancelTeleport((DarwinPlayer) event.getTarget(), locationCandidate.get());
                if (cancel) {
                    event.setCancelled(true);
                    ((DarwinPlayer) event.getTarget()).sendMessage(WD_NOT_PERMITTED, false);
                }
            }
        }
    }

    private boolean doCancelTeleport(DarwinPlayer player, DarwinLocation newLocation) {
        Location plotLoc = new Location(newLocation.getWorld().getName(), newLocation.getX().intValue(), newLocation.getY().intValue(), newLocation.getZ().intValue());
        Plot plot = Plot.getPlot(plotLoc);
        if (plot != null) {
            if (plot.isDenied(player.getUniqueId()) && !player.hasPermission(Permissions.ADMIN_BYPASS) && plot.getWorldName().matches("[0-9]+,[0-9]+")) {
                player.getLocation().ifPresent(loc -> {
                    if (loc.getWorld().getWorldUUID().equals(newLocation.getWorld().getWorldUUID()))
                        player.execute("/lobby");
                });
                return true;
            }
        }
        return false;
    }

}
