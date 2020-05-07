package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.entities.living.inventory.DarwinItem;
import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.util.ItemUtils;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityImplementation(ItemUtils.class)
public class SpongeItemUtils extends ItemUtils<ItemStack> {


    @Override
    public ItemStack setDisplayName(String displayName, DarwinItem<ItemStack> in) {
        ItemStack itemStack = in.getItemReference();
        itemStack.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserializeUnchecked(displayName));
        return itemStack;
    }

    @Override
    public ItemStack setLore(String[] lore, DarwinItem<ItemStack> in) {
        ItemStack itemStack = in.getItemReference();
        List<Text> loreText = Arrays.stream(lore).map(TextSerializers.FORMATTING_CODE::deserializeUnchecked).collect(Collectors.toList());
        itemStack.offer(Keys.ITEM_LORE, loreText);
        return itemStack;
    }

    @Override
    public String getDisplayName(DarwinItem<ItemStack> in) {
        return in.getItemReference().get(Keys.DISPLAY_NAME).orElse(Text.of()).toString();
    }
}
