package org.dockbox.hartshorn.server.minecraft.inventory;

import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Bound;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(onConstructor_ = @Bound)
@Binds(InventoryLayout.class)
@Getter
public class InventoryLayoutImpl implements InventoryLayout {
    private final InventoryType inventoryType;
    private final Map<Integer, Element> elements;
}
