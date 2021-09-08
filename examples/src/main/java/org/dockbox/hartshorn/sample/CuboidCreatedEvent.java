package org.dockbox.hartshorn.sample;

import org.dockbox.hartshorn.events.parents.ContextCarrierEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CuboidCreatedEvent extends ContextCarrierEvent {
    @Getter private final Cuboid cuboid;
}
