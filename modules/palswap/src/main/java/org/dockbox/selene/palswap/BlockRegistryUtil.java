package org.dockbox.selene.palswap;

import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.palswap.fileparsers.BlockRegistryParser;
import org.dockbox.selene.palswap.fileparsers.MC1_12BlockRegistryParser;

public final class BlockRegistryUtil {

    private BlockRegistryUtil() {}

    public static Class<? extends BlockRegistryParser> getBlockRegistryParserClass() {
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
                    .replace("vertical_connection", "ctm")
                    .replace("round_top", "round"));
    }

    public static String getItemVariant(Item item) {
        return VariantIdentifier.ofItem(item).toString()
                .toLowerCase()
                .replace("_", "");
    }
}

