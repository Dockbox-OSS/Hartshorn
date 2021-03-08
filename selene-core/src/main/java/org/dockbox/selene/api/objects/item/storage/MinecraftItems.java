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

package org.dockbox.selene.api.objects.item.storage;

import org.dockbox.selene.api.MinecraftVersion;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({ "unused", "OverlyComplexClass", "MethodMayBeStatic" })
public abstract class MinecraftItems {

    // Static as it is possible multiple instances of this type are created
    private static final Map<MinecraftVersion, Map<String, Supplier<Item>>> customItems =
            SeleneUtils.emptyConcurrentMap();

    public Item getCustom(String identifier) {
        Map<String, Supplier<Item>> customItemsForVersion =
                customItems.getOrDefault(this.getMinecraftVersion(), SeleneUtils.emptyMap());
        return customItemsForVersion.getOrDefault(identifier, () -> Selene.getItems().getAir()).get();
    }

    public abstract MinecraftVersion getMinecraftVersion();

    public Item getAir() {
        return Item.of(this.getAirId());
    }

    public String getAirId() {
        return "minecraft:air";
    }

    public MinecraftItems registerCustom(String identifier, Item item) {
        return this.registerCustom(identifier, () -> item);
    }

    public MinecraftItems registerCustom(String identifier, Supplier<Item> item) {
        customItems.putIfAbsent(this.getMinecraftVersion(), SeleneUtils.emptyConcurrentMap());
        if (customItems.get(this.getMinecraftVersion()).containsKey(identifier))
            Selene.log().warn("Overwriting custom item identifier '" + identifier + "'");
        customItems.get(this.getMinecraftVersion()).put(identifier, item);
        return this;
    }

    public abstract Item getAcaciaLeaves();

    public abstract Item getAcaciaLog();

    public abstract Item getAcaciaPlanks();

    public abstract Item getAcaciaSapling();

    public abstract Item getAcaciaWoodSlab();

    public abstract Item getAllium();

    public abstract Item getAndesite();

    public abstract Item getAzureBluet();

    public abstract Item getBirchLeaves();

    public abstract Item getBirchLog();

    public abstract Item getBirchPlanks();

    public abstract Item getBirchSapling();

    public abstract Item getBirchWoodSlab();

    public abstract Item getBlackBanner();

    public abstract Item getBlackBed();

    public abstract Item getBlackCarpet();

    public abstract Item getBlackConcrete();

    public abstract Item getBlackConcretePowder();

    public abstract Item getBlackDye();

    public abstract Item getBlackStainedGlass();

    public abstract Item getBlackStainedGlassPane();

    public abstract Item getBlackTerracotta();

    public abstract Item getBlackWool();

    public abstract Item getBlueBanner();

    public abstract Item getBlueBed();

    public abstract Item getBlueCarpet();

    public abstract Item getBlueOrchid();

    public abstract Item getBlueStainedGlass();

    public abstract Item getBlueStainedGlassPane();

    public abstract Item getBlueTerracotta();

    public abstract Item getBlueWool();

    public abstract Item getBrickSlab();

    public abstract Item getBrownBanner();

    public abstract Item getBrownBed();

    public abstract Item getBrownCarpet();

    public abstract Item getBrownConcrete();

    public abstract Item getBrownConcretePowder();

    public abstract Item getBrownDye();

    public abstract Item getBrownStainedGlass();

    public abstract Item getBrownStainedGlassPane();

    public abstract Item getBrownTerracotta();

    public abstract Item getBrownWool();

    public abstract Item getCharcoal();

    public abstract Item getSlightlyDamagedAnvil();

    public abstract Item getChiseledRedSandstone();

    public abstract Item getChiseledSandstone();

    public abstract Item getChiseledStoneBricks();

    public abstract Item getCoarseDirt();

    public abstract Item getCobblestoneSlab();

    public abstract Item getCookedSalmon();

    public abstract Item getCrackedStoneBricks();

    public abstract Item getCreeperHead();

    public abstract Item getCyanBanner();

    public abstract Item getCyanBed();

    public abstract Item getCyanCarpet();

    public abstract Item getCyanConcrete();

    public abstract Item getCyanConcretePowder();

    public abstract Item getCyanDye();

    public abstract Item getCyanStainedGlass();

    public abstract Item getCyanStainedGlassPane();

    public abstract Item getCyanTerracotta();

    public abstract Item getCyanWool();

    public abstract Item getVeryDamagedAnvil();

    public abstract Item getDarkOakLeaves();

    public abstract Item getDarkOakLog();

    public abstract Item getDarkOakPlanks();

    public abstract Item getDarkOakSapling();

    public abstract Item getDarkOakWoodSlab();

    public abstract Item getDarkPrismarine();

    public abstract Item getDiorite();

    public abstract Item getDragonHead();

    public abstract Item getEnchantedGoldenApple();

    public abstract Item getFern();

    public abstract Item getGranite();

    public abstract Item getGrass();

    public abstract Item getGrayBanner();

    public abstract Item getGrayBed();

    public abstract Item getGrayCarpet();

    public abstract Item getGrayConcrete();

    public abstract Item getGrayConcretePowder();

    public abstract Item getGrayDye();

    public abstract Item getGrayStainedGlass();

    public abstract Item getGrayStainedGlassPane();

    public abstract Item getGrayTerracotta();

    public abstract Item getGrayWool();

    public abstract Item getGreenBanner();

    public abstract Item getGreenBed();

    public abstract Item getGreenCarpet();

    public abstract Item getGreenConcrete();

    public abstract Item getGreenConcretePowder();

    public abstract Item getGreenDye();

    public abstract Item getGreenStainedGlass();

    public abstract Item getGreenStainedGlassPane();

    public abstract Item getGreenTerracotta();

    public abstract Item getGreenWool();

    public abstract Item getChiseledStoneBrickMonsterEgg();

    public abstract Item getCobblestoneMonsterEgg();

    public abstract Item getCrackedStoneBrickMonsterEgg();

    public abstract Item getMossyStoneBrickMonsterEgg();

    public abstract Item getStoneBrickMonsterEgg();

    public abstract Item getJungleLeaves();

    public abstract Item getJungleLog();

    public abstract Item getJunglePlanks();

    public abstract Item getJungleSapling();

    public abstract Item getJungleWoodSlab();

    public abstract Item getLargeFern();

    public abstract Item getLightBlueBanner();

    public abstract Item getLightBlueBed();

    public abstract Item getLightBlueCarpet();

    public abstract Item getLightBlueConcrete();

    public abstract Item getLightBlueConcretePowder();

    public abstract Item getLightBlueDye();

    public abstract Item getLightBlueStainedGlass();

    public abstract Item getLightBlueStainedGlassPane();

    public abstract Item getLightBlueTerracotta();

    public abstract Item getLightBlueWool();

    public abstract Item getLightGrayBanner();

    public abstract Item getLightGrayBed();

    public abstract Item getLightGrayCarpet();

    public abstract Item getLightGrayConcrete();

    public abstract Item getLightGrayConcretePowder();

    public abstract Item getLightGrayDye();

    public abstract Item getLightGrayStainedGlass();

    public abstract Item getLightGrayStainedGlassPane();

    public abstract Item getLightGrayTerracotta();

    public abstract Item getLightGrayWool();

    public abstract Item getLilac();

    public abstract Item getLimeBanner();

    public abstract Item getLimeBed();

    public abstract Item getLimeCarpet();

    public abstract Item getLimeConcrete();

    public abstract Item getLimeConcretePowder();

    public abstract Item getLimeDye();

    public abstract Item getLimeStainedGlass();

    public abstract Item getLimeStainedGlassPane();

    public abstract Item getLimeTerracotta();

    public abstract Item getLimeWool();

    public abstract Item getMagentaBanner();

    public abstract Item getMagentaBed();

    public abstract Item getMagentaCarpet();

    public abstract Item getMagentaConcrete();

    public abstract Item getMagentaConcretePowder();

    public abstract Item getMagentaDye();

    public abstract Item getMagentaStainedGlass();

    public abstract Item getMagentaStainedGlassPane();

    public abstract Item getMagentaTerracotta();

    public abstract Item getMagentaWool();

    public abstract Item getMossyCobblestoneWall();

    public abstract Item getMossyStoneBricks();

    public abstract Item getNetherBrickSlab();

    public abstract Item getOakWoodSlab();

    public abstract Item getOrangeBanner();

    public abstract Item getOrangeBed();

    public abstract Item getOrangeCarpet();

    public abstract Item getOrangeConcrete();

    public abstract Item getOrangeConcretePowder();

    public abstract Item getOrangeDye();

    public abstract Item getOrangeStainedGlass();

    public abstract Item getOrangeStainedGlassPane();

    public abstract Item getOrangeTerracotta();

    public abstract Item getOrangeTulip();

    public abstract Item getOrangeWool();

    public abstract Item getOxeyeDaisy();

    public abstract Item getPeony();

    public abstract Item getPinkBanner();

    public abstract Item getPinkBed();

    public abstract Item getPinkCarpet();

    public abstract Item getPinkConcrete();

    public abstract Item getPinkConcretePowder();

    public abstract Item getPinkDye();

    public abstract Item getPinkStainedGlass();

    public abstract Item getPinkStainedGlassPane();

    public abstract Item getPinkTerracotta();

    public abstract Item getPinkTulip();

    public abstract Item getPinkWool();

    public abstract Item getSteveHead();

    public abstract Item getPodzol();

    public abstract Item getPolishedAndesite();

    public abstract Item getPolishedDiorite();

    public abstract Item getPolishedGranite();

    public abstract Item getPrismarineBricks();

    public abstract Item getPufferfish();

    public abstract Item getPurpleBanner();

    public abstract Item getPurpleBed();

    public abstract Item getPurpleCarpet();

    public abstract Item getPurpleConcrete();

    public abstract Item getPurpleConcretePowder();

    public abstract Item getPurpleDye();

    public abstract Item getPurpleStainedGlass();

    public abstract Item getPurpleStainedGlassPane();

    public abstract Item getPurpleTerracotta();

    public abstract Item getPurpleWool();

    public abstract Item getPillarQuartzBlock();

    public abstract Item getQuartzSlab();

    public abstract Item getRedBanner();

    public abstract Item getRedBed();

    public abstract Item getRedCarpet();

    public abstract Item getRedConcrete();

    public abstract Item getRedConcretePowder();

    public abstract Item getRedDye();

    public abstract Item getRedSand();

