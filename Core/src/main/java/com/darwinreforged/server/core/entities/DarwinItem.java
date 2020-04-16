package com.darwinreforged.server.core.entities;

import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.util.ItemUtils;

public class DarwinItem<I> {

    I itemReference;

    public DarwinItem(I itemReference) {
        this.itemReference = itemReference;
    }

    @SuppressWarnings("unchecked")
    public void setLore(String[] lore) {
        this.itemReference = (I) DarwinServer.getUtilChecked(ItemUtils.class).setLore(lore, this);
    }

    @SuppressWarnings("unchecked")
    public void setDisplayName(String displayName) {
        this.itemReference = (I) DarwinServer.getUtilChecked(ItemUtils.class).setDisplayName(displayName, this);
    }

    @SuppressWarnings("unchecked")
    public String getDisplayName() {
        return DarwinServer.getUtilChecked(ItemUtils.class).getDisplayName(this);
    }

    public I getItemReference() {
        return itemReference;
    }

    public void setItemReference(I itemReference) {
        this.itemReference = itemReference;
    }
}
