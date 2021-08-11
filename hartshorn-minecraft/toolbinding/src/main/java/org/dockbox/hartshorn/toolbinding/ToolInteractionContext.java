package org.dockbox.hartshorn.toolbinding;

import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.server.minecraft.Interactable;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.ClickType;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Sneaking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ToolInteractionContext extends DefaultContext {

    private final ToolInteractionEvent event;

    public Player player() {
        return this.event.player();
    }

    public Item item() {
        return this.event.item();
    }

    public ItemTool tool() {
        return this.event.tool();
    }

    public Hand hand() {
        return this.event.hand();
    }

    public ClickType type() {
        return this.event.type();
    }

    public Interactable target() {
        return this.event.target();
    }

    public Sneaking sneaking() {
        return this.event.sneaking();
    }
}