    public abstract Item getRedSandstoneSlab();

    public abstract Item getRedStainedGlass();

    public abstract Item getRedStainedGlassPane();

    public abstract Item getRedTerracotta();

    public abstract Item getRedTulip();

    public abstract Item getRedWool();

    public abstract Item getRoseBush();

    public abstract Item getRawSalmon();

    public abstract Item getSandstoneSlab();

    public abstract Item getSkeletonSkull();

    public abstract Item getSmoothRedSandstone();

    public abstract Item getSmoothSandstone();

    public abstract Item getSpruceLeaves();

    public abstract Item getSpruceLog();

    public abstract Item getSprucePlanks();

    public abstract Item getSpruceSapling();

    public abstract Item getSpruceWoodSlab();

    public abstract Item getStoneBrickSlab();

    public abstract Item getDoubleTallgrass();

    public abstract Item getTropicalFish();

    public abstract Item getWetSponge();

    public abstract Item getWhiteBanner();

    public abstract Item getWhiteBed();

    public abstract Item getWhiteCarpet();

    public abstract Item getWhiteConcrete();

    public abstract Item getWhiteConcretePowder();

    public abstract Item getWhiteDye();

    public abstract Item getWhiteStainedGlass();

    public abstract Item getWhiteStainedGlassPane();

    public abstract Item getWhiteTulip();

    public abstract Item getWitherSkeletonSkull();

    public abstract Item getYellowBanner();

    public Item getAcaciaBoat() {
        return Item.of("minecraft:acacia_boat");
    }

    public Item getAcaciaButton() {
        return Item.of("minecraft:acacia_button");
    }

    public Item getAcaciaDoor() {
        return Item.of("minecraft:acacia_door");
    }

    public Item getAcaciaFence() {
        return Item.of("minecraft:acacia_fence");
    }

    public Item getAcaciaFenceGate() {
        return Item.of("minecraft:acacia_fence_gate");
    }

    public Item getAcaciaPressurePlate() {
        return Item.of("minecraft:acacia_pressure_plate");
    }

    public Item getAcaciaSign() {
        return Item.of("minecraft:acacia_sign");
    }

    public Item getAcaciaWoodStairs() {
        return Item.of("minecraft:acacia_stairs");
    }

    public Item getAcaciaTrapdoor() {
        return Item.of("minecraft:acacia_trapdoor");
    }

    public Item getAcaciaWoodWithBark() {
        return Item.of("minecraft:acacia_wood");
    }

    public Item getActivatorRails() {
        return Item.of("minecraft:activator_rail");
    }

    public Item getAncientDebris() {
        return Item.of("minecraft:ancient_debris");
    }

    public Item getAndesiteSlab() {
        return Item.of("minecraft:andesite_slab");
    }

    public Item getAndesiteStairs() {
        return Item.of("minecraft:andesite_stairs");
    }

    public Item getAndesiteWall() {
        return Item.of("minecraft:andesite_wall");
    }

    public Item getAnvil() {
        return Item.of("minecraft:anvil");
    }

    public Item getApple() {
        return Item.of("minecraft:apple");
    }

    public Item getArmorStand() {
        return Item.of("minecraft:armor_stand");
    }

    public Item getArrow() {
        return Item.of("minecraft:arrow");
    }

    public Item getBakedPotato() {
        return Item.of("minecraft:baked_potato");
    }

    public Item getBamboo() {
        return Item.of("minecraft:bamboo");
    }

    public Item getBarrel() {
        return Item.of("minecraft:barrel");
    }

    public Item getBarrier() {
        return Item.of("minecraft:barrier");
    }

    public Item getBasalt() {
        return Item.of("minecraft:basalt");
    }

    public Item getBatSpawnEgg() {
        return Item.of("minecraft:bat_spawn_egg");
    }

    public Item getBeacon() {
        return Item.of("minecraft:beacon");
    }

    public Item getBedrock() {
        return Item.of("minecraft:bedrock");
    }

    public Item getBeeNest() {
        return Item.of("minecraft:bee_nest");
    }

    public Item getBeeSpawnEgg() {
        return Item.of("minecraft:bee_spawn_egg");
    }

    public Item getRawBeef() {
        return Item.of("minecraft:beef");
    }

    public Item getBeehive() {
        return Item.of("minecraft:beehive");
    }

    public Item getBeetroot() {
        return Item.of("minecraft:beetroot");
    }

    public Item getBeetrootSeeds() {
        return Item.of("minecraft:beetroot_seeds");
    }

    public Item getBeetrootSoup() {
        return Item.of("minecraft:beetroot_soup");
    }

    public Item getBell() {
        return Item.of("minecraft:bell");
    }

    public Item getBirchBoat() {
        return Item.of("minecraft:birch_boat");
    }

    public Item getBirchButton() {
        return Item.of("minecraft:birch_button");
    }

    public Item getBirchDoor() {
        return Item.of("minecraft:birch_door");
    }

    public Item getBirchFence() {
        return Item.of("minecraft:birch_fence");
    }

    public Item getBirchFenceGate() {
        return Item.of("minecraft:birch_fence_gate");
    }

    public Item getBirchPressurePlate() {
        return Item.of("minecraft:birch_pressure_plate");
    }

    public Item getBirchSign() {
        return Item.of("minecraft:birch_sign");
    }

    public Item getBirchWoodStairs() {
        return Item.of("minecraft:birch_stairs");
    }

    public Item getBirchTrapdoor() {
        return Item.of("minecraft:birch_trapdoor");
    }

    public Item getBirchWoodWithBark() {
        return Item.of("minecraft:birch_wood");
    }

    public Item getBlackGlazedTerracotta() {
        return Item.of("minecraft:black_glazed_terracotta");
    }

    public Item getBlackShulkerBox() {
        return Item.of("minecraft:black_shulker_box");
    }

    public Item getBlackstone() {
        return Item.of("minecraft:blackstone");
    }

    public Item getBlackstoneSlab() {
        return Item.of("minecraft:blackstone_slab");
    }

    public Item getBlackstoneStairs() {
        return Item.of("minecraft:blackstone_stairs");
    }

    public Item getBlackstoneWall() {
        return Item.of("minecraft:blackstone_wall");
    }

    public Item getBlastFurnace() {
        return Item.of("minecraft:blast_furnace");
    }

    public Item getBlazePowder() {
        return Item.of("minecraft:blaze_powder");
    }

    public Item getBlazeRod() {
        return Item.of("minecraft:blaze_rod");
    }

    public Item getBlazeSpawnEgg() {
        return Item.of("minecraft:blaze_spawn_egg");
    }

    public Item getBlueConcrete() {
        return Item.of("minecraft:blue_concrete");
    }

    public Item getBlueConcretePowder() {
        return Item.of("minecraft:blue_concrete_powder");
    }

    public Item getBlueDye() {
        return Item.of("minecraft:blue_dye");
    }

    public Item getBlueGlazedTerracotta() {
        return Item.of("minecraft:blue_glazed_terracotta");
    }

    public Item getBlueIce() {
        return Item.of("minecraft:blue_ice");
    }

    public Item getBlueShulkerBox() {
        return Item.of("minecraft:blue_shulker_box");
    }

    public Item getBone() {
        return Item.of("minecraft:bone");
    }

    public Item getBoneBlock() {
        return Item.of("minecraft:bone_block");
    }

    public Item getBoneMeal() {
        return Item.of("minecraft:bone_meal");
    }

    public Item getBook() {
        return Item.of("minecraft:book");
    }

    public Item getBookshelf() {
        return Item.of("minecraft:bookshelf");
    }

    public Item getBow() {
        return Item.of("minecraft:bow");
    }

    public Item getBowl() {
        return Item.of("minecraft:bowl");
    }

    public Item getBrainCoral() {
        return Item.of("minecraft:brain_coral");
    }

    public Item getBrainCoralBlock() {
        return Item.of("minecraft:brain_coral_block");
    }

    public Item getBrainCoralFan() {
        return Item.of("minecraft:brain_coral_fan");
    }

    public Item getBread() {
        return Item.of("minecraft:bread");
    }

    public Item getBrewingStand() {
        return Item.of("minecraft:brewing_stand");
    }

    public Item getBrick() {
        return Item.of("minecraft:brick");
    }

    public Item getBrickStairs() {
        return Item.of("minecraft:brick_stairs");
    }

    public Item getBrickWall() {
        return Item.of("minecraft:brick_wall");
    }

    public Item getBricks() {
        return Item.of("minecraft:bricks");
    }

    public Item getBrownGlazedTerracotta() {
        return Item.of("minecraft:brown_glazed_terracotta");
    }

    public Item getBrownMushroom() {
        return Item.of("minecraft:brown_mushroom");
    }

    public Item getBrownMushroomBlock() {
        return Item.of("minecraft:brown_mushroom_block");
    }

    public Item getBrownShulkerBox() {
        return Item.of("minecraft:brown_shulker_box");
    }

    public Item getBubbleCoral() {
        return Item.of("minecraft:bubble_coral");
    }

    public Item getBubbleCoralBlock() {
        return Item.of("minecraft:bubble_coral_block");
    }

    public Item getBubbleCoralFan() {
        return Item.of("minecraft:bubble_coral_fan");
    }

    public Item getBucket() {
        return Item.of("minecraft:bucket");
    }

    public Item getCactus() {
        return Item.of("minecraft:cactus");
    }

    public Item getCake() {
        return Item.of("minecraft:cake");
    }

    public Item getCampfire() {
        return Item.of("minecraft:campfire");
    }

    public Item getCarrot() {
        return Item.of("minecraft:carrot");
    }

    public Item getCarrotOnAStick() {
        return Item.of("minecraft:carrot_on_a_stick");
    }

    public Item getCartographyTable() {
        return Item.of("minecraft:cartography_table");
    }

    public Item getCarvedPumpkin() {
        return Item.of("minecraft:carved_pumpkin");
    }

    public Item getCatSpawnEgg() {
        return Item.of("minecraft:cat_spawn_egg");
    }

    public Item getCauldron() {
        return Item.of("minecraft:cauldron");
    }

    public Item getCaveSpiderSpawnEgg() {
        return Item.of("minecraft:cave_spider_spawn_egg");
    }

    public Item getChain() {
        return Item.of("minecraft:chain");
    }

    public Item getChainCommandBlock() {
        return Item.of("minecraft:chain_command_block");
    }

    public Item getChainBoots() {
        return Item.of("minecraft:chainmail_boots");
    }

