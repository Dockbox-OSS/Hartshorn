package org.dockbox.selene.sponge.external;

import org.dockbox.selene.core.external.pattern.Mask;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.Wrapper;
import org.jetbrains.annotations.NotNull;

public class WrappedMask implements Mask, Wrapper<com.sk89q.worldedit.function.mask.Mask> {

    private com.sk89q.worldedit.function.mask.Mask mask;

    public WrappedMask(com.sk89q.worldedit.function.mask.Mask mask) {
        this.mask = mask;
    }

    @Override
    public Exceptional<com.sk89q.worldedit.function.mask.Mask> getReference() {
        return Exceptional.ofNullable(this.mask);
    }

    @Override
    public void setReference(@NotNull Exceptional<com.sk89q.worldedit.function.mask.Mask> reference) {
        reference.ifPresent(mask -> this.mask = mask);
    }

    @Override
    public Exceptional<com.sk89q.worldedit.function.mask.Mask> constructInitialReference() {
        return Exceptional.empty();
    }
}
