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

package org.dockbox.hartshorn.palswap.fileparsers;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.hartshorn.palswap.BlockIdentifier;
import org.dockbox.hartshorn.palswap.BlockRegistryUtil;
import org.dockbox.hartshorn.palswap.VariantIdentifier;
import org.dockbox.hartshorn.server.minecraft.MinecraftVersion;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

@Singleton
public class MC1_12BlockRegistryParser extends BlockRegistryParser {

    public static final Pattern idInformationRegex = Pattern.compile("(\\w*:\\w*):?(\\d*)?");

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.MC1_12;
    }

    @Override
    protected List<String> loadMinecraftItemRawIDs(String filename) {
        List<String> rawIDs = new ArrayList<>();
        int lineIndex = 0;
        String id;
        int meta = 0;

        for (String line : this.parseFile(filename)) {
            lineIndex++;
            if (1 == lineIndex) {
                meta = Integer.parseInt(line.split("\\t")[2]);
            }
            else if (3 == lineIndex) {
                id = line.substring(1, line.length() - 3);
                rawIDs.add(id + ":" + meta);
                lineIndex = 0;
            }
        }
        return rawIDs;
    }

    @Override
    protected List<String> loadConquestItemRawIDs(String filename) {
        List<String> rawIDs = new ArrayList<>();

        for (String line : this.parseFile(filename)) {
            String[] parts = line.split("\\.");
            String parent = parts[0];

            for (int i = 1; i < parts.length; i++) {
                String variant = parts[i];
                variant = variant.replaceFirst("variant\\(", "");
                variant = variant.substring(0, variant.length() - 1)
                        .replaceAll(" ", "");
                variant = variant.replaceAll("\"", "");
                String[] metaAndName = variant.split(",");
                int meta = Integer.parseInt(metaAndName[0]);

                rawIDs.add("conquest:" + parent + ":" + meta);
            }
        }
        return rawIDs;
    }

    @Override
    public Exceptional<String> determineBlockIdentifier(Item item) {
        String name = item.getDisplayName(Language.EN_US).toStringValue();
        @NonNls String blockIdentifier = VariantIdentifier.getBlockNameWithoutVariant(name);

        //If the name is the same, check that its a fullblock and not an invalid item (E.g: plant, food, etc)
        if (name.equalsIgnoreCase(blockIdentifier)) {
            Exceptional<VariantIdentifier> eVariantIdentifier = VariantIdentifier.ofName(name);

            if (eVariantIdentifier.present() && VariantIdentifier.FULL != eVariantIdentifier.get())
                return Exceptional.none();
        }
        return Exceptional.of(BlockIdentifier.formatBlockName(name));
    }

    @Override
    public String getItemID(Item item) {
        return BlockRegistryUtil.getUpdatedID(item);
    }

    /**
     * @param rawID
     *         The id of the {@link Item} in the format modId:itemId:meta
     *
     * @return The {@link Item} of the provided id.
     */
    @Override
    public Item getItemFromRawID(String rawID) {
        Matcher matcher = idInformationRegex.matcher(rawID);

        if (matcher.matches()) {
            // If there's no meta, then assume its 0.
            int meta = null == matcher.group(2) ? 0 : Integer.parseInt(matcher.group(2));
            return Item.of(matcher.group(1), meta);
        }
        return MinecraftItems.getInstance().getAir();
    }
}
