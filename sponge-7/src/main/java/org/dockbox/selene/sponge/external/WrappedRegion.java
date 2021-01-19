package org.dockbox.selene.sponge.external;

import org.dockbox.selene.core.external.region.Region;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.Wrapper;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;

public class WrappedRegion implements Region, Wrapper<com.sk89q.worldedit.regions.Region> {

    private com.sk89q.worldedit.regions.Region region;

    public WrappedRegion(com.sk89q.worldedit.regions.Region region) {
        this.region = region;
    }

    @Override
    public Vector3N getMinimumPoint() {
        return SpongeConversionUtil.fromWorldEdit(this.region.getMinimumPoint());
    }

    @Override
    public Vector3N getMaximumPoint() {
        return SpongeConversionUtil.fromWorldEdit(this.region.getMaximumPoint());
    }

    @Override
    public Vector3N getCenter() {
        return SpongeConversionUtil.fromWorldEdit(this.region.getCenter());
    }

    @Override
    public int getArea() {
        return this.region.getArea();
    }

    @Override
    public int getWidth() {
        return this.region.getWidth();
    }

    @Override
    public int getHeight() {
        return this.region.getHeight();
    }

    @Override
    public int getLength() {
        return this.region.getHeight();
    }

    @Override
    public World getWorld() {
        return SpongeConversionUtil.fromWorldEdit(this.region.getWorld());
    }

    @Override
    public Exceptional<com.sk89q.worldedit.regions.Region> getReference() {
        return Exceptional.ofNullable(this.region);
    }

    @Override
    public void setReference(@NotNull Exceptional<com.sk89q.worldedit.regions.Region> reference) {
        reference.ifPresent(region -> this.region = region);
    }

    @Override
    public Exceptional<com.sk89q.worldedit.regions.Region> constructInitialReference() {
        return Exceptional.empty();
    }
}
