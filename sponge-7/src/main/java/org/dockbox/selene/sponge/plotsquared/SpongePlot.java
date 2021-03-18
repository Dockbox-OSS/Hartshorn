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

package org.dockbox.selene.sponge.plotsquared;

import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.object.Plot;

import org.dockbox.selene.api.PlayerStorageService;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.ReferencedWrapper;
import org.dockbox.selene.api.objects.location.Direction;
import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.plots.PlotMembership;
import org.dockbox.selene.plots.flags.PlotFlag;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongePlot extends ReferencedWrapper<Plot> implements org.dockbox.selene.plots.Plot {

    private final Location center;
    private final int x;
    private final int y;

    public SpongePlot(Plot plot) {
        this.center = SpongeConversionUtil.fromPlotSquared(plot.getCenter());
        this.x = plot.getId().x;
        this.y = plot.getId().y;
        plot.getCenter();
        this.setReference(this.constructInitialReference());
    }

    @Override
    public Player getOwner() {
        if (referenceExists()) {
            Plot plot = getReference().get();
            UUID ownerUuid = plot.getOwners().iterator().next();
            return Selene.provide(PlayerStorageService.class).getPlayer(ownerUuid).orNull();
        }
        throw new IllegalStateException("Reference plot at " + center.getWorld().getName() + ";" + x + "," + y + " could not be found");
    }

    @Override
    public Collection<Player> getPlayers(PlotMembership membership) {
        PlayerStorageService service = Selene.provide(PlayerStorageService.class);
        return getUUIDs(membership).stream()
                .map(service::getPlayer)
                .filter(Exceptional::isPresent)
                .map(Exceptional::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasMembership(Player player, PlotMembership membership) {
        return getUUIDs(membership).contains(player.getUniqueId());
    }

    @Override
    public Map<PlotFlag<?>, Object> getFlags() {
        Map<PlotFlag<?>, Object> flags = SeleneUtils.emptyMap();
        getReference().get().getFlags().forEach(((flag, o) ->
                SpongePlotSquaredService.getFlag(flag.getName())
                        .ifPresent(plotFlag -> flags.put(plotFlag, o)))
        );
        return flags;
    }

    @Override
    public <T> void addFlag(PlotFlag<T> flag, T value) {
        getReference().ifPresent(plot -> SpongePlotSquaredService.getPlotSquaredFlag(flag.getId()).ifPresent(plotFlag -> plot.setFlag(plotFlag, value)));
    }

    @Override
    public void removeFlag(PlotFlag<?> flag) {
        getReference().ifPresent(plot -> SpongePlotSquaredService.getPlotSquaredFlag(flag.getId()).ifPresent(plot::removeFlag));
    }

    @Override
    public <T> Exceptional<T> getFlag(PlotFlag<T> flag) {
        if (getReference().isPresent()) {
            Flag<?> plotFlag = SpongePlotSquaredService.getPlotSquaredFlag(flag.getId()).orNull();
            if (plotFlag == null) return Exceptional.empty();
            //noinspection unchecked
            return Exceptional.of(getReference().get().getFlag(plotFlag).transform(value -> (T) value));
        }
        return Exceptional.empty();
    }

    @Override
    public int getPlotX() {
        return this.x;
    }

    @Override
    public int getPlotY() {
        return this.y;
    }

    @Override
    public Location getHome() {
        if (getReference().isPresent()) {
            return SpongeConversionUtil.fromPlotSquared(getReference().get().getHome());
        }
        return Location.empty();
    }

    @Override
    public Location getCenter() {
        return this.center;
    }

    @Override
    public Exceptional<org.dockbox.selene.plots.Plot> getRelative(Direction direction) {
        if (getReference().isPresent()) {
            return Exceptional.of(new SpongePlot(getReference().get().getRelative(direction.ordinal())));
        }
        return Exceptional.empty();
    }

    @Override
    public boolean isWorld() {
        return getReference()
                .map(plot -> plot.getWorldName().replaceAll(",", ";").equals(plot.getId().toString()))
                .orElse(false);
    }

    private Set<UUID> getUUIDs(PlotMembership membership) {
        Set<UUID> uuids = new HashSet<>();
        switch (membership) {
            case OWNER:
                uuids = getReference().get().getOwners();
                break;
            case TRUSTED:
                uuids = getReference().get().getTrusted();
                break;
            case MEMBER:
                uuids = getReference().get().getMembers();
                break;
            case DENIED:
                uuids = getReference().get().getDenied();
                break;
        }
        return uuids;
    }

    @Override
    public Exceptional<Plot> constructInitialReference() {
        if (this.center == null) return Exceptional.empty();
        return Exceptional.ofNullable(Plot.getPlot(SpongeConversionUtil.toPlotSquared(this.center)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpongePlot)) return false;
        SpongePlot that = (SpongePlot) o;
        return x == that.x && y == that.y && Objects.equals(center, that.center);
    }
}
