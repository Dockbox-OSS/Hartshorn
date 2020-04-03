package com.darwinreforged.server.mcp.wrappers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;

import javax.annotation.Nullable;

public class ItemWrapper extends Wrapper<Item> {

  public ItemWrapper(Item item) {
    set(item);
  }

  public boolean hasCustomProperties() {
    return get().hasCustomProperties();
  }

  public ItemWrapper setMaxStackSize(int maxStackSize) {
    get().setMaxStackSize(maxStackSize);
    return this;
  }

  public float getDestroySpeed(ItemStack stack, IBlockState state) {
    return get().getDestroySpeed(stack, state);
  }

  @Deprecated
  public int getItemStackLimit() {
    return get().getItemStackLimit();
  }

  public int getMetadata(int damage) {
    return get().getMetadata(damage);
  }

  public boolean getHasSubtypes() {
    return get().getHasSubtypes();
  }

  public ItemWrapper setHasSubtypes(boolean hasSubtypes) {
    get().setHasSubtypes(hasSubtypes);
    return this;
  }

  public int getMaxDamage() {
    return get().getMaxDamage();
  }

  public ItemWrapper setMaxDamage(int maxDamageIn) {
    get().setMaxDamage(maxDamageIn);
    return this;
  }

  public boolean isDamageable() {
    return get().isDamageable();
  }

  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    return get().hitEntity(stack, target, attacker);
  }

  public ItemWrapper setFull3D() {
    get().setFull3D();
    return this;
  }

  public boolean isFull3D() {
    return get().isFull3D();
  }

  public boolean shouldRotateAroundWhenRendering() {
    return get().shouldRotateAroundWhenRendering();
  }

  public ItemWrapper setTranslationKey(String key) {
    get().setTranslationKey(key);
    return this;
  }

  public String getTranslationKey() {
    return get().getTranslationKey();
  }

  public boolean getShareTag() {
    return get().getShareTag();
  }

  @Nullable
  public ItemWrapper getContainerItem() {
    return new ItemWrapper(get().getContainerItem());
  }

  @Deprecated
  public boolean hasContainerItem() {
    return get().hasContainerItem();
  }

  public boolean isMap() {
    return get().isMap();
  }

  public int getItemEnchantability() {
    return get().getItemEnchantability();
  }

  public boolean canItemEditBlocks() {
    return get().canItemEditBlocks();
  }

  public boolean isRepairable() {
    return get().isRepairable();
  }

  public ItemWrapper setNoRepair() {
    get().setNoRepair();
    return this;
  }

  public void setHarvestLevel(String toolClass, int level) {
    get().setHarvestLevel(toolClass, level);
  }
}
