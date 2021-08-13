package org.dockbox.hartshorn.sponge.inventory.panes;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.StaticPane;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;

import java.util.Optional;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SpongeStaticPane implements StaticPane {

    private final InventoryMenu menu;

    @Override
    public void open(final Player player) {
        final Exceptional<ServerPlayer> serverPlayer = SpongeConvert.toSponge(player);
        // Only open if the player is still online, it's possible the user logged off
        serverPlayer.present(this.menu::open);
    }

    @Override
    public void set(final Element element, final int index) {
        this.set(element.item(), index);
        this.menu.registerSlotClick((cause, container, slot, slotIndex, clickType) -> {
            if (slotIndex == index) {
                final Optional<ServerPlayer> player = cause.first(ServerPlayer.class);
                if (player.isEmpty()) return false;
                final Player origin = SpongeConvert.fromSponge(player.get());
                element.perform(origin);
            }
            return false;
        });
    }

    @Override
    public void set(final Item item, final int index) {
        this.menu.inventory().set(index, SpongeConvert.toSponge(item));
    }

    @Override
    public void update(final InventoryLayout layout) {
        layout.elements().forEach((index, element) -> this.set(element, index));
    }
}
