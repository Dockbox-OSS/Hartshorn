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

package org.dockbox.selene.core.impl.objects.keys;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.Key;
import org.dockbox.selene.core.objects.keys.Keys;
import org.dockbox.selene.core.objects.player.Gamemode;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.targets.AbstractIdentifiable;
import org.dockbox.selene.core.text.Text;

import java.util.List;
import java.util.UUID;

@SuppressWarnings({"rawtypes"})
public final class GenericKeys {

    public static final Key<AbstractIdentifiable, UUID> UNIQUE_ID = Keys.unsafeDynamicKeyOf((k, s) -> {}, AbstractIdentifiable::getUniqueId);
    public static final Key<AbstractIdentifiable, String> NAME = Keys.unsafeDynamicKeyOf(AbstractIdentifiable::setName, AbstractIdentifiable::getName);

    public static final Key<Player, Gamemode> GAMEMODE = Keys.unsafeDynamicKeyOf(Player::setGamemode, Player::getGamemode);
    public static final Key<Player, Language> LANGUAGE = Keys.unsafeDynamicKeyOf(Player::setLanguage, Player::getLanguage);

    public static final Key<Item, Text> DISPLAY_NAME = Keys.unsafeDynamicKeyOf(Item::setDisplayName, Item::getDisplayName, Item::removeDisplayName);
    public static final Key<Item, Integer> AMOUNT = Keys.unsafeDynamicKeyOf(Item::setAmount, Item::getAmount);
    public static final Key<Item, List<Text>> LORE = Keys.unsafeDynamicKeyOf(Item::setLore, Item::getLore, Item::removeLore);

    private GenericKeys() {
    }

}
