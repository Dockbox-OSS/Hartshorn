/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.sponge.plotsquared;

import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.object.Plot;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.plots.PlotMembership;
import org.dockbox.hartshorn.plots.flags.PlotFlag;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Direction;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.dockbox.hartshorn.util.ReferencedWrapper;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class SpongePlot extends ReferencedWrapper<Plot> implements org.dockbox.hartshorn.plots.Plot {

    private final Location center;
    private final int plotX;
    private final int plotY;

    public SpongePlot(Plot plot) {
        this.center = SpongeConversionUtil.fromPlotSquared(plot.getCenter());
        this.plotX = plot.getId().x;
        this.plotY = plot.getId().y;
        plot.getCenter();
        this.setReference(this.constructInitialReference());
    }

    @Override
    public Exceptional<Player> getOwner() {
        if (this.referenceExists()) {
            Plot plot = this.getReference().get();
            if (plot.getOwners().isEmpty()) return Exceptional.none();
            UUID ownerUuid = plot.getOwners().iterator().next();
            return Hartshorn.context().get(Players.class).getPlayer(ownerUuid);
        }
        throw new IllegalStateException("Reference plot at " + this.center.getWorld().getName() + ";" + this.plotX + "," + this.plotY + " could not be found");
    }

    @Override
    public Collection<Player> getPlayers(PlotMembership membership) {
        Players service = Hartshorn.context().get(Players.class);
        return this.getUUIDs(membership).stream()
                .map(service::getPlayer)
                .filter(Exceptional::present)
                .map(Exceptional::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasMembership(Player player, PlotMembership membership) {
        return this.getUUIDs(membership).contains(player.getUniqueId());
    }

    @Override
    public boolean hasAnyMembership(Player player, PlotMembership... memberships) {
        for (PlotMembership membership : memberships) {
            if (this.hasMembership(player, membership)) return true;
        }
        return false;
    }

    @Override
    public Map<PlotFlag<?>, Object> getFlags() {
        Map<PlotFlag<?>, Object> flags = HartshornUtils.emptyMap();
        this.getReference().get().getFlags().forEach(((flag, o) ->
                SpongePlotSquaredService.getFlag(flag.getName())
                        .present(plotFlag -> flags.put(plotFlag, o)))
        );
        return flags;
    }

    @Override
    public <T> void addFlag(PlotFlag<T> flag, T value) {
        this.getReference().present(plot -> SpongePlotSquaredService.getPlotSquaredFlag(flag.getId()).present(plotFlag -> plot.setFlag(plotFlag, value)));
    }

    @Override
    public void removeFlag(PlotFlag<?> flag) {
        this.getReference().present(plot -> SpongePlotSquaredService.getPlotSquaredFlag(flag.getId()).present(plot::removeFlag));
    }

    @Override
    public <T> Exceptional<T> getFlag(PlotFlag<T> flag) {
        if (this.getReference().present()) {
            Flag<?> plotFlag = SpongePlotSquaredService.getPlotSquaredFlag(flag.getId()).orNull();
            if (plotFlag == null) return Exceptional.none();
            //noinspection unchecked
            return Exceptional.of(this.getReference().get().getFlag(plotFlag).transform(value -> (T) value).orNull());
        }
        return Exceptional.none();
    }

    @Override
    public Location getHome() {
        if (this.getReference().present()) {
            return SpongeConversionUtil.fromPlotSquared(this.getReference().get().getHome());
        }
        return Location.empty();
    }

    @Override
    public Exceptional<org.dockbox.hartshorn.plots.Plot> getRelative(Direction direction) {
        if (this.getReference().present()) {
            return Exceptional.of(new SpongePlot(this.getReference().get().getRelative(direction.ordinal())));
        }
        return Exceptional.none();
    }

    @Override
    public boolean isWorld() {
        return this.getReference()
                .map(plot -> plot.getWorldName().replaceAll(",", ";").equals(plot.getId().toString()))
                .or(false);
    }

    private Set<UUID> getUUIDs(PlotMembership membership) {
        Set<UUID> uuids = new HashSet<>();
        switch (membership) {
            case OWNER:
                uuids = this.getReference().get().getOwners();
                break;
            case TRUSTED:
                uuids = this.getReference().get().getTrusted();
                break;
            case MEMBER:
                uuids = this.getReference().get().getMembers();
                break;
            case DENIED:
                uuids = this.getReference().get().getDenied();
                break;
        }
        return uuids;
    }

    @Override
    public Exceptional<Plot> constructInitialReference() {
        if (this.center == null) return Exceptional.none();
        return Exceptional.of(Plot.getPlot(SpongeConversionUtil.toPlotSquared(this.center)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpongePlot)) return false;
        SpongePlot that = (SpongePlot) o;
        return this.plotX == that.plotX && this.plotY == that.plotY && Objects.equals(this.center, that.center);
    }
}