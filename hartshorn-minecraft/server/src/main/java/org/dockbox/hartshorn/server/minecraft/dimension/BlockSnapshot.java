package org.dockbox.hartshorn.server.minecraft.dimension;

import org.dockbox.hartshorn.server.minecraft.Interactable;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BlockSnapshot implements Interactable {

    private final Block block;
    private final Location location;

}
