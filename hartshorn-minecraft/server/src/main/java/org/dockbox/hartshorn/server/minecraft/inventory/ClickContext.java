package org.dockbox.hartshorn.server.minecraft.inventory;

import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.Pane;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClickContext extends DefaultContext {
    private final Player player;
    private final Item item;
    private final Pane pane;

    public void close() {
        this.pane.close(this.player);
    }
}
