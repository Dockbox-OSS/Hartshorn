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

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.generator.SquarePlotWorld;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotBlock;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.plots.Plot;
import org.dockbox.selene.plots.PlotService;
import org.dockbox.selene.plots.flags.PlotFlag;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;

import java.util.Map;
import java.util.function.BiConsumer;

public class SpongePlotSquaredService implements PlotService {

    private static final Map<String, PlotFlag<?>> flagRegistrations = SeleneUtils.emptyConcurrentMap();

    @Override
    public Exceptional<Plot> getPlotAt(Location location) {
        com.intellectualcrafters.plot.object.Plot plot = com.intellectualcrafters.plot.object.Plot.getPlot(SpongeConversionUtil.toPlotSquared(location));
        return Exceptional.ofNullable(plot).map(SpongePlot::new);
    }

    @Override
    public Exceptional<Plot> getCurrentPlot(Player player) {
        com.intellectualcrafters.plot.object.Plot plot = SpongeConversionUtil.toPlotSquared(player).getCurrentPlot();
        return Exceptional.ofNullable(plot).map(SpongePlot::new);
    }

    @Override
    public void registerFlag(PlotFlag<?> flag) {
        Flag<?> plotSquaredFlag = new SpongePlotSquaredCustomFlag<>(flag);
        flagRegistrations.put(flag.getId(), flag);
        Flags.registerFlag(plotSquaredFlag);
    }

    @Override
    public Exceptional<Plot> getPlot(World world, int x, int y) {
        for (com.intellectualcrafters.plot.object.Plot plot : PS.get().getPlots(world.getName())) {
            if (plot.getId().x == x && plot.getId().y == y) return Exceptional.of(new SpongePlot(plot));
        }
        return Exceptional.empty();
    }

    @Override
    public void setFilling(Plot plot, Item item) {
        setBlocks(plot, item, "main");
    }

    @Override
    public void setFloor(Plot plot, Item item) {
        setBlocks(plot, item, "floor");
    }

    @Override
    public void setAir(Plot plot, Item item) {
        setBlocks(plot, item, "air");
    }

    @Override
    public void setAll(Plot plot, Item item) {
        setBlocks(plot, item, "all");
    }

    @Override
    public void setWallBorder(Plot plot, Item item) {
        setBlocks(plot, item, "border");
    }

    @Override
    public void setWallFilling(Plot plot, Item item) {
        setBlocks(plot, item, "wall");
    }

    @Override
    public void setOutline(Plot plot, Item item) {
        setBlocks(plot, item, "outline");
    }

    @Override
    public void setMiddle(Plot plot, Item item) {
        setBlocks(plot, item, "middle");
    }

    private void setBlocks(Plot plot, Item item, String component) {
        SpongeConversionUtil.toPlotSquared(item).ifPresent(block -> executeWithPlot(plot, item,
                (p, $) -> p.setComponent(component, new PlotBlock[]{block}))
        );
    }

    @Override
    public Integer getSize(Plot plot) {
        // Negative is used if the plot is a full world, or is unobtainable as the type of plot is unmanaged (not SpongePlot)
        int negative = -1;
        if (plot instanceof SpongePlot) {
            return ((SpongePlot) plot).getReference().map(p -> {
                PlotArea area = p.getArea();
                if (area instanceof SquarePlotWorld) {
                    return ((SquarePlotWorld) area).PLOT_WIDTH;
                }
                return negative;
            }).orElse(negative);
        }
        return negative;
    }

    @Override
    public Text getAlias(Plot plot) {
        if (plot instanceof SpongePlot) {
            return Text.of(((SpongePlot) plot).getReference().get().getAlias());
        }
        return Text.of();
    }

    @Override
    public void setAlias(Plot plot, Text alias) {
        if (plot instanceof SpongePlot) {
            ((SpongePlot) plot).getReference().get().setAlias(alias.toLegacy().replaceAll(" ", "_"));
        }
    }

    private <T> void executeWithPlot(Plot plot, T value, BiConsumer<com.intellectualcrafters.plot.object.Plot, T> consumer) {
        if (plot instanceof SpongePlot) {
            Exceptional<com.intellectualcrafters.plot.object.Plot> reference = ((SpongePlot) plot).getReference();
            if (reference.isPresent()) {
                consumer.accept(reference.get(), value);
            }
        }
    }

    protected static Exceptional<Flag<?>> getPlotSquaredFlag(String id) {
        return Exceptional.ofNullable(Flags.getFlag(id));
    }

    protected static Exceptional<PlotFlag<?>> getFlag(String id) {
        if (flagRegistrations.containsKey(id)) {
            return Exceptional.ofNullable(flagRegistrations.get(id));
        } else {
            return getPlotSquaredFlag(id).map(SpongeFlagWrapper::new);
        }
    }
}
