package com.darwinreforged.server.mcp.wrappers;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ItemStackWrapper extends Wrapper<ItemStack> {

    public ItemStackWrapper(BlockWrapper blockIn) {
        set(new ItemStack(blockIn.get()));
    }

    public ItemStackWrapper(BlockWrapper blockIn, int amount) {
        set(new ItemStack(blockIn.get(), amount));
    }

    public ItemStackWrapper(BlockWrapper blockIn, int amount, int meta) {
        set(new ItemStack(blockIn.get(), amount, meta));
    }

    public ItemStackWrapper(ItemWrapper itemIn) {
        set(new ItemStack(itemIn.get()));
    }

    public ItemStackWrapper(ItemWrapper itemIn, int amount) {
        set(new ItemStack(itemIn.get(), amount));
    }

    public ItemStackWrapper(ItemWrapper itemIn, int amount, int meta) {
        set(new ItemStack(itemIn.get(), amount, meta));
    }

    public ItemStackWrapper(ItemWrapper itemIn, int amount, int meta, @Nullable NBTTagCompound capNBT) {
        set(new ItemStack(itemIn.get(), amount, meta, capNBT));
    }

    public ItemStackWrapper(NBTTagCompound compound) {
        set(new ItemStack(compound));
    }

    public boolean isEmpty() {
        return get().isEmpty();
    }

    public static void registerFixes(DataFixer fixer) {
        ItemStack.registerFixes(fixer);
    }

    public ItemStack splitStack(int amount) {
        return get().splitStack(amount);
    }

    public Item getItem() {
        return get().getItem();
    }

    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return get().onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return get().onItemUseFirst(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
    }

    public float getDestroySpeed(IBlockState blockIn) {
        return get().getDestroySpeed(blockIn);
    }

    public ActionResult<ItemStack> useItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        return get().useItemRightClick(worldIn, playerIn, hand);
    }

    public ItemStack onItemUseFinish(World worldIn, EntityLivingBase entityLiving) {
        return get().onItemUseFinish(worldIn, entityLiving);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return get().writeToNBT(nbt);
    }

    public int getMaxStackSize() {
        return get().getMaxStackSize();
    }

    public boolean isStackable() {
        return get().isStackable();
    }

    public boolean isItemStackDamageable() {
        return get().isItemStackDamageable();
    }

    public boolean getHasSubtypes() {
        return get().getHasSubtypes();
    }

    public boolean isItemDamaged() {
        return get().isItemDamaged();
    }

    public int getItemDamage() {
        return get().getItemDamage();
    }

    public int getMetadata() {
        return get().getMetadata();
    }

    public void setItemDamage(int meta) {
        get().setItemDamage(meta);
    }

    public int getMaxDamage() {
        return get().getMaxDamage();
    }

    public boolean attemptDamageItem(int amount, Random rand, @Nullable EntityPlayerMP damager) {
        return get().attemptDamageItem(amount, rand, damager);
    }

    public void damageItem(int amount, EntityLivingBase entityIn) {
        get().damageItem(amount, entityIn);
    }

    public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn) {
        get().hitEntity(entityIn, playerIn);
    }

    public void onBlockDestroyed(World worldIn, IBlockState blockIn, BlockPos pos, EntityPlayer playerIn) {
        get().onBlockDestroyed(worldIn, blockIn, pos, playerIn);
    }

    public boolean canHarvestBlock(IBlockState blockIn) {
        return get().canHarvestBlock(blockIn);
    }

    public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn, EnumHand hand) {
        return get().interactWithEntity(playerIn, entityIn, hand);
    }

    public ItemStack copy() {
        return get().copy();
    }

    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

    public static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemStacksEqual(stackA, stackB);
    }

    public static boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemsEqual(stackA, stackB);
    }

    public static boolean areItemsEqualIgnoreDurability(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemsEqualIgnoreDurability(stackA, stackB);
    }

    public boolean isItemEqual(ItemStack other) {
        return get().isItemEqual(other);
    }

    public boolean isItemEqualIgnoreDurability(ItemStack stack) {
        return get().isItemEqualIgnoreDurability(stack);
    }

    public String getTranslationKey() {
        return get().getTranslationKey();
    }

    public void updateAnimation(World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem) {
        get().updateAnimation(worldIn, entityIn, inventorySlot, isCurrentItem);
    }

    public void onCrafting(World worldIn, EntityPlayer playerIn, int amount) {
        get().onCrafting(worldIn, playerIn, amount);
    }

    public int getMaxItemUseDuration() {
        return get().getMaxItemUseDuration();
    }

    public EnumAction getItemUseAction() {
        return get().getItemUseAction();
    }

    public void onPlayerStoppedUsing(World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        get().onPlayerStoppedUsing(worldIn, entityLiving, timeLeft);
    }

    public boolean hasTagCompound() {
        return get().hasTagCompound();
    }

    @Nullable
    public NBTTagCompound getTagCompound() {
        return get().getTagCompound();
    }

    public NBTTagCompound getOrCreateSubCompound(String key) {
        return get().getOrCreateSubCompound(key);
    }

    @Nullable
    public NBTTagCompound getSubCompound(String key) {
        return get().getSubCompound(key);
    }

    public void removeSubCompound(String key) {
        get().removeSubCompound(key);
    }

    public NBTTagList getEnchantmentTagList() {
        return get().getEnchantmentTagList();
    }

    public void setTagCompound(@Nullable NBTTagCompound nbt) {
        get().setTagCompound(nbt);
    }

    public String getDisplayName() {
        return get().getDisplayName();
    }

    public ItemStack setTranslatableName(String p_190924_1_) {
        return get().setTranslatableName(p_190924_1_);
    }

    public ItemStack setStackDisplayName(String displayName) {
        return get().setStackDisplayName(displayName);
    }

    public void clearCustomName() {
        get().clearCustomName();
    }

    public boolean hasDisplayName() {
        return get().hasDisplayName();
    }

    @SideOnly(Side.CLIENT)
    public List<String> getTooltip(@Nullable EntityPlayer playerIn, ITooltipFlag advanced) {
        return get().getTooltip(playerIn, advanced);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect() {
        return get().hasEffect();
    }

    @Deprecated
    public EnumRarity getRarity() {
        return get().getRarity();
    }

    public boolean isItemEnchantable() {
        return get().isItemEnchantable();
    }

    public void addEnchantment(Enchantment ench, int level) {
        get().addEnchantment(ench, level);
    }

    public boolean isItemEnchanted() {
        return get().isItemEnchanted();
    }

    public void setTagInfo(String key, NBTBase value) {
        get().setTagInfo(key, value);
    }

    public boolean canEditBlocks() {
        return get().canEditBlocks();
    }

    public boolean isOnItemFrame() {
        return get().isOnItemFrame();
    }

    public void setItemFrame(EntityItemFrame frame) {
        get().setItemFrame(frame);
    }

    @Nullable
    public EntityItemFrame getItemFrame() {
        return get().getItemFrame();
    }

    public int getRepairCost() {
        return get().getRepairCost();
    }

    public void setRepairCost(int cost) {
        get().setRepairCost(cost);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        return get().getAttributeModifiers(equipmentSlot);
    }

    public void addAttributeModifier(String attributeName, AttributeModifier modifier, @Nullable EntityEquipmentSlot equipmentSlot) {
        get().addAttributeModifier(attributeName, modifier, equipmentSlot);
    }

    public ITextComponent getTextComponent() {
        return get().getTextComponent();
    }

    public boolean canDestroy(Block blockIn) {
        return get().canDestroy(blockIn);
    }

    public boolean canPlaceOn(Block blockIn) {
        return get().canPlaceOn(blockIn);
    }

    public int getAnimationsToGo() {
        return get().getAnimationsToGo();
    }

    public void setAnimationsToGo(int animations) {
        get().setAnimationsToGo(animations);
    }

    public int getCount() {
        return get().getCount();
    }

    public void setCount(int size) {
        get().setCount(size);
    }

    public void grow(int quantity) {
        get().grow(quantity);
    }

    public void shrink(int quantity) {
        get().shrink(quantity);
    }

    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return get().hasCapability(capability, facing);
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return get().getCapability(capability, facing);
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        get().deserializeNBT(nbt);
    }

    public NBTTagCompound serializeNBT() {
        return get().serializeNBT();
    }

    public boolean areCapsCompatible(ItemStack other) {
        return get().areCapsCompatible(other);
    }

    public static boolean areItemStacksEqualUsingNBTShareTag(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemStacksEqualUsingNBTShareTag(stackA, stackB);
    }

    public static boolean areItemStackShareTagsEqual(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemStackShareTagsEqual(stackA, stackB);
    }

    public boolean doesSneakBypassUse(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return get().doesSneakBypassUse(world, pos, player);
    }
}
