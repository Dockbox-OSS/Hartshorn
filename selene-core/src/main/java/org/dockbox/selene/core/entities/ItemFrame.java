package org.dockbox.selene.core.entities;

import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Item;

public interface ItemFrame extends Entity<ItemFrame> {

    Exceptional<Item> getDisplayedItem();

    void setDisplayedItem(Item stack);

    Rotation getRotation();

    void setRotation(Rotation rotation);

    enum Rotation {
        BOTTOM,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        RIGHT,
        TOP,
        TOP_LEFT,
        TOP_RIGHT
    }

}
