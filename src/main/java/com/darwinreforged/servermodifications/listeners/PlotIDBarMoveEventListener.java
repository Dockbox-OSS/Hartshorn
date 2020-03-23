package com.darwinreforged.servermodifications.listeners;

import com.darwinreforged.servermodifications.objects.PlotIDBarPlayer;
import com.darwinreforged.servermodifications.modules.PlotIdBarModule;
import com.darwinreforged.servermodifications.resources.Translations;
import com.google.common.base.Optional;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlotIDBarMoveEventListener {
    @Listener
    public void onMove(ClientConnectionEvent.Join event, @First Player player2) {
        checkLogin(event, player2);
    }

    @Listener
    public void onMove(MoveEntityEvent event, @First Player player2) {
        check(event, player2);
    }

    @Listener
    public void onTeleport(MoveEntityEvent.Teleport event, @First Player player2) {
        check(event, player2);
    }

    private void checkLogin(ClientConnectionEvent.Join event, Player player) {
        com.intellectualcrafters.plot.object.Location loc =
                new com.intellectualcrafters.plot.object.Location();
        loc.setX(event.getTargetEntity().getTransform().getLocation().getBlockX());
        loc.setY(event.getTargetEntity().getTransform().getLocation().getBlockY());
        loc.setZ(event.getTargetEntity().getTransform().getLocation().getBlockZ());
        loc.setWorld(
                event.getTargetEntity().getTransform().getLocation().getExtent().getName().toString());
        if (Plot.getPlot(loc) != null) {
            PlotIDBarPlayer barP = new PlotIDBarPlayer(player);

            Plot plot = Plot.getPlot(loc);
            if (barP.getIDBar() != null) {
                ServerBossBar oldBar1 = barP.getIDBar();
                oldBar1.removePlayer(player);
                barP.setIDBar(null);
            }
            if (barP.getMemBar() != null) {
                ServerBossBar oldBar2 = barP.getMemBar();
                barP.setMemBar(null);
                oldBar2.removePlayer(player);
            }
            barP.setLastPlot("");
            PlotIdBarModule.allPlayers.put(player.getUniqueId(), barP);
            doBar(plot, player);
        } else {
            renderBar(player);
        }
    }

    private void renderBar(Player player) {
        PlotIDBarPlayer barP = new PlotIDBarPlayer(player);
        if (barP.hasPlotTime) {
            Sponge.getCommandManager().process(player, "ptime reset");
            barP.setPlotTime(false);
        }
        if (barP.getIDBar() != null) {
            ServerBossBar oldBar1 = barP.getIDBar();
            oldBar1.removePlayer(player);
        }
        if (barP.getMemBar() != null) {
            ServerBossBar oldBar2 = barP.getMemBar();
            oldBar2.removePlayer(player);
        }
        barP.setLastPlot("");
        PlotIdBarModule.allPlayers.put(player.getUniqueId(), barP);
    }

    private void check(MoveEntityEvent event, Player player) {
        com.intellectualcrafters.plot.object.Location loc =
                new com.intellectualcrafters.plot.object.Location();
        loc.setX(event.getToTransform().getLocation().getBlockX());
        loc.setY(event.getToTransform().getLocation().getBlockY());
        loc.setZ(event.getToTransform().getLocation().getBlockZ());
        loc.setWorld(event.getToTransform().getLocation().getExtent().getName().toString());
        if (Plot.getPlot(loc) != null) {
            Plot plot = Plot.getPlot(loc);
            doBar(plot, player);
        } else {
            renderBar(player);
        }
    }

    private void doBar(Plot plot, Player player) {
        ServerBossBar bossBarA, bossBarB;
        PlotIDBarPlayer barP = new PlotIDBarPlayer(player);
        PlotId ID = plot.getId();
        Optional<String> isDone = plot.getFlag(Flags.DONE);
        Object done = false;
        if (isDone.isPresent()) {
            done = isDone.get();
        }

        Set<UUID> owner = plot.getOwners();
        String actualowner;

        if (barP.getBarBool()) {

            if (barP.getIDBar() != null) {
                ServerBossBar idBar = barP.getIDBar();
                idBar.removePlayer(player);
            }
            if (barP.getMemBar() != null) {
                ServerBossBar membersBar = barP.getIDBar();
                membersBar.removePlayer(player);
            }
            return;
        }

        if (barP.getMembersBool()) {

            if (barP.getMemBar() != null) {
                ServerBossBar membersBar = barP.getIDBar();
                membersBar.removePlayer(player);
            }
        }
        try {
            actualowner = PlotIdBarModule.getUser(owner.iterator().next()).get().getName();
        } catch (Exception e) {
            actualowner = Translations.UNOWNED.s();
        }
        if (actualowner.equals(Translations.UNOWNED.s())) {

            if (barP.getIDBar() != null) {
                ServerBossBar oldBar1 = barP.getIDBar();
                oldBar1.removePlayer(player);
            }
            if (barP.getMemBar() != null) {
                ServerBossBar oldBar2 = barP.getMemBar();
                oldBar2.removePlayer(player);
            }
            barP.setLastPlot("");
            PlotIdBarModule.allPlayers.put(player.getUniqueId(), barP);

            return;
        }
        String worldname = plot.getWorldName();
        HashSet<UUID> trusted = plot.getTrusted();

        Object[] trustedArray = trusted.toArray();
        String firstTrusted = null, secondTrusted = null, thirdTrusted = null;
        int somenumber = 0;
        if (trustedArray.length > 0 && trustedArray[0] != null) {
            if (PlotIdBarModule.getUser(UUID.fromString(trustedArray[0].toString())).isPresent()) {
                firstTrusted = PlotIdBarModule.getUser(UUID.fromString(trustedArray[0].toString())).get().getName();
                somenumber += 1;
                //	System.out.println(firstTrusted);
            }
        }
        if (trustedArray.length > 1 && trustedArray[1] != null) {
            if (PlotIdBarModule.getUser(UUID.fromString(trustedArray[1].toString())).isPresent()) {
                secondTrusted = PlotIdBarModule.getUser(UUID.fromString(trustedArray[1].toString())).get().getName();
                somenumber += 1;
                // System.out.println(secondTrusted);
            }
        }
        String members = null;
        Boolean hasmembers = false;
        if (firstTrusted != null) {
            hasmembers = true;
            members = firstTrusted;
        }
        if (somenumber == 2) {
            if (trusted.size() > 2) {
                members = Translations.PID_USERS_TRUSTED_MORE.f(firstTrusted, secondTrusted, (trusted.size() - 2));
            } else {
                members = Translations.PID_USERS_TRUSTED.f(firstTrusted, secondTrusted);
            }
        }

        for (UUID uuid : trusted) {
            if (!PlotIdBarModule.getUser(uuid).isPresent()) {
                members = Translations.EVERYONE.s();
                break;
            }
        }

        boolean isWorld = plot.getWorldName().replaceAll(",", ";").equals(plot.getId().toString());

        Text IDM;
        if (isWorld) {
            IDM = Translations.PID_WORLD_FORMAT.ft(ID);
        } else {
            IDM = Translations.PID_PLOT_FORMAT.ft(worldname, ID);
        }

        Text OwnerName = Translations.PID_OWNER_FORMAT.ft(actualowner);

        if (!plot.getAlias().isEmpty()) {
            IDM = Text.of(plot.getAlias().replaceAll("&", "§").replaceAll("_", " "));
        }

        Text separator = Translations.PID_BAR_SEPARATOR.t();

        boolean hideOwner = plot.getFlag(Flags.HIDE_OWNER).or(false);

        Text nameText = hideOwner ? Text.of(TextColors.DARK_AQUA, IDM) : Text.of(TextColors.DARK_AQUA, IDM, separator, OwnerName);
        bossBarA =
                ServerBossBar.builder()
                        .name(nameText)
                        .percent(1f)
                        .color(BossBarColors.WHITE)
                        .overlay(BossBarOverlays.PROGRESS)
                        .build();
        bossBarB =
                ServerBossBar.builder()
                        .name(Translations.PID_BAR_MEMBERS.ft(members))
                        .percent(1f)
                        .color(BossBarColors.BLUE)
                        .overlay(BossBarOverlays.PROGRESS)
                        .build();

        // System.out.println("PlotIDBarPlayer " + barP.getLastPlot());
        // System.out.println("WORLDNAME " + worldname + ";" + plot.getId());
        if (!barP.getLastPlot().equals(worldname + plot.getId())) {
            // System.out.println("Updating");

            barP.setLastPlot(worldname + plot.getId().toString());
            if (plot.getFlag(Flags.TIME).isPresent()) {
                if (!barP.hasPlotTime) {
                    Sponge.getCommandManager()
                            .process(player, "ptime set " + plot.getFlag(Flags.TIME).get().intValue());
                    barP.setPlotTime(true);
                }
            } else {
                if (barP.hasPlotTime) {
                    Sponge.getCommandManager().process(player, "ptime reset");
                    barP.setPlotTime(false);
                }
            }
            if (barP.getIDBar() != null) {
                ServerBossBar oldBar1 = barP.getIDBar();
                oldBar1.removePlayer(player);
            }
            if (barP.getMemBar() != null) {
                ServerBossBar oldBar2 = barP.getMemBar();
                oldBar2.removePlayer(player);
            }

            bossBarA.addPlayer(player);
            barP.setIDBar(bossBarA);
            if (!barP.getMembersBool() && trusted.size() > 0) {
                boolean doRenderMemberBar = true;
                if (plot.getFlag(Flags.MEMBER_BAR).isPresent())
                    doRenderMemberBar = plot.getFlag(Flags.MEMBER_BAR).get();
                if (doRenderMemberBar) {
                    bossBarB.addPlayer(player);
                    barP.setMemBar(bossBarB);
                    barP.setLastPlot(worldname + plot.getId());
                }
            }
            PlotIdBarModule.allPlayers.put(player.getUniqueId(), barP);
        } else {
            if (bossBarA != barP.getIDBar()) {
                barP.setLastPlot(worldname + plot.getId());
                PlotIdBarModule.allPlayers.put(player.getUniqueId(), barP);
            }
        }
    }
}
