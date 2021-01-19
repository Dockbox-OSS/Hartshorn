package org.dockbox.selene.sponge.external;

import org.dockbox.selene.core.external.pattern.Pattern;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.Wrapper;
import org.jetbrains.annotations.NotNull;

public class WrappedPattern implements Pattern, Wrapper<com.sk89q.worldedit.function.pattern.Pattern> {

    private com.sk89q.worldedit.function.pattern.Pattern pattern;

    public WrappedPattern(com.sk89q.worldedit.function.pattern.Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Exceptional<com.sk89q.worldedit.function.pattern.Pattern> getReference() {
        return Exceptional.ofNullable(this.pattern);
    }

    @Override
    public void setReference(@NotNull Exceptional<com.sk89q.worldedit.function.pattern.Pattern> reference) {
        reference.ifPresent(pattern -> this.pattern = pattern);
    }

    @Override
    public Exceptional<com.sk89q.worldedit.function.pattern.Pattern> constructInitialReference() {
        return Exceptional.empty();
    }
}
