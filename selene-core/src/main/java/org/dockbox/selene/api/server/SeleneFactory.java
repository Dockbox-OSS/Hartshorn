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

package org.dockbox.selene.api.server;

import org.dockbox.selene.api.entities.ArmorStand;
import org.dockbox.selene.api.entities.ItemFrame;
import org.dockbox.selene.api.i18n.permissions.Permission;
import org.dockbox.selene.api.i18n.permissions.PermissionContext;
import org.dockbox.selene.api.inventory.Element;
import org.dockbox.selene.api.objects.bossbar.Bossbar;
import org.dockbox.selene.api.objects.bossbar.BossbarColor;
import org.dockbox.selene.api.objects.bossbar.BossbarStyle;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.text.Text;

import java.util.UUID;
import java.util.function.Consumer;

public interface SeleneFactory {

    Item item(String id, int meta);

    Element element(Item item, Consumer<Player> onClick);

    Bossbar bossbar(String id, float percent, Text text, BossbarColor color, BossbarStyle style);

    Profile profile(UUID uuid);

    Profile profile(Profile profile);

    Permission permission(String key, PermissionContext context);

    Permission permission(String key);

    ArmorStand armorStand(Location location);

    ItemFrame itemFrame(Location location);

}
