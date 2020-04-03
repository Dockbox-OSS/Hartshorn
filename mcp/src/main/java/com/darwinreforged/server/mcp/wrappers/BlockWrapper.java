package com.darwinreforged.server.mcp.wrappers;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
public class BlockWrapper  extends Wrapper<Block> {
    
    public BlockWrapper(MaterialWrapper blockMaterialIn, MapColorWrapper blockMapColorIn) {
        set(new Block(blockMaterialIn.get(), blockMapColorIn.get()));
    }
    public BlockWrapper(MaterialWrapper materialIn) {
        set(new Block(materialIn.get()));
    }

    public static int getIdFromBlock(BlockWrapper blockIn) {
        return Block.getIdFromBlock(blockIn.get());
    }
    public static int getStateId(IBlockState state) {
        return Block.getStateId(state);
    }
    public static Block getBlockById(int id) {
        return Block.getBlockById(id);
    }
    public static IBlockState getStateById(int id) {
        return Block.getStateById(id);
    }
    public static Block getBlockFromItem(@Nullable Item itemIn) {
        return Block.getBlockFromItem(itemIn);
    }
    @Nullable
    public static Block getBlockFromName(String name) {
        return Block.getBlockFromName(name);
    }
    @Deprecated
    public boolean isTopSolid(IBlockState state) {
        return get().isTopSolid(state);
    }
    
    @Deprecated
    public boolean isFullBlock(IBlockState state) {
        return get().isFullBlock(state);
    }
    
