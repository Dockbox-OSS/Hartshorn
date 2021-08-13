package org.dockbox.hartshorn.sponge.inventory;

import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO: #369 Implement this
@AllArgsConstructor
public class SpongeInventoryLayout implements InventoryLayout {

    @Getter
    private final InventoryType inventoryType;

    @Override
    public Map<Integer, Element> elements() {
        return null;
    }
}
