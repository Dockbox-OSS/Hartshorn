package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.entities.living.inventory.DarwinItem;
import com.darwinreforged.server.core.init.AbstractUtility;

@AbstractUtility("ItemStack manipulation utilities")
public abstract class ItemUtils<I> {

    public abstract I setDisplayName(String displayName, DarwinItem<I> in);

    public abstract I setLore(String[] lore, DarwinItem<I> in);

    public abstract String getDisplayName(DarwinItem<I> in);

}
