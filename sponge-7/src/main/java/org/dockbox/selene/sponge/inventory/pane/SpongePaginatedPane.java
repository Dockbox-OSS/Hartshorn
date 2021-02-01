/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.inventory.pane;

import org.dockbox.selene.core.PlatformConversionService;
import org.dockbox.selene.core.inventory.Element;
import org.dockbox.selene.core.inventory.pane.PaginatedPane;
import org.dockbox.selene.core.objects.player.Player;

import java.util.Collection;
import java.util.stream.Collectors;

import dev.flashlabs.flashlibs.inventory.Page;

public class SpongePaginatedPane implements PaginatedPane {

    private final Page page;

    public SpongePaginatedPane(Page initializedPage) {
        this.page = initializedPage;
    }

    @Override
    public void open(Player player, int page) {
        PlatformConversionService.<Player, org.spongepowered.api.entity.living.player.Player>mapSafely(player).ifPresent(p -> {
            this.page.open(p, page);
        });
    }

    @Override
    public void elements(Collection<Element> elements) {
        this.page.define(elements.stream()
                .map(PlatformConversionService::map)
                .map(dev.flashlabs.flashlibs.inventory.Element.class::cast)
                .collect(Collectors.toList())
        );
    }

    @Override
    public void open(Player player) {
        PlatformConversionService.<Player, org.spongepowered.api.entity.living.player.Player>mapSafely(player)
                .ifPresent(this.page::open);
    }
}
