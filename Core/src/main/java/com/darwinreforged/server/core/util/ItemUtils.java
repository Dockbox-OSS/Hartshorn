package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.types.living.inventory.DarwinItem;
import com.darwinreforged.server.core.init.AbstractUtility;

/**
 The type Item utils.

 @param <I>
 the type parameter
 */
@AbstractUtility("ItemStack manipulation utilities")
public abstract class ItemUtils<I> {

    /**
     Sets display name.

     @param displayName
     the display name
     @param in
     the in

     @return the display name
     */
    public abstract I setDisplayName(String displayName, DarwinItem<I> in);

    /**
     Sets lore.

     @param lore
     the lore
     @param in
     the in

     @return the lore
     */
    public abstract I setLore(String[] lore, DarwinItem<I> in);

    /**
     Gets display name.

     @param in
     the in

     @return the display name
     */
    public abstract String getDisplayName(DarwinItem<I> in);

}