    public Item getChainChestplate() {
        return Item.of("minecraft:chainmail_chestplate");
    }

    public Item getChainHelmet() {
        return Item.of("minecraft:chainmail_helmet");
    }

    public Item getChainLeggings() {
        return Item.of("minecraft:chainmail_leggings");
    }

    public Item getChest() {
        return Item.of("minecraft:chest");
    }

    public Item getMinecartWithChest() {
        return Item.of("minecraft:chest_minecart");
    }

    public Item getRawChicken() {
        return Item.of("minecraft:chicken");
    }

    public Item getChickenSpawnEgg() {
        return Item.of("minecraft:chicken_spawn_egg");
    }

    public Item getChiseledNetherBricks() {
        return Item.of("minecraft:chiseled_nether_bricks");
    }

    public Item getChiseledPolishedBlackstone() {
        return Item.of("minecraft:chiseled_polished_blackstone");
    }

    public Item getChiseledQuartzBlock() {
        return Item.of("minecraft:chiseled_quartz_block");
    }

    public Item getChorusFlower() {
        return Item.of("minecraft:chorus_flower");
    }

    public Item getChorusFruit() {
        return Item.of("minecraft:chorus_fruit");
    }

    public Item getChorusPlant() {
        return Item.of("minecraft:chorus_plant");
    }

    public Item getClayBlock() {
        return Item.of("minecraft:clay");
    }

    public Item getClay() {
        return Item.of("minecraft:clay_ball");
    }

    public Item getClock() {
        return Item.of("minecraft:clock");
    }

    public Item getCoal() {
        return Item.of("minecraft:coal");
    }

    public Item getCoalBlock() {
        return Item.of("minecraft:coal_block");
    }

    public Item getCoalOre() {
        return Item.of("minecraft:coal_ore");
    }

    public Item getCobblestone() {
        return Item.of("minecraft:cobblestone");
    }

    public Item getCobblestoneStairs() {
        return Item.of("minecraft:cobblestone_stairs");
    }

    public Item getCobblestoneWall() {
        return Item.of("minecraft:cobblestone_wall");
    }

    public Item getCobweb() {
        return Item.of("minecraft:cobweb");
    }

    public Item getCocoaBeans() {
        return Item.of("minecraft:cocoa_beans");
    }

    public Item getRawCod() {
        return Item.of("minecraft:cod");
    }

    public Item getBucketOfCod() {
        return Item.of("minecraft:cod_bucket");
    }

    public Item getCodSpawnEgg() {
        return Item.of("minecraft:cod_spawn_egg");
    }

    public Item getCommandBlock() {
        return Item.of("minecraft:command_block");
    }

    public Item getMinecartWithCommandBlock() {
        return Item.of("minecraft:command_block_minecart");
    }

    public Item getRedstoneComparator() {
        return Item.of("minecraft:comparator");
    }

    public Item getCompass() {
        return Item.of("minecraft:compass");
    }

    public Item getComposter() {
        return Item.of("minecraft:composter");
    }

    public Item getConduit() {
        return Item.of("minecraft:conduit");
    }

    public Item getSteak() {
        return Item.of("minecraft:cooked_beef");
    }

    public Item getCookedChicken() {
        return Item.of("minecraft:cooked_chicken");
    }

    public Item getCookedCod() {
        return Item.of("minecraft:cooked_cod");
    }

    public Item getCookedMutton() {
        return Item.of("minecraft:cooked_mutton");
    }

    public Item getCookedPorkchop() {
        return Item.of("minecraft:cooked_porkchop");
    }

    public Item getCookedRabbit() {
        return Item.of("minecraft:cooked_rabbit");
    }

    public Item getCookie() {
        return Item.of("minecraft:cookie");
    }

    public Item getCornflower() {
        return Item.of("minecraft:cornflower");
    }

    public Item getCowSpawnEgg() {
        return Item.of("minecraft:cow_spawn_egg");
    }

    public Item getCrackedNetherBricks() {
        return Item.of("minecraft:cracked_nether_bricks");
    }

    public Item getCrackedPolishedBlackstoneBricks() {
        return Item.of("minecraft:cracked_polished_blackstone_bricks");
    }

    public Item getCraftingTable() {
        return Item.of("minecraft:crafting_table");
    }

    public Item getCreeperChargeBannerPattern() {
        return Item.of("minecraft:creeper_banner_pattern");
    }

    public Item getCreeperSpawnEgg() {
        return Item.of("minecraft:creeper_spawn_egg");
    }

    public Item getCrimsonButton() {
        return Item.of("minecraft:crimson_button");
    }

    public Item getCrimsonDoor() {
        return Item.of("minecraft:crimson_door");
    }

    public Item getCrimsonFence() {
        return Item.of("minecraft:crimson_fence");
    }

    public Item getCrimsonFenceGate() {
        return Item.of("minecraft:crimson_fence_gate");
    }

    public Item getCrimsonFungus() {
        return Item.of("minecraft:crimson_fungus");
    }

    public Item getCrimsonHyphae() {
        return Item.of("minecraft:crimson_hyphae");
    }

    public Item getCrimsonNylium() {
        return Item.of("minecraft:crimson_nylium");
    }

    public Item getCrimsonPlanks() {
        return Item.of("minecraft:crimson_planks");
    }

    public Item getCrimsonPressurePlate() {
        return Item.of("minecraft:crimson_pressure_plate");
    }

    public Item getCrimsonRoots() {
        return Item.of("minecraft:crimson_roots");
    }

    public Item getCrimsonSign() {
        return Item.of("minecraft:crimson_sign");
    }

    public Item getCrimsonSlab() {
        return Item.of("minecraft:crimson_slab");
    }

    public Item getCrimsonStairs() {
        return Item.of("minecraft:crimson_stairs");
    }

    public Item getCrimsonStem() {
        return Item.of("minecraft:crimson_stem");
    }

    public Item getCrimsonTrapdoor() {
        return Item.of("minecraft:crimson_trapdoor");
    }

    public Item getCrossbow() {
        return Item.of("minecraft:crossbow");
    }

    public Item getCryingObsidian() {
        return Item.of("minecraft:crying_obsidian");
    }

    public Item getCutRedSandstone() {
        return Item.of("minecraft:cut_red_sandstone");
    }

    public Item getCutRedSandstoneSlab() {
        return Item.of("minecraft:cut_red_sandstone_slab");
    }

    public Item getCutSandstone() {
        return Item.of("minecraft:cut_sandstone");
    }

    public Item getCutSandstoneSlab() {
        return Item.of("minecraft:cut_sandstone_slab");
    }

    public Item getCyanGlazedTerracotta() {
        return Item.of("minecraft:cyan_glazed_terracotta");
    }

    public Item getCyanShulkerBox() {
        return Item.of("minecraft:cyan_shulker_box");
    }

    public Item getDandelion() {
        return Item.of("minecraft:dandelion");
    }

    public Item getDarkOakBoat() {
        return Item.of("minecraft:dark_oak_boat");
    }

    public Item getDarkOakButton() {
        return Item.of("minecraft:dark_oak_button");
    }

    public Item getDarkOakDoor() {
        return Item.of("minecraft:dark_oak_door");
    }

    public Item getDarkOakFence() {
        return Item.of("minecraft:dark_oak_fence");
    }

    public Item getDarkOakFenceGate() {
        return Item.of("minecraft:dark_oak_fence_gate");
    }

    public Item getDarkOakPressurePlate() {
        return Item.of("minecraft:dark_oak_pressure_plate");
    }

    public Item getDarkOakSign() {
        return Item.of("minecraft:dark_oak_sign");
    }

    public Item getDarkOakWoodStairs() {
        return Item.of("minecraft:dark_oak_stairs");
    }

    public Item getDarkOakTrapdoor() {
        return Item.of("minecraft:dark_oak_trapdoor");
    }

    public Item getDarkOakWoodWithBark() {
        return Item.of("minecraft:dark_oak_wood");
    }

    public Item getDarkPrismarineSlab() {
        return Item.of("minecraft:dark_prismarine_slab");
    }

    public Item getDarkPrismarineStairs() {
        return Item.of("minecraft:dark_prismarine_stairs");
    }

    public Item getDaylightSensor() {
        return Item.of("minecraft:daylight_detector");
    }

    public Item getDeadBrainCoral() {
        return Item.of("minecraft:dead_brain_coral");
    }

    public Item getDeadBrainCoralBlock() {
        return Item.of("minecraft:dead_brain_coral_block");
    }

    public Item getDeadBrainCoralFan() {
        return Item.of("minecraft:dead_brain_coral_fan");
    }

    public Item getDeadBubbleCoral() {
        return Item.of("minecraft:dead_bubble_coral");
    }

    public Item getDeadBubbleCoralBlock() {
        return Item.of("minecraft:dead_bubble_coral_block");
    }

    public Item getDeadBubbleCoralFan() {
        return Item.of("minecraft:dead_bubble_coral_fan");
    }

    public Item getDeadBush() {
        return Item.of("minecraft:dead_bush");
    }

    public Item getDeadFireCoral() {
        return Item.of("minecraft:dead_fire_coral");
    }

    public Item getDeadFireCoralBlock() {
        return Item.of("minecraft:dead_fire_coral_block");
    }

    public Item getDeadFireCoralFan() {
        return Item.of("minecraft:dead_fire_coral_fan");
    }

    public Item getDeadHornCoral() {
        return Item.of("minecraft:dead_horn_coral");
    }

    public Item getDeadHornCoralBlock() {
        return Item.of("minecraft:dead_horn_coral_block");
    }

    public Item getDeadHornCoralFan() {
        return Item.of("minecraft:dead_horn_coral_fan");
    }

    public Item getDeadTubeCoral() {
        return Item.of("minecraft:dead_tube_coral");
    }

    public Item getDeadTubeCoralBlock() {
        return Item.of("minecraft:dead_tube_coral_block");
    }

    public Item getDeadTubeCoralFan() {
        return Item.of("minecraft:dead_tube_coral_fan");
    }

    public Item getDetectorRails() {
        return Item.of("minecraft:detector_rail");
    }

    public Item getDiamond() {
        return Item.of("minecraft:diamond");
    }

    public Item getDiamondAxe() {
        return Item.of("minecraft:diamond_axe");
    }

    public Item getBlockOfDiamond() {
        return Item.of("minecraft:diamond_block");
    }

    public Item getDiamondBoots() {
        return Item.of("minecraft:diamond_boots");
    }

    public Item getDiamondChestplate() {
        return Item.of("minecraft:diamond_chestplate");
    }

    public Item getDiamondHelmet() {
        return Item.of("minecraft:diamond_helmet");
    }

    public Item getDiamondHoe() {
        return Item.of("minecraft:diamond_hoe");
    }

    public Item getDiamondHorseArmor() {
        return Item.of("minecraft:diamond_horse_armor");
    }

    public Item getDiamondLeggings() {
        return Item.of("minecraft:diamond_leggings");
    }

    public Item getDiamondOre() {
        return Item.of("minecraft:diamond_ore");
    }

    public Item getDiamondPickaxe() {
        return Item.of("minecraft:diamond_pickaxe");
    }

    public Item getDiamondShovel() {
        return Item.of("minecraft:diamond_shovel");
    }

    public Item getDiamondSword() {
        return Item.of("minecraft:diamond_sword");
    }

    public Item getDioriteSlab() {
        return Item.of("minecraft:diorite_slab");
    }

    public Item getDioriteStairs() {
        return Item.of("minecraft:diorite_stairs");
    }

    public Item getDioriteWall() {
        return Item.of("minecraft:diorite_wall");
    }

    public Item getDirt() {
        return Item.of("minecraft:dirt");
    }

    public Item getDispenser() {
        return Item.of("minecraft:dispenser");
    }

    public Item getDolphinSpawnEgg() {
        return Item.of("minecraft:dolphin_spawn_egg");
    }

    public Item getDonkeySpawnEgg() {
        return Item.of("minecraft:donkey_spawn_egg");
    }

    public Item getDragonsBreath() {
        return Item.of("minecraft:dragon_breath");
    }

    public Item getDragonEgg() {
        return Item.of("minecraft:dragon_egg");
    }

    public Item getDriedKelp() {
        return Item.of("minecraft:dried_kelp");
    }

    public Item getDriedKelpBlock() {
        return Item.of("minecraft:dried_kelp_block");
    }

    public Item getDropper() {
        return Item.of("minecraft:dropper");
    }

    public Item getDrownedSpawnEgg() {
        return Item.of("minecraft:drowned_spawn_egg");
    }

    public Item getEgg() {
        return Item.of("minecraft:egg");
    }

    public Item getElderGuardianSpawnEgg() {
        return Item.of("minecraft:elder_guardian_spawn_egg");
    }

    public Item getElytra() {
        return Item.of("minecraft:elytra");
    }

    public Item getEmerald() {
        return Item.of("minecraft:emerald");
    }

    public Item getBlockOfEmerald() {
        return Item.of("minecraft:emerald_block");
    }

    public Item getEmeraldOre() {
        return Item.of("minecraft:emerald_ore");
    }

    public Item getEnchantedBook() {
        return Item.of("minecraft:enchanted_book");
    }

    public Item getEnchantingTable() {
        return Item.of("minecraft:enchanting_table");
    }

    public Item getEndCrystal() {
        return Item.of("minecraft:end_crystal");
    }

    public Item getEndPortalFrame() {
        return Item.of("minecraft:end_portal_frame");
    }

    public Item getEndRod() {
        return Item.of("minecraft:end_rod");
    }

    public Item getEndStone() {
        return Item.of("minecraft:end_stone");
    }

    public Item getEndStoneBrickSlab() {
        return Item.of("minecraft:end_stone_brick_slab");
    }

    public Item getEndStoneBrickStairs() {
        return Item.of("minecraft:end_stone_brick_stairs");
    }

    public Item getEndStoneBrickWall() {
        return Item.of("minecraft:end_stone_brick_wall");
    }

    public Item getEndStoneBricks() {
        return Item.of("minecraft:end_stone_bricks");
    }

    public Item getEnderChest() {
        return Item.of("minecraft:ender_chest");
    }

    public Item getEyeOfEnder() {
        return Item.of("minecraft:ender_eye");
    }

    public Item getEnderPearl() {
        return Item.of("minecraft:ender_pearl");
    }

    public Item getEndermanSpawnEgg() {
        return Item.of("minecraft:enderman_spawn_egg");
    }

    public Item getEndermiteSpawnEgg() {
        return Item.of("minecraft:endermite_spawn_egg");
    }

    public Item getEvokerSpawnEgg() {
        return Item.of("minecraft:evoker_spawn_egg");
    }

    public Item getBottleOEnchanting() {
        return Item.of("minecraft:experience_bottle");
    }

    public Item getFarmland() {
        return Item.of("minecraft:farmland");
    }

    public Item getFeather() {
        return Item.of("minecraft:feather");
    }

    public Item getFermentedSpiderEye() {
        return Item.of("minecraft:fermented_spider_eye");
    }

    public Item getFilledMap() {
        return Item.of("minecraft:filled_map");
    }

    public Item getFireCharge() {
        return Item.of("minecraft:fire_charge");
    }

    public Item getFireCoral() {
        return Item.of("minecraft:fire_coral");
    }

    public Item getFireCoralBlock() {
        return Item.of("minecraft:fire_coral_block");
    }

    public Item getFireCoralFan() {
        return Item.of("minecraft:fire_coral_fan");
    }

    public Item getFireworkRocket() {
        return Item.of("minecraft:firework_rocket");
    }

    public Item getFireworkStar() {
        return Item.of("minecraft:firework_star");
    }

    public Item getFishingRod() {
        return Item.of("minecraft:fishing_rod");
    }

    public Item getFletchingTable() {
        return Item.of("minecraft:fletching_table");
    }

    public Item getFlint() {
        return Item.of("minecraft:flint");
    }

    public Item getFlintAndSteel() {
        return Item.of("minecraft:flint_and_steel");
    }

    public Item getFlowerChargeBannerPattern() {
        return Item.of("minecraft:flower_banner_pattern");
    }

    public Item getFlowerPot() {
        return Item.of("minecraft:flower_pot");
    }

    public Item getFoxSpawnEgg() {
        return Item.of("minecraft:fox_spawn_egg");
    }

    public Item getFurnace() {
        return Item.of("minecraft:furnace");
    }

    public Item getMinecartWithFurnace() {
        return Item.of("minecraft:furnace_minecart");
    }

    public Item getGhastSpawnEgg() {
        return Item.of("minecraft:ghast_spawn_egg");
    }

    public Item getGhastTear() {
        return Item.of("minecraft:ghast_tear");
    }

    public Item getGildedBlackstone() {
        return Item.of("minecraft:gilded_blackstone");
    }

    public Item getGlass() {
        return Item.of("minecraft:glass");
    }

    public Item getGlassBottle() {
        return Item.of("minecraft:glass_bottle");
    }

    public Item getGlassPane() {
        return Item.of("minecraft:glass_pane");
    }

    public Item getGlisteringMelon() {
        return Item.of("minecraft:glistering_melon_slice");
    }

    public Item getGlobeBannerPattern() {
        return Item.of("minecraft:globe_banner_pattern");
    }

    public Item getGlowstone() {
        return Item.of("minecraft:glowstone");
    }

    public Item getGlowstoneDust() {
        return Item.of("minecraft:glowstone_dust");
    }

    public Item getBlockOfGold() {
        return Item.of("minecraft:gold_block");
    }

    public Item getGoldIngot() {
        return Item.of("minecraft:gold_ingot");
    }

    public Item getGoldNugget() {
        return Item.of("minecraft:gold_nugget");
    }

    public Item getGoldOre() {
        return Item.of("minecraft:gold_ore");
    }

    public Item getGoldenApple() {
        return Item.of("minecraft:golden_apple");
    }

    public Item getGoldenAxe() {
        return Item.of("minecraft:golden_axe");
    }

    public Item getGoldenBoots() {
        return Item.of("minecraft:golden_boots");
    }

    public Item getGoldenCarrot() {
        return Item.of("minecraft:golden_carrot");
    }

    public Item getGoldenChestplate() {
        return Item.of("minecraft:golden_chestplate");
    }

    public Item getGoldenHelmet() {
        return Item.of("minecraft:golden_helmet");
    }

    public Item getGoldenHoe() {
        return Item.of("minecraft:golden_hoe");
    }

    public Item getGoldHorseArmor() {
        return Item.of("minecraft:golden_horse_armor");
    }

    public Item getGoldenLeggings() {
        return Item.of("minecraft:golden_leggings");
    }

    public Item getGoldenPickaxe() {
        return Item.of("minecraft:golden_pickaxe");
    }

    public Item getGoldenShovel() {
        return Item.of("minecraft:golden_shovel");
    }

    public Item getGoldenSword() {
        return Item.of("minecraft:golden_sword");
    }

    public Item getGraniteSlab() {
        return Item.of("minecraft:granite_slab");
    }

    public Item getGraniteStairs() {
        return Item.of("minecraft:granite_stairs");
    }

    public Item getGraniteWall() {
        return Item.of("minecraft:granite_wall");
    }

    public Item getGrassBlock() {
        return Item.of("minecraft:grass_block");
    }

    public Item getGrassPath() {
        return Item.of("minecraft:grass_path");
    }

    public Item getGravel() {
        return Item.of("minecraft:gravel");
    }

    public Item getGrayGlazedTerracotta() {
        return Item.of("minecraft:gray_glazed_terracotta");
    }

    public Item getGrayShulkerBox() {
        return Item.of("minecraft:gray_shulker_box");
    }

    public Item getGreenGlazedTerracotta() {
        return Item.of("minecraft:green_glazed_terracotta");
    }

    public Item getGreenShulkerBox() {
        return Item.of("minecraft:green_shulker_box");
    }

    public Item getGrindstone() {
        return Item.of("minecraft:grindstone");
    }

    public Item getGuardianSpawnEgg() {
        return Item.of("minecraft:guardian_spawn_egg");
    }

    public Item getGunpowder() {
        return Item.of("minecraft:gunpowder");
    }

    public Item getHayBale() {
        return Item.of("minecraft:hay_block");
    }

    public Item getHeartOfTheSea() {
        return Item.of("minecraft:heart_of_the_sea");
    }

    public Item getHeavyWeightedPressurePlate() {
        return Item.of("minecraft:heavy_weighted_pressure_plate");
    }

    public Item getHoglinSpawnEgg() {
        return Item.of("minecraft:hoglin_spawn_egg");
    }

    public Item getHoneyBlock() {
        return Item.of("minecraft:honey_block");
    }

    public Item getHoneyBottle() {
        return Item.of("minecraft:honey_bottle");
    }