    @Deprecated
    public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
        return get().canEntitySpawn(state, entityIn);
    }
    
    @Deprecated
    public int getLightOpacity(IBlockState state) {
        return get().getLightOpacity(state);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public boolean isTranslucent(IBlockState state) {
        return get().isTranslucent(state);
    }
    
    @Deprecated
    public int getLightValue(IBlockState state) {
        return get().getLightValue(state);
    }
    
    @Deprecated
    public boolean getUseNeighborBrightness(IBlockState state) {
        return get().getUseNeighborBrightness(state);
    }
    
    @Deprecated
    public Material getMaterial(IBlockState state) {
        return get().getMaterial(state);
    }
    
    @Deprecated
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return get().getMapColor(state, worldIn, pos);
    }
    
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return get().getStateFromMeta(meta);
    }
    
    public int getMetaFromState(IBlockState state) {
        return get().getMetaFromState(state);
    }
    
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return get().getActualState(state, worldIn, pos);
    }
    
    @Deprecated
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return get().withRotation(state, rot);
    }
    
    @Deprecated
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return get().withMirror(state, mirrorIn);
    }
    
    public Block setLightOpacity(int opacity) {
        return get().setLightOpacity(opacity);
    }
    
    public Block setLightLevel(float value) {
        return get().setLightLevel(value);
    }
    
    public Block setResistance(float resistance) {
        return get().setResistance(resistance);
    }
    
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return get().isBlockNormalCube(state);
    }
    
    @Deprecated
    public boolean isNormalCube(IBlockState state) {
        return get().isNormalCube(state);
    }
    
    @Deprecated
    public boolean causesSuffocation(IBlockState state) {
        return get().causesSuffocation(state);
    }
    
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return get().isFullCube(state);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return get().hasCustomBreakingProgress(state);
    }
    
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return get().isPassable(worldIn, pos);
    }
    
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return get().getRenderType(state);
    }
    
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return get().isReplaceable(worldIn, pos);
    }
    
    public Block setHardness(float hardness) {
        return get().setHardness(hardness);
    }
    
    public Block setBlockUnbreakable() {
        return get().setBlockUnbreakable();
    }
    
    @Deprecated
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return get().getBlockHardness(blockState, worldIn, pos);
    }
    
    public Block setTickRandomly(boolean shouldTick) {
        return get().setTickRandomly(shouldTick);
    }
    
    public boolean getTickRandomly() {
        return get().getTickRandomly();
    }
    
    @Deprecated
    public boolean hasTileEntity() {
        return get().hasTileEntity();
    }
    
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return get().getBoundingBox(state, source, pos);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        return get().getPackedLightmapCoords(state, source, pos);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return get().shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
    
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return get().getBlockFaceShape(worldIn, state, pos, face);
    }
    
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        get().addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    }
    
    @Nullable
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return get().getCollisionBoundingBox(blockState, worldIn, pos);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return get().getSelectedBoundingBox(state, worldIn, pos);
    }
    
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return get().isOpaqueCube(state);
    }
    
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return get().canCollideCheck(state, hitIfLiquid);
    }
    
    public boolean isCollidable() {
        return get().isCollidable();
    }
    
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        get().randomTick(worldIn, pos, state, random);
    }
    
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        get().updateTick(worldIn, pos, state, rand);
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        get().randomDisplayTick(stateIn, worldIn, pos, rand);
    }
    
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state) {
        get().onPlayerDestroy(worldIn, pos, state);
    }
    
    @Deprecated
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        get().neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }
    
    public int tickRate(World worldIn) {
        return get().tickRate(worldIn);
    }
    
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        get().onBlockAdded(worldIn, pos, state);
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        get().breakBlock(worldIn, pos, state);
    }
    
    public int quantityDropped(Random random) {
        return get().quantityDropped(random);
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return get().getItemDropped(state, rand, fortune);
    }
    
    @Deprecated
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
        return get().getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }
    
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        get().dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }
    public static void spawnAsEntity(World worldIn, BlockPos pos, ItemStack stack) {
        Block.spawnAsEntity(worldIn, pos, stack);
    }
    
    public void dropXpOnBlockBreak(World worldIn, BlockPos pos, int amount) {
        get().dropXpOnBlockBreak(worldIn, pos, amount);
    }
    
    public int damageDropped(IBlockState state) {
        return get().damageDropped(state);
    }
    
    @Deprecated
    public float getExplosionResistance(Entity exploder) {
        return get().getExplosionResistance(exploder);
    }
    
    @Nullable
    @Deprecated
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return get().collisionRayTrace(blockState, worldIn, pos, start, end);
    }
    
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        get().onExplosionDestroy(worldIn, pos, explosionIn);
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return get().getRenderLayer();
    }
    
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return get().canPlaceBlockOnSide(worldIn, pos, side);
    }
    
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return get().canPlaceBlockAt(worldIn, pos);
    }
    
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return get().onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
    
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        get().onEntityWalk(worldIn, pos, entityIn);
    }
    
    @Deprecated
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return get().getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }
    
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        get().onBlockClicked(worldIn, pos, playerIn);
    }
    
    public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
        return get().modifyAcceleration(worldIn, pos, entityIn, motion);
    }
    
    @Deprecated
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return get().getWeakPower(blockState, blockAccess, pos, side);
    }
    
    @Deprecated
    public boolean canProvidePower(IBlockState state) {
        return get().canProvidePower(state);
    }
    
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        get().onEntityCollision(worldIn, pos, state, entityIn);
    }
    
    @Deprecated
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return get().getStrongPower(blockState, blockAccess, pos, side);
    }
    
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        get().harvestBlock(worldIn, player, pos, state, te, stack);
    }
    
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return get().quantityDroppedWithBonus(fortune, random);
    }
    
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        get().onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }
    
    public boolean canSpawnInBlock() {
        return get().canSpawnInBlock();
    }
    
    public Block setTranslationKey(String key) {
        return get().setTranslationKey(key);
    }
    
    public String getLocalizedName() {
        return get().getLocalizedName();
    }
    
    public String getTranslationKey() {
        return get().getTranslationKey();
    }
    
    @Deprecated
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        return get().eventReceived(state, worldIn, pos, id, param);
    }
    
    public boolean getEnableStats() {
        return get().getEnableStats();
    }
    
    @Deprecated
    public EnumPushReaction getPushReaction(IBlockState state) {
        return get().getPushReaction(state);
    }
    
    @SideOnly(Side.CLIENT)
    @Deprecated
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return get().getAmbientOcclusionLightValue(state);
    }
    
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        get().onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }
    
    public void onLanded(World worldIn, Entity entityIn) {
        get().onLanded(worldIn, entityIn);
    }
    
    @Deprecated
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return get().getItem(worldIn, pos, state);
    }
    
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        get().getSubBlocks(itemIn, items);
    }
    
    public CreativeTabs getCreativeTab() {
        return get().getCreativeTab();
    }
    
    public Block setCreativeTab(CreativeTabs tab) {
        return get().setCreativeTab(tab);
    }
    
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        get().onBlockHarvested(worldIn, pos, state, player);
    }
    
    public void fillWithRain(World worldIn, BlockPos pos) {
        get().fillWithRain(worldIn, pos);
    }
    
    public boolean requiresUpdates() {
        return get().requiresUpdates();
    }
    
    public boolean canDropFromExplosion(Explosion explosionIn) {
        return get().canDropFromExplosion(explosionIn);
    }
    
    public boolean isAssociatedBlock(Block other) {
        return get().isAssociatedBlock(other);
    }
    public static boolean isEqualTo(Block blockIn, Block other) {
        return Block.isEqualTo(blockIn, other);
    }
    
    @Deprecated
    public boolean hasComparatorInputOverride(IBlockState state) {
        return get().hasComparatorInputOverride(state);
    }
    
    @Deprecated
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return get().getComparatorInputOverride(blockState, worldIn, pos);
    }
    
    public BlockStateContainer getBlockState() {
        return get().getBlockState();
    }
    
    public Block.EnumOffsetType getOffsetType() {
        return get().getOffsetType();
    }
    
    @Deprecated
    public Vec3d getOffset(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return get().getOffset(state, worldIn, pos);
    }
    
    @Deprecated
    public SoundType getSoundType() {
        return get().getSoundType();
    }
    
    public String toString() {
        return get().toString();
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        get().addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity entity) {
        return get().getSlipperiness(state, world, pos, entity);
    }
    
    public void setDefaultSlipperiness(float slipperiness) {
        get().setDefaultSlipperiness(slipperiness);
    }
    
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().getLightValue(state, world, pos);
    }
    
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return get().isLadder(state, world, pos, entity);
    }
    
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().isNormalCube(state, world, pos);
    }
    
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return get().doesSideBlockRendering(state, world, pos, face);
    }
    
    @Deprecated
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return get().isSideSolid(base_state, world, pos, side);
    }
    
    public boolean isBurning(IBlockAccess world, BlockPos pos) {
        return get().isBurning(world, pos);
    }
    
    public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().isAir(state, world, pos);
    }
    
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return get().canHarvestBlock(world, pos, player);
    }
    
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return get().removedByPlayer(state, world, pos, player, willHarvest);
    }
    
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return get().getFlammability(world, pos, face);
    }
    
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return get().isFlammable(world, pos, face);
    }
    
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return get().getFireSpreadSpeed(world, pos, face);
    }
    
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return get().isFireSource(world, pos, side);
    }
    
    public boolean hasTileEntity(IBlockState state) {
        return get().hasTileEntity(state);
    }
    
    @Nullable
    public TileEntity createTileEntity(World world, IBlockState state) {
        return get().createTileEntity(world, state);
    }
    
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return get().quantityDropped(state, fortune, random);
    }
    
    @Deprecated
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return get().getDrops(world, pos, state, fortune);
    }
    
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        get().getDrops(drops, world, pos, state, fortune);
    }
    
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return get().canSilkHarvest(world, pos, state, player);
    }
    
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return get().canCreatureSpawn(state, world, pos, type);
    }
    
    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player) {
        return get().isBed(state, world, pos, player);
    }
    
    @Nullable
    public BlockPos getBedSpawnPosition(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityPlayer player) {
        return get().getBedSpawnPosition(state, world, pos, player);
    }
    
    public void setBedOccupied(IBlockAccess world, BlockPos pos, EntityPlayer player, boolean occupied) {
        get().setBedOccupied(world, pos, player, occupied);
    }
    
    public EnumFacing getBedDirection(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().getBedDirection(state, world, pos);
    }
    
    public boolean isBedFoot(IBlockAccess world, BlockPos pos) {
        return get().isBedFoot(world, pos);
    }
    
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos) {
        get().beginLeavesDecay(state, world, pos);
    }
    
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().canSustainLeaves(state, world, pos);
    }
    
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().isLeaves(state, world, pos);
    }
    
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().canBeReplacedByLeaves(state, world, pos);
    }
    
    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return get().isWood(world, pos);
    }
    
    public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target) {
        return get().isReplaceableOreGen(state, world, pos, target);
    }
    
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return get().getExplosionResistance(world, pos, exploder, explosion);
    }
    
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        get().onBlockExploded(world, pos, explosion);
    }
    
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return get().canConnectRedstone(state, world, pos, side);
    }
    
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().canPlaceTorchOnTop(state, world, pos);
    }
    
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return get().getPickBlock(state, target, world, pos, player);
    }
    
    public boolean isFoliage(IBlockAccess world, BlockPos pos) {
        return get().isFoliage(world, pos);
    }
    
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        return get().addLandingEffects(state, worldObj, blockPosition, iblockstate, entity, numberOfParticles);
    }
    
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        return get().addRunningEffects(state, world, pos, entity);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return get().addHitEffects(state, worldObj, target, manager);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return get().addDestroyEffects(world, pos, manager);
    }
    
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return get().canSustainPlant(state, world, pos, direction, plantable);
    }
    
    public void onPlantGrow(IBlockState state, World world, BlockPos pos, BlockPos source) {
        get().onPlantGrow(state, world, pos, source);
    }
    
    public boolean isFertile(World world, BlockPos pos) {
        return get().isFertile(world, pos);
    }
    
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().getLightOpacity(state, world, pos);
    }
    
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return get().canEntityDestroy(state, world, pos, entity);
    }
    
    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
        return get().isBeaconBase(worldObj, pos, beacon);
    }
    
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return get().rotateBlock(world, pos, axis);
    }
    
    @Nullable
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return get().getValidRotations(world, pos);
    }
    
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return get().getEnchantPowerBonus(world, pos);
    }
    
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        return get().recolorBlock(world, pos, side, color);
    }
    
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        return get().getExpDrop(state, world, pos, fortune);
    }
    
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        get().onNeighborChange(world, pos, neighbor);
    }
    
    public void observedNeighborChange(IBlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos) {
        get().observedNeighborChange(observerState, world, observerPos, changedBlock, changedBlockPos);
    }
    
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return get().shouldCheckWeakPower(state, world, pos, side);
    }
    
    public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
        return get().getWeakChanges(world, pos);
    }
    
    public void setHarvestLevel(String toolClass, int level) {
        get().setHarvestLevel(toolClass, level);
    }
    
    public void setHarvestLevel(String toolClass, int level, IBlockState state) {
        get().setHarvestLevel(toolClass, level, state);
    }
    
    @Nullable
    public String getHarvestTool(IBlockState state) {
        return get().getHarvestTool(state);
    }
    
    public int getHarvestLevel(IBlockState state) {
        return get().getHarvestLevel(state);
    }
    
    public boolean isToolEffective(String type, IBlockState state) {
        return get().isToolEffective(type, state);
    }
    
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().getExtendedState(state, world, pos);
    }
    
    @Nullable
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
        return get().isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
    }
    
    @Nullable
    public Boolean isAABBInsideMaterial(World world, BlockPos pos, AxisAlignedBB boundingBox, Material materialIn) {
        return get().isAABBInsideMaterial(world, pos, boundingBox, materialIn);
    }
    
    @Nullable
    public Boolean isAABBInsideLiquid(World world, BlockPos pos, AxisAlignedBB boundingBox) {
        return get().isAABBInsideLiquid(world, pos, boundingBox);
    }
    
    public float getBlockLiquidHeight(World world, BlockPos pos, IBlockState state, Material material) {
        return get().getBlockLiquidHeight(world, pos, state, material);
    }
    
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return get().canRenderInLayer(state, layer);
    }
    
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return get().getSoundType(state, world, pos, entity);
    }
    
    @Nullable
    public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
        return get().getBeaconColorMultiplier(state, world, pos, beaconPos);
    }
    
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
        return get().getFogColor(world, pos, state, entity, originalColor, partialTicks);
    }
    
    public IBlockState getStateAtViewpoint(IBlockState state, IBlockAccess world, BlockPos pos, Vec3d viewpoint) {
        return get().getStateAtViewpoint(state, world, pos, viewpoint);
    }
    
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return get().getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
    }
    
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return get().canBeConnectedTo(world, pos, facing);
    }
    
    @Deprecated
    @Nullable
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos) {
        return get().getAiPathNodeType(state, world, pos);
    }
    
    @Nullable
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityLiving entity) {
        return get().getAiPathNodeType(state, world, pos, entity);
    }
    
    public boolean doesSideBlockChestOpening(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return get().doesSideBlockChestOpening(blockState, world, pos, side);
    }
    
    public boolean isStickyBlock(IBlockState state) {
        return get().isStickyBlock(state);
    }
    public static void registerBlocks() {
        Block.registerBlocks();
    }
}
