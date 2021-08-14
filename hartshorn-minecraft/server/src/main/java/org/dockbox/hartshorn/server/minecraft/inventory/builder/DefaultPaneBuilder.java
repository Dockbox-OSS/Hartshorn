package org.dockbox.hartshorn.server.minecraft.inventory.builder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.server.minecraft.inventory.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.Pane;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
public abstract class DefaultPaneBuilder<T extends Pane, B extends DefaultPaneBuilder<T, B>> implements PaneBuilder<T, B> {

    private BiConsumer<Player, T> onClose = (player, pane) -> {};
    private boolean lock = false;

    private final Multimap<Integer, Function<ClickContext, Boolean>> listeners = ArrayListMultimap.create();

    @Override
    public B onClose(final BiConsumer<Player, T> onClose) {
        this.onClose = onClose;
        return this.self();
    }

    @Override
    public B onClick(final int index, final Function<ClickContext, Boolean> onClick) {
        this.listeners.put(index, onClick);
        return this.self();
    }

    public B lock(final boolean lock) {
        this.lock = lock;
        return this.self();
    }

    protected abstract B self();

}