    public Item getHoneycomb() {
        return Item.of("minecraft:honeycomb");
    }

    public Item getHoneycombBlock() {
        return Item.of("minecraft:honeycomb_block");
    }

    public Item getHopper() {
        return Item.of("minecraft:hopper");
    }

    public Item getMinecartWithHopper() {
        return Item.of("minecraft:hopper_minecart");
    }

    public Item getHornCoral() {
        return Item.of("minecraft:horn_coral");
    }

    public Item getHornCoralBlock() {
        return Item.of("minecraft:horn_coral_block");
    }

    public Item getHornCoralFan() {
        return Item.of("minecraft:horn_coral_fan");
    }

    public Item getHorseSpawnEgg() {
        return Item.of("minecraft:horse_spawn_egg");
    }

    public Item getHuskSpawnEgg() {
        return Item.of("minecraft:husk_spawn_egg");
    }

    public Item getIce() {
        return Item.of("minecraft:ice");
    }

    public Item getStoneMonsterEgg() {
        return Item.of("minecraft:infested_stone");
    }

    public Item getInkSac() {
        return Item.of("minecraft:ink_sac");
    }

    public Item getIronAxe() {
        return Item.of("minecraft:iron_axe");
    }

    public Item getIronBars() {
        return Item.of("minecraft:iron_bars");
    }

    public Item getBlockOfIron() {
        return Item.of("minecraft:iron_block");
    }

    public Item getIronBoots() {
        return Item.of("minecraft:iron_boots");
    }

    public Item getIronChestplate() {
        return Item.of("minecraft:iron_chestplate");
    }

    public Item getIronDoor() {
        return Item.of("minecraft:iron_door");
    }

    public Item getIronHelmet() {
        return Item.of("minecraft:iron_helmet");
    }

    public Item getIronHoe() {
        return Item.of("minecraft:iron_hoe");
    }

    public Item getIronHorseArmor() {
        return Item.of("minecraft:iron_horse_armor");
    }

    public Item getIronIngot() {
        return Item.of("minecraft:iron_ingot");
    }

    public Item getIronLeggings() {
        return Item.of("minecraft:iron_leggings");
    }

    public Item getIronNugget() {
        return Item.of("minecraft:iron_nugget");
    }

    public Item getIronOre() {
        return Item.of("minecraft:iron_ore");
    }

    public Item getIronPickaxe() {
        return Item.of("minecraft:iron_pickaxe");
    }

    public Item getIronShovel() {
        return Item.of("minecraft:iron_shovel");
    }

    public Item getIronSword() {
        return Item.of("minecraft:iron_sword");
    }

    public Item getIronTrapdoor() {
        return Item.of("minecraft:iron_trapdoor");
    }

    public Item getItemFrame() {
        return Item.of("minecraft:item_frame");
    }

    public Item getJackOLantern() {
        return Item.of("minecraft:jack_o_lantern");
    }

    public Item getJukebox() {
        return Item.of("minecraft:jukebox");
    }

    public Item getJungleBoat() {
        return Item.of("minecraft:jungle_boat");
    }

    public Item getJungleButton() {
        return Item.of("minecraft:jungle_button");
    }

    public Item getJungleDoor() {
        return Item.of("minecraft:jungle_door");
    }

    public Item getJungleFence() {
        return Item.of("minecraft:jungle_fence");
    }

    public Item getJungleFenceGate() {
        return Item.of("minecraft:jungle_fence_gate");
    }

    public Item getJunglePressurePlate() {
        return Item.of("minecraft:jungle_pressure_plate");
    }

    public Item getJungleSign() {
        return Item.of("minecraft:jungle_sign");
    }

    public Item getJungleWoodStairs() {
        return Item.of("minecraft:jungle_stairs");
    }

    public Item getJungleTrapdoor() {
        return Item.of("minecraft:jungle_trapdoor");
    }

    public Item getJungleWoodWithBark() {
        return Item.of("minecraft:jungle_wood");
    }

    public Item getKelp() {
        return Item.of("minecraft:kelp");
    }

    public Item getLadder() {
        return Item.of("minecraft:ladder");
    }

    public Item getLantern() {
        return Item.of("minecraft:lantern");
    }

    public Item getLapisLazuliBlock() {
        return Item.of("minecraft:lapis_block");
    }

    public Item getLapisLazuli() {
        return Item.of("minecraft:lapis_lazuli");
    }

    public Item getLapisLazuliOre() {
        return Item.of("minecraft:lapis_ore");
    }

    public Item getLava() {
        return Item.of("minecraft:lava");
    }

    public Item getLavaBucket() {
        return Item.of("minecraft:lava_bucket");
    }

    public Item getLead() {
        return Item.of("minecraft:lead");
    }

    public Item getLeather() {
        return Item.of("minecraft:leather");
    }

    public Item getLeatherBoots() {
        return Item.of("minecraft:leather_boots");
    }

    public Item getLeatherTunic() {
        return Item.of("minecraft:leather_chestplate");
    }

    public Item getLeatherCap() {
        return Item.of("minecraft:leather_helmet");
    }

    public Item getLeatherHorseArmor() {
        return Item.of("minecraft:leather_horse_armor");
    }

    public Item getLeatherPants() {
        return Item.of("minecraft:leather_leggings");
    }

    public Item getLecturn() {
        return Item.of("minecraft:lectern");
    }

    public Item getLever() {
        return Item.of("minecraft:lever");
    }

    public Item getLightBlueGlazedTerracotta() {
        return Item.of("minecraft:light_blue_glazed_terracotta");
    }

    public Item getLightBlueShulkerBox() {
        return Item.of("minecraft:light_blue_shulker_box");
    }

    public Item getLightGrayGlazedTerracotta() {
        return Item.of("minecraft:light_gray_glazed_terracotta");
    }

    public Item getLightGrayShulkerBox() {
        return Item.of("minecraft:light_gray_shulker_box");
    }

    public Item getLightWeightedPressurePlate() {
        return Item.of("minecraft:light_weighted_pressure_plate");
    }

    public Item getLilyOfTheValley() {
        return Item.of("minecraft:lily_of_the_valley");
    }

    public Item getLilyPad() {
        return Item.of("minecraft:lily_pad");
    }

    public Item getLimeGlazedTerracotta() {
        return Item.of("minecraft:lime_glazed_terracotta");
    }

    public Item getLimeShulkerBox() {
        return Item.of("minecraft:lime_shulker_box");
    }

    public Item getLingeringPotion() {
        return Item.of("minecraft:lingering_potion");
    }

    public Item getLlamaSpawnEgg() {
        return Item.of("minecraft:llama_spawn_egg");
    }

    public Item getLodestone() {
        return Item.of("minecraft:lodestone");
    }

    public Item getLoom() {
        return Item.of("minecraft:loom");
    }

    public Item getMagentaGlazedTerracotta() {
        return Item.of("minecraft:magenta_glazed_terracotta");
    }

    public Item getMagentaShulkerBox() {
        return Item.of("minecraft:magenta_shulker_box");
    }

    public Item getMagmaBlock() {
        return Item.of("minecraft:magma_block");
    }

    public Item getMagmaCream() {
        return Item.of("minecraft:magma_cream");
    }

    public Item getMagmaCubeSpawnEgg() {
        return Item.of("minecraft:magma_cube_spawn_egg");
    }

    public Item getMap() {
        return Item.of("minecraft:map");
    }

    public Item getBlockOfMelon() {
        return Item.of("minecraft:melon");
    }

    public Item getMelonSeeds() {
        return Item.of("minecraft:melon_seeds");
    }

    public Item getMelonSlice() {
        return Item.of("minecraft:melon_slice");
    }

    public Item getMilk() {
        return Item.of("minecraft:milk_bucket");
    }

    public Item getMinecart() {
        return Item.of("minecraft:minecart");
    }

    public Item getThingBannerPattern() {
        return Item.of("minecraft:mojang_banner_pattern");
    }

    public Item getMooshroomSpawnEgg() {
        return Item.of("minecraft:mooshroom_spawn_egg");
    }

    public Item getMossyCobblestone() {
        return Item.of("minecraft:mossy_cobblestone");
    }

    public Item getMossyCobblestoneSlab() {
        return Item.of("minecraft:mossy_cobblestone_slab");
    }

    public Item getMossyCobblestoneStairs() {
        return Item.of("minecraft:mossy_cobblestone_stairs");
    }

    public Item getMossyStoneBrickSlab() {
        return Item.of("minecraft:mossy_stone_brick_slab");
    }

    public Item getMossyStoneBrickStairs() {
        return Item.of("minecraft:mossy_stone_brick_stairs");
    }

    public Item getMossyStoneBrickWall() {
        return Item.of("minecraft:mossy_stone_brick_wall");
    }

    public Item getMuleSpawnEgg() {
        return Item.of("minecraft:mule_spawn_egg");
    }

    public Item getMushroomStem() {
        return Item.of("minecraft:mushroom_stem");
    }

    public Item getMushroomStew() {
        return Item.of("minecraft:mushroom_stew");
    }

    public Item getMusicDiscC418_11() {
        return Item.of("minecraft:music_disc_11");
    }

    public Item getMusicDiscC418_13() {
        return Item.of("minecraft:music_disc_13");
    }

    public Item getMusicDiscC418Blocks() {
        return Item.of("minecraft:music_disc_blocks");
    }

    public Item getMusicDiscC418Cat() {
        return Item.of("minecraft:music_disc_cat");
    }

    public Item getMusicDiscC418Chirp() {
        return Item.of("minecraft:music_disc_chirp");
    }

    public Item getMusicDiscC418Far() {
        return Item.of("minecraft:music_disc_far");
    }

    public Item getMusicDiscC418Mall() {
        return Item.of("minecraft:music_disc_mall");
    }

    public Item getMusicDiscC418Mellohi() {
        return Item.of("minecraft:music_disc_mellohi");
    }

    public Item getMusicDiscPigstep() {
        return Item.of("minecraft:music_disc_pigstep");
    }

    public Item getMusicDiscC418Stal() {
        return Item.of("minecraft:music_disc_stal");
    }

    public Item getMusicDiscC418Strad() {
        return Item.of("minecraft:music_disc_strad");
    }

    public Item getMusicDiscC418Wait() {
        return Item.of("minecraft:music_disc_wait");
    }

    public Item getMusicDiscC418Ward() {
        return Item.of("minecraft:music_disc_ward");
    }

    public Item getRawMutton() {
        return Item.of("minecraft:mutton");
    }

