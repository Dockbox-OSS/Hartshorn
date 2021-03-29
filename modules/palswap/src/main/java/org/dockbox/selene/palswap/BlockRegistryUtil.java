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

package org.dockbox.selene.palswap;

import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.palswap.fileparsers.BlockRegistryParser;
import org.dockbox.selene.palswap.fileparsers.MC1_12BlockRegistryParser;

public final class BlockRegistryUtil {

    private BlockRegistryUtil() {}

    public static Class<? extends BlockRegistryParser> getBlockRegistryParserClass() {
        //noinspection SwitchStatementWithTooFewBranches
        switch (Selene.getServer().getMinecraftVersion()) {
            default:
                return MC1_12BlockRegistryParser.class;
        }
    }

    public static String getUpdatedID(Item item) {
        return String.format("%s:%s_%s",
                getModID(item), formatItemName(item), getItemVariant(item));
    }

    public static String getModID(Item item) {
        String id = item.getId();
        return id.substring(0, id.indexOf(':'));
    }

    public static String formatItemName(Item item) {
        return VariantIdentifier.getBlockNameWithoutVariant(
                item.getDisplayName(Language.EN_US).toStringValue()
                    .toLowerCase()
                    .replace(' ', '_')
                    .replace("^", "up_arrow")
                    .replace("/", "forward_slash")
                    .replace("\\", "back_slash")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("-", "_")
                    .replace("and", "")
                    .replace("with", "")
                    .replace("vertical_connection", "ctm")
                    .replace("round_top", "round"));
    }

    public static String getItemVariant(Item item) {
        return VariantIdentifier.ofRawItem(item).or(VariantIdentifier.FULL)
                .toString()
                .toLowerCase()
                .replace("_", "");
    }
}