    public Item getMycelium() {
        return Item.of("minecraft:mycelium");
    }

    public Item getNameTag() {
        return Item.of("minecraft:name_tag");
    }

    public Item getNautilusShell() {
        return Item.of("minecraft:nautilus_shell");
    }

    public Item getNetherBrick() {
        return Item.of("minecraft:nether_brick");
    }

    public Item getNetherBrickFence() {
        return Item.of("minecraft:nether_brick_fence");
    }

    public Item getNetherBrickStairs() {
        return Item.of("minecraft:nether_brick_stairs");
    }

    public Item getNetherBrickWall() {
        return Item.of("minecraft:nether_brick_wall");
    }

    public Item getBlockOfNetherBricks() {
        return Item.of("minecraft:nether_bricks");
    }

    public Item getNetherGoldOre() {
        return Item.of("minecraft:nether_gold_ore");
    }

    public Item getNetherQuartzOre() {
        return Item.of("minecraft:nether_quartz_ore");
    }

    public Item getNetherSprouts() {
        return Item.of("minecraft:nether_sprouts");
    }

    public Item getNetherStar() {
        return Item.of("minecraft:nether_star");
    }

    public Item getNetherWart() {
        return Item.of("minecraft:nether_wart");
    }

    public Item getNetherWartBlock() {
        return Item.of("minecraft:nether_wart_block");
    }

    public Item getNetheriteAxe() {
        return Item.of("minecraft:netherite_axe");
    }

    public Item getBlockOfNetherite() {
        return Item.of("minecraft:netherite_block");
    }

    public Item getNetheriteBoots() {
        return Item.of("minecraft:netherite_boots");
    }

    public Item getNetheriteChestplate() {
        return Item.of("minecraft:netherite_chestplate");
    }

    public Item getNetheriteHelmet() {
        return Item.of("minecraft:netherite_helmet");
    }

    public Item getNetheriteHoe() {
        return Item.of("minecraft:netherite_hoe");
    }

    public Item getNetheriteIngot() {
        return Item.of("minecraft:netherite_ingot");
    }

    public Item getNetheriteLeggings() {
        return Item.of("minecraft:netherite_leggings");
    }

    public Item getNetheritePickaxe() {
        return Item.of("minecraft:netherite_pickaxe");
    }

    public Item getNetheriteScrap() {
        return Item.of("minecraft:netherite_scrap");
    }

    public Item getNetheriteShovel() {
        return Item.of("minecraft:netherite_shovel");
    }

    public Item getNetheriteSword() {
        return Item.of("minecraft:netherite_sword");
    }

    public Item getNetherrack() {
        return Item.of("minecraft:netherrack");
    }

    public Item getQuartzBricks() {
        return Item.of("minecraft:quartz_bricks");
    }

    public Item getNoteBlock() {
        return Item.of("minecraft:note_block");
    }

    public Item getOakBoat() {
        return Item.of("minecraft:oak_boat");
    }

    public Item getOakButton() {
        return Item.of("minecraft:oak_button");
    }

    public Item getOakDoor() {
        return Item.of("minecraft:oak_door");
    }

    public Item getOakFence() {
        return Item.of("minecraft:oak_fence");
    }

    public Item getOakFenceGate() {
        return Item.of("minecraft:oak_fence_gate");
    }

    public Item getOakLeaves() {
        return Item.of("minecraft:oak_leaves");
    }

    public Item getOakLog() {
        return Item.of("minecraft:oak_log");
    }

    public Item getOakPlanks() {
        return Item.of("minecraft:oak_planks");
    }

    public Item getOakPressurePlate() {
        return Item.of("minecraft:oak_pressure_plate");
    }

    public Item getOakSapling() {
        return Item.of("minecraft:oak_sapling");
    }

    public Item getOakSign() {
        return Item.of("minecraft:oak_sign");
    }

    public Item getOakWoodStairs() {
        return Item.of("minecraft:oak_stairs");
    }

    public Item getOakTrapdoor() {
        return Item.of("minecraft:oak_trapdoor");
    }

    public Item getOakWoodWithBark() {
        return Item.of("minecraft:oak_wood");
    }

    public Item getObserver() {
        return Item.of("minecraft:observer");
    }

    public Item getObsidian() {
        return Item.of("minecraft:obsidian");
    }

    public Item getOcelotSpawnEgg() {
        return Item.of("minecraft:ocelot_spawn_egg");
    }

    public Item getOrangeGlazedTerracotta() {
        return Item.of("minecraft:orange_glazed_terracotta");
    }

    public Item getOrangeShulkerBox() {
        return Item.of("minecraft:orange_shulker_box");
    }

    public Item getPackedIce() {
        return Item.of("minecraft:packed_ice");
    }

    public Item getPainting() {
        return Item.of("minecraft:painting");
    }

    public Item getPandaSpawnEgg() {
        return Item.of("minecraft:panda_spawn_egg");
    }

    public Item getPaper() {
        return Item.of("minecraft:paper");
    }

    public Item getParrotSpawnEgg() {
        return Item.of("minecraft:parrot_spawn_egg");
    }

    public Item getPetrifiedOakSlab() {
        return Item.of("minecraft:petrified_oak_slab");
    }

    public Item getPhantomMembrane() {
        return Item.of("minecraft:phantom_membrane");
    }

    public Item getPhantomSpawnEgg() {
        return Item.of("minecraft:phantom_spawn_egg");
    }

    public Item getPigSpawnEgg() {
        return Item.of("minecraft:pig_spawn_egg");
    }

    public Item getSnoutBannerPattern() {
        return Item.of("minecraft:piglin_banner_pattern");
    }

    public Item getSpawnPiglinBrute() {
        return Item.of("minecraft:piglin_brute_spawn_egg");
    }

    public Item getPiglinSpawnEgg() {
        return Item.of("minecraft:piglin_spawn_egg");
    }

    public Item getPillagerSpawnEgg() {
        return Item.of("minecraft:pillager_spawn_egg");
    }

    public Item getPinkGlazedTerracotta() {
        return Item.of("minecraft:pink_glazed_terracotta");
    }

    public Item getPinkShulkerBox() {
        return Item.of("minecraft:pink_shulker_box");
    }

    public Item getPiston() {
        return Item.of("minecraft:piston");
    }

    public Item getPoisonousPotato() {
        return Item.of("minecraft:poisonous_potato");
    }

    public Item getPolarBearSpawnEgg() {
        return Item.of("minecraft:polar_bear_spawn_egg");
    }

    public Item getPolishedAndesiteSlab() {
        return Item.of("minecraft:polished_andesite_slab");
    }

    public Item getPolishedAndesiteStairs() {
        return Item.of("minecraft:polished_andesite_stairs");
    }

    public Item getPolishedBasalt() {
        return Item.of("minecraft:polished_basalt");
    }

    public Item getPolishedBlackstone() {
        return Item.of("minecraft:polished_blackstone");
    }

    public Item getPolishedBlackstoneBrickSlab() {
        return Item.of("minecraft:polished_blackstone_brick_slab");
    }

    public Item getPolishedBlackstoneBrickStairs() {
        return Item.of("minecraft:polished_blackstone_brick_stairs");
    }

    public Item getPolishedBlackstoneBrickWall() {
        return Item.of("minecraft:polished_blackstone_brick_wall");
    }

    public Item getPolishedBlackstoneBricks() {
        return Item.of("minecraft:polished_blackstone_bricks");
    }

    public Item getPolishedBlackstoneButton() {
        return Item.of("minecraft:polished_blackstone_button");
    }

    public Item getPolishedBlackstonePressurePlate() {
        return Item.of("minecraft:polished_blackstone_pressure_plate");
    }

    public Item getPolishedBlackstoneSlab() {
        return Item.of("minecraft:polished_blackstone_slab");
    }

    public Item getPolishedBlackstoneStairs() {
        return Item.of("minecraft:polished_blackstone_stairs");
    }

    public Item getPolishedBlackstoneWall() {
        return Item.of("minecraft:polished_blackstone_wall");
    }

    public Item getPolishedDioriteSlab() {
        return Item.of("minecraft:polished_diorite_slab");
    }

    public Item getPolishedDioriteStairs() {
        return Item.of("minecraft:polished_diorite_stairs");
    }

    public Item getPolishedGraniteSlab() {
        return Item.of("minecraft:polished_granite_slab");
    }

    public Item getPolishedGraniteStairs() {
        return Item.of("minecraft:polished_granite_stairs");
    }

    public Item getPoppedChorusFruit() {
        return Item.of("minecraft:popped_chorus_fruit");
    }

    public Item getPoppy() {
        return Item.of("minecraft:poppy");
    }

    public Item getRawPorkchop() {
        return Item.of("minecraft:porkchop");
    }

    public Item getPotato() {
        return Item.of("minecraft:potato");
    }

    public Item getPotion() {
        return Item.of("minecraft:potion");
    }

    public Item getWaterBottle() {
        return Item.of("minecraft:potion");
    }

    public Item getPoweredRails() {
        return Item.of("minecraft:powered_rail");
    }

    public Item getPrismarine() {
        return Item.of("minecraft:prismarine");
    }

    public Item getPrismarineBrickSlab() {
        return Item.of("minecraft:prismarine_brick_slab");
    }

    public Item getPrismarineBricksStairs() {
        return Item.of("minecraft:prismarine_brick_stairs");
    }

    public Item getPrismarineCrystals() {
        return Item.of("minecraft:prismarine_crystals");
    }

    public Item getPrismarineShard() {
        return Item.of("minecraft:prismarine_shard");
    }

    public Item getPrismarineSlab() {
        return Item.of("minecraft:prismarine_slab");
    }

    public Item getPrismarineStairs() {
        return Item.of("minecraft:prismarine_stairs");
    }

    public Item getPrismarineWall() {
        return Item.of("minecraft:prismarine_wall");
    }

    public Item getBucketOfPufferfish() {
        return Item.of("minecraft:pufferfish_bucket");
    }

    public Item getPufferfishSpawnEgg() {
        return Item.of("minecraft:pufferfish_spawn_egg");
    }

    public Item getPumpkin() {
        return Item.of("minecraft:pumpkin");
    }

    public Item getPumpkinPie() {
        return Item.of("minecraft:pumpkin_pie");
    }

    public Item getPumpkinSeeds() {
        return Item.of("minecraft:pumpkin_seeds");
    }

    public Item getPurpleGlazedTerracotta() {
        return Item.of("minecraft:purple_glazed_terracotta");
    }

    public Item getPurpleShulkerBox() {
        return Item.of("minecraft:purple_shulker_box");
    }

    public Item getPurpurBlock() {
        return Item.of("minecraft:purpur_block");
    }

    public Item getPurpurPillar() {
        return Item.of("minecraft:purpur_pillar");
    }

    public Item getPurpurSlab() {
        return Item.of("minecraft:purpur_slab");
    }

    public Item getPurpurStairs() {
        return Item.of("minecraft:purpur_stairs");
    }

    public Item getNetherQuartz() {
        return Item.of("minecraft:quartz");
    }

    public Item getBlockOfQuartz() {
        return Item.of("minecraft:quartz_block");
    }

    public Item getQuartzStairs() {
        return Item.of("minecraft:quartz_stairs");
    }

    public Item getRawRabbit() {
        return Item.of("minecraft:rabbit");
    }

    public Item getRabbitsFoot() {
        return Item.of("minecraft:rabbit_foot");
    }

    public Item getRabbitHide() {
        return Item.of("minecraft:rabbit_hide");
    }

    public Item getRabbitSpawnEgg() {
        return Item.of("minecraft:rabbit_spawn_egg");
    }

    public Item getRabbitStew() {
        return Item.of("minecraft:rabbit_stew");
    }

    public Item getRails() {
        return Item.of("minecraft:rail");
    }

    public Item getRavagerSpawnEgg() {
        return Item.of("minecraft:ravager_spawn_egg");
    }

    public Item getRedGlazedTerracotta() {
        return Item.of("minecraft:red_glazed_terracotta");
    }

    public Item getRedMushroom() {
        return Item.of("minecraft:red_mushroom");
    }

    public Item getRedMushroomBlock() {
        return Item.of("minecraft:red_mushroom_block");
    }

    public Item getRedNetherBrickSlab() {
        return Item.of("minecraft:red_nether_brick_slab");
    }

    public Item getRedNetherBrickStairs() {
        return Item.of("minecraft:red_nether_brick_stairs");
    }

    public Item getRedNetherBrickWall() {
        return Item.of("minecraft:red_nether_brick_wall");
    }

    public Item getRedNetherBrick() {
        return Item.of("minecraft:red_nether_bricks");
    }

    public Item getRedSandstone() {
        return Item.of("minecraft:red_sandstone");
    }

    public Item getRedSandstoneStairs() {
        return Item.of("minecraft:red_sandstone_stairs");
    }

    public Item getRedSandstoneWall() {
        return Item.of("minecraft:red_sandstone_wall");
    }

    public Item getRedShulkerBox() {
        return Item.of("minecraft:red_shulker_box");
    }

    public Item getRedstoneDust() {
        return Item.of("minecraft:redstone");
    }

    public Item getBlockOfRedstone() {
        return Item.of("minecraft:redstone_block");
    }

    public Item getRedstoneLamp() {
        return Item.of("minecraft:redstone_lamp");
    }

    public Item getRedstoneOre() {
        return Item.of("minecraft:redstone_ore");
    }

    public Item getRedstoneTorch() {
        return Item.of("minecraft:redstone_torch");
    }

    public Item getRedstoneRepeater() {
        return Item.of("minecraft:repeater");
    }

    public Item getRepeatingCommandBlock() {
        return Item.of("minecraft:repeating_command_block");
    }

    public Item getRespawnAnchor() {
        return Item.of("minecraft:respawn_anchor");
    }

    public Item getRottenFlesh() {
        return Item.of("minecraft:rotten_flesh");
    }

    public Item getSaddle() {
        return Item.of("minecraft:saddle");
    }

    public Item getBucketOfSalmon() {
        return Item.of("minecraft:salmon_bucket");
    }

    public Item getSalmonSpawnEgg() {
        return Item.of("minecraft:salmon_spawn_egg");
    }

    public Item getSand() {
        return Item.of("minecraft:sand");
    }

    public Item getSandstone() {
        return Item.of("minecraft:sandstone");
    }

    public Item getSandstoneStairs() {
        return Item.of("minecraft:sandstone_stairs");
    }

    public Item getSandstoneWall() {
        return Item.of("minecraft:sandstone_wall");
    }

    public Item getScaffolding() {
        return Item.of("minecraft:scaffolding");
    }

    public Item getScute() {
        return Item.of("minecraft:scute");
    }

    public Item getSeaLantern() {
        return Item.of("minecraft:sea_lantern");
    }

    public Item getSeaPickle() {
        return Item.of("minecraft:sea_pickle");
    }

    public Item getSeagrass() {
        return Item.of("minecraft:seagrass");
    }

    public Item getShears() {
        return Item.of("minecraft:shears");
    }

    public Item getSheepSpawnEgg() {
        return Item.of("minecraft:sheep_spawn_egg");
    }

    public Item getShield() {
        return Item.of("minecraft:shield");
    }

    public Item getShroomlight() {
        return Item.of("minecraft:shroomlight");
    }

    public Item getShulkerBox() {
        return Item.of("minecraft:shulker_box");
    }

    public Item getShulkerShell() {
        return Item.of("minecraft:shulker_shell");
    }

    public Item getShulkerSpawnEgg() {
        return Item.of("minecraft:shulker_spawn_egg");
    }

    public Item getSilverfishSpawnEgg() {
        return Item.of("minecraft:silverfish_spawn_egg");
    }

    public Item getSkeletonHorseSpawnEgg() {
        return Item.of("minecraft:skeleton_horse_spawn_egg");
    }

    public Item getSkeletonSpawnEgg() {
        return Item.of("minecraft:skeleton_spawn_egg");
    }

    public Item getSkullChargeBannerPattern() {
        return Item.of("minecraft:skull_banner_pattern");
    }

    public Item getSlimeball() {
        return Item.of("minecraft:slime_ball");
    }

    public Item getSlimeBlock() {
        return Item.of("minecraft:slime_block");
    }

    public Item getSlimeSpawnEgg() {
        return Item.of("minecraft:slime_spawn_egg");
    }

    public Item getSmithingTable() {
        return Item.of("minecraft:smithing_table");
    }

    public Item getSmoker() {
        return Item.of("minecraft:smoker");
    }

    public Item getSmoothQuartz() {
        return Item.of("minecraft:smooth_quartz");
    }

    public Item getSmoothQuartzSlab() {
        return Item.of("minecraft:smooth_quartz_slab");
    }

    public Item getSmoothQuartzStairs() {
        return Item.of("minecraft:smooth_quartz_stairs");
    }

    public Item getSmoothRedSandstoneSlab() {
        return Item.of("minecraft:smooth_red_sandstone_slab");
    }

    public Item getSmoothRedSandstoneStairs() {
        return Item.of("minecraft:smooth_red_sandstone_stairs");
    }

    public Item getSmoothSandstoneSlab() {
        return Item.of("minecraft:smooth_sandstone_slab");
    }

    public Item getSmoothSandstoneStairs() {
        return Item.of("minecraft:smooth_sandstone_stairs");
    }

    public Item getSmoothStone() {
        return Item.of("minecraft:smooth_stone");
    }

    public Item getSmoothStoneSlab() {
        return Item.of("minecraft:smooth_stone_slab");
    }

    public Item getSnow() {
        return Item.of("minecraft:snow");
    }

    public Item getSnowBlock() {
        return Item.of("minecraft:snow_block");
    }

    public Item getSnowball() {
        return Item.of("minecraft:snowball");
    }

    public Item getSoulCampfire() {
        return Item.of("minecraft:soul_campfire");
    }

    public Item getSoulLantern() {
        return Item.of("minecraft:soul_lantern");
    }

    public Item getSoulSand() {
        return Item.of("minecraft:soul_sand");
    }

    public Item getSoulSoil() {
        return Item.of("minecraft:soul_soil");
    }

    public Item getSoulTorch() {
        return Item.of("minecraft:soul_torch");
    }

    public Item getMonsterSpawner() {
        return Item.of("minecraft:spawner");
    }

    public Item getSpectralArrow() {
        return Item.of("minecraft:spectral_arrow");
    }

    public Item getSpiderEye() {
        return Item.of("minecraft:spider_eye");
    }

    public Item getSpiderSpawnEgg() {
        return Item.of("minecraft:spider_spawn_egg");
    }

    public Item getSplashPotion() {
        return Item.of("minecraft:splash_potion");
    }

    public Item getSponge() {
        return Item.of("minecraft:sponge");
    }

    public Item getSpruceBoat() {
        return Item.of("minecraft:spruce_boat");
    }

    public Item getSpruceButton() {
        return Item.of("minecraft:spruce_button");
    }

    public Item getSpruceDoor() {
        return Item.of("minecraft:spruce_door");
    }

    public Item getSpruceFence() {
        return Item.of("minecraft:spruce_fence");
    }

    public Item getSpruceFenceGate() {
        return Item.of("minecraft:spruce_fence_gate");
    }

    public Item getSprucePressurePlate() {
        return Item.of("minecraft:spruce_pressure_plate");
    }

    public Item getSpruceSign() {
        return Item.of("minecraft:spruce_sign");
    }

    public Item getSpruceWoodStairs() {
        return Item.of("minecraft:spruce_stairs");
    }

    public Item getSpruceTrapdoor() {
        return Item.of("minecraft:spruce_trapdoor");
    }

    public Item getSpruceWoodWithBark() {
        return Item.of("minecraft:spruce_wood");
    }

    public Item getSquidSpawnEgg() {
        return Item.of("minecraft:squid_spawn_egg");
    }

    public Item getStick() {
        return Item.of("minecraft:stick");
    }

    public Item getStickyPiston() {
        return Item.of("minecraft:sticky_piston");
    }

    public Item getStone() {
        return Item.of("minecraft:stone");
    }

    public Item getStoneAxe() {
        return Item.of("minecraft:stone_axe");
    }

    public Item getStoneBrickStairs() {
        return Item.of("minecraft:stone_brick_stairs");
    }

    public Item getStoneBrickWall() {
        return Item.of("minecraft:stone_brick_wall");
    }

    public Item getStoneBricks() {
        return Item.of("minecraft:stone_bricks");
    }

    public Item getStoneButton() {
        return Item.of("minecraft:stone_button");
    }

    public Item getStoneHoe() {
        return Item.of("minecraft:stone_hoe");
    }

    public Item getStonePickaxe() {
        return Item.of("minecraft:stone_pickaxe");
    }

    public Item getStonePressurePlate() {
        return Item.of("minecraft:stone_pressure_plate");
    }

    public Item getStoneShovel() {
        return Item.of("minecraft:stone_shovel");
    }

    public Item getStoneSlab() {
        return Item.of("minecraft:stone_slab");
    }

    public Item getStoneStairs() {
        return Item.of("minecraft:stone_stairs");
    }

    public Item getStoneSword() {
        return Item.of("minecraft:stone_sword");
    }

    public Item getStonecutter() {
        return Item.of("minecraft:stonecutter");
    }

    public Item getStraySpawnEgg() {
        return Item.of("minecraft:stray_spawn_egg");
    }

    public Item getStriderSpawnEgg() {
        return Item.of("minecraft:strider_spawn_egg");
    }

    public Item getString() {
        return Item.of("minecraft:string");
    }

    public Item getStrippedAcaciaLog() {
        return Item.of("minecraft:stripped_acacia_log");
    }

    public Item getStrippedAcaciaWood() {
        return Item.of("minecraft:stripped_acacia_wood");
    }

    public Item getStrippedBirchWood() {
        return Item.of("minecraft:stripped_birch_wood");
    }

    public Item getStrippedCrimsonHyphae() {
        return Item.of("minecraft:stripped_crimson_hyphae");
    }

    public Item getStrippedCrimsonStem() {
        return Item.of("minecraft:stripped_crimson_stem");
    }

    public Item getStrippedDarkOakLog() {
        return Item.of("minecraft:stripped_dark_oak_log");
    }

    public Item getStrippedDarkOakWood() {
        return Item.of("minecraft:stripped_dark_oak_wood");
    }

    public Item getStrippedJungleWood() {
        return Item.of("minecraft:stripped_jungle_wood");
    }

    public Item getStrippedOakLog() {
        return Item.of("minecraft:stripped_oak_log");
    }

    public Item getStrippedOakWood() {
        return Item.of("minecraft:stripped_oak_wood");
    }

    public Item getStrippedSpruceLog() {
        return Item.of("minecraft:stripped_spruce_log");
    }

    public Item getStrippedSpruceWood() {
        return Item.of("minecraft:stripped_spruce_wood");
    }

    public Item getStrippedWarpedHyphae() {
        return Item.of("minecraft:stripped_warped_hyphae");
    }

    public Item getStrippedWarpedStem() {
        return Item.of("minecraft:stripped_warped_stem");
    }

    public Item getStrippedBirchLog() {
        return Item.of("minecraft:stripped_birch_log");
    }

    public Item getStrippedJungleLog() {
        return Item.of("minecraft:stripped_jungle_log");
    }

    public Item getStructureBlock() {
        return Item.of("minecraft:structure_block");
    }

    public Item getStructureVoid() {
        return Item.of("minecraft:structure_void");
    }

    public Item getSugar() {
        return Item.of("minecraft:sugar");
    }

    public Item getSugarCanes() {
        return Item.of("minecraft:sugar_cane");
    }

    public Item getSunflower() {
        return Item.of("minecraft:sunflower");
    }

    public Item getSuspiciousStew() {
        return Item.of("minecraft:suspicious_stew");
    }

    public Item getSweetBerries() {
        return Item.of("minecraft:sweet_berries");
    }

    public Item getTarget() {
        return Item.of("minecraft:target");
    }

    public Item getTerracotta() {
        return Item.of("minecraft:terracotta");
    }

    public Item getTippedArrow() {
        return Item.of("minecraft:tipped_arrow");
    }

    public Item getTnt() {
        return Item.of("minecraft:tnt");
    }

    public Item getMinecartWithTnt() {
        return Item.of("minecraft:tnt_minecart");
    }

    public Item getTorch() {
        return Item.of("minecraft:torch");
    }

    public Item getTotemOfUndying() {
        return Item.of("minecraft:totem_of_undying");
    }

    public Item getTraderLlamaSpawnEgg() {
        return Item.of("minecraft:trader_llama_spawn_egg");
    }

    public Item getTrappedChest() {
        return Item.of("minecraft:trapped_chest");
    }

    public Item getTrident() {
        return Item.of("minecraft:trident");
    }

    public Item getTripwireHook() {
        return Item.of("minecraft:tripwire_hook");
    }

    public Item getBucketOfTropicalFish() {
        return Item.of("minecraft:tropical_fish_bucket");
    }

    public Item getTropicalFishSpawnEgg() {
        return Item.of("minecraft:tropical_fish_spawn_egg");
    }

    public Item getTubeCoral() {
        return Item.of("minecraft:tube_coral");
    }

    public Item getTubeCoralBlock() {
        return Item.of("minecraft:tube_coral_block");
    }

    public Item getTubeCoralFan() {
        return Item.of("minecraft:tube_coral_fan");
    }

    public Item getTurtleEgg() {
        return Item.of("minecraft:turtle_egg");
    }

    public Item getTurtleShell() {
        return Item.of("minecraft:turtle_helmet");
    }

    public Item getTurtleSpawnEgg() {
        return Item.of("minecraft:turtle_spawn_egg");
    }

    public Item getTwistingVines() {
        return Item.of("minecraft:twisting_vines");
    }

    public Item getVexSpawnEgg() {
        return Item.of("minecraft:vex_spawn_egg");
    }

    public Item getVillagerSpawnEgg() {
        return Item.of("minecraft:villager_spawn_egg");
    }

    public Item getVindicatorSpawnEgg() {
        return Item.of("minecraft:vindicator_spawn_egg");
    }

    public Item getVines() {
        return Item.of("minecraft:vine");
    }

    public Item getWanderingTraderSpawnEgg() {
        return Item.of("minecraft:wandering_trader_spawn_egg");
    }

    public Item getWarpedButton() {
        return Item.of("minecraft:warped_button");
    }

    public Item getWarpedDoor() {
        return Item.of("minecraft:warped_door");
    }

    public Item getWarpedFence() {
        return Item.of("minecraft:warped_fence");
    }

    public Item getWarpedFenceGate() {
        return Item.of("minecraft:warped_fence_gate");
    }

    public Item getWarpedFungus() {
        return Item.of("minecraft:warped_fungus");
    }

    public Item getWarpedFungusOnAStick() {
        return Item.of("minecraft:warped_fungus_on_a_stick");
    }

    public Item getWarpedHyphae() {
        return Item.of("minecraft:warped_hyphae");
    }

    public Item getWarpedNylium() {
        return Item.of("minecraft:warped_nylium");
    }

    public Item getWarpedPlanks() {
        return Item.of("minecraft:warped_planks");
    }

    public Item getWarpedPressurePlate() {
        return Item.of("minecraft:warped_pressure_plate");
    }

    public Item getWarpedRoots() {
        return Item.of("minecraft:warped_roots");
    }

    public Item getWarpedSign() {
        return Item.of("minecraft:warped_sign");
    }

    public Item getWarpedSlab() {
        return Item.of("minecraft:warped_slab");
    }

    public Item getWarpedStairs() {
        return Item.of("minecraft:warped_stairs");
    }

    public Item getWarpedStem() {
        return Item.of("minecraft:warped_stem");
    }

    public Item getWarpedTrapdoor() {
        return Item.of("minecraft:warped_trapdoor");
    }

    public Item getWarpedWartBlock() {
        return Item.of("minecraft:warped_wart_block");
    }

    public Item getWater() {
        return Item.of("minecraft:water");
    }

    public Item getWaterBucket() {
        return Item.of("minecraft:water_bucket");
    }

    public Item getWeepingVines() {
        return Item.of("minecraft:weeping_vines");
    }

    public Item getWheat() {
        return Item.of("minecraft:wheat");
    }

    public Item getSeeds() {
        return Item.of("minecraft:wheat_seeds");
    }

    public Item getWhiteGlazedTerracotta() {
        return Item.of("minecraft:white_glazed_terracotta");
    }

    public Item getWhiteShulkerBox() {
        return Item.of("minecraft:white_shulker_box");
    }

    public Item getWhiteTerracotta() {
        return Item.of("minecraft:white_terracotta");
    }

    public Item getWhiteWool() {
        return Item.of("minecraft:white_wool");
    }

    public Item getWitchSpawnEgg() {
        return Item.of("minecraft:witch_spawn_egg");
    }

    public Item getWitherRose() {
        return Item.of("minecraft:wither_rose");
    }

    public Item getWitherSkeletonSpawnEgg() {
        return Item.of("minecraft:wither_skeleton_spawn_egg");
    }

    public Item getWolfSpawnEgg() {
        return Item.of("minecraft:wolf_spawn_egg");
    }

    public Item getWoodenAxe() {
        return Item.of("minecraft:wooden_axe");
    }

    public Item getWoodenHoe() {
        return Item.of("minecraft:wooden_hoe");
    }

    public Item getWoodenPickaxe() {
        return Item.of("minecraft:wooden_pickaxe");
    }

    public Item getWoodenShovel() {
        return Item.of("minecraft:wooden_shovel");
    }

    public Item getWoodenSword() {
        return Item.of("minecraft:wooden_sword");
    }

    public Item getBookAndQuill() {
        return Item.of("minecraft:writable_book");
    }

    public Item getWrittenBook() {
        return Item.of("minecraft:written_book");
    }

    public Item getYellowGlazedTerracotta() {
        return Item.of("minecraft:yellow_glazed_terracotta");
    }

    public Item getYellowShulkerBox() {
        return Item.of("minecraft:yellow_shulker_box");
    }

    public Item getZoglinSpawnEgg() {
        return Item.of("minecraft:zoglin_spawn_egg");
    }

    public Item getZombieHorseSpawnEgg() {
        return Item.of("minecraft:zombie_horse_spawn_egg");
    }

    public Item getZombieSpawnEgg() {
        return Item.of("minecraft:zombie_spawn_egg");
    }

    public Item getZombieVillagerSpawnEgg() {
        return Item.of("minecraft:zombie_villager_spawn_egg");
    }

    public Item getZombifiedPiglinSpawnEgg() {
        return Item.of("minecraft:zombified_piglin_spawn_egg");
    }

    public abstract Item getYellowBed();

    public abstract Item getYellowCarpet();

    public abstract Item getYellowConcrete();

    public abstract Item getYellowConcretePowder();

    public abstract Item getYellowDye();

    public abstract Item getYellowStainedGlass();

    public abstract Item getYellowStainedGlassPane();

    public abstract Item getYellowTerracotta();

    public abstract Item getYellowWool();

    public abstract Item getZombieHead();
}
