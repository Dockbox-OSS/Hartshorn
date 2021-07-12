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

package org.dockbox.hartshorn.server.minecraft.item.storage;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.server.minecraft.MinecraftVersion;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({ "unused", "OverlyComplexClass", "MethodMayBeStatic" })
public abstract class MinecraftItems {

    // Static as it is possible multiple instances of this type are created
    private static final Map<MinecraftVersion, Map<String, Supplier<Item>>> customItems = HartshornUtils.emptyConcurrentMap();

    public static MinecraftItems instance() {
        return Hartshorn.context().get(MinecraftVersion.class).items();
    }

    public Item custom(String identifier) {
        Map<String, Supplier<Item>> customItemsForVersion = customItems.getOrDefault(this.minecraftVersion(), HartshornUtils.emptyMap());
        return customItemsForVersion.getOrDefault(identifier, () -> instance().air()).get();
    }

    public abstract MinecraftVersion minecraftVersion();

    public Item air() {
        return Item.of(this.airId());
    }

    public String airId() {
        return "minecraft:air";
    }

    public MinecraftItems register(String identifier, Item item) {
        return this.register(identifier, () -> item);
    }

    public MinecraftItems register(String identifier, Supplier<Item> item) {
        customItems.putIfAbsent(this.minecraftVersion(), HartshornUtils.emptyConcurrentMap());
        if (customItems.get(this.minecraftVersion()).containsKey(identifier))
            Hartshorn.log().warn("Overwriting custom item identifier '" + identifier + "'");
        customItems.get(this.minecraftVersion()).put(identifier, item);
        return this;
    }

    public abstract Item acaciaLeaves();

    public abstract Item acaciaLog();

    public abstract Item acaciaPlanks();

    public abstract Item acaciaSapling();

    public abstract Item acaciaWoodSlab();

    public abstract Item allium();

    public abstract Item andesite();

    public abstract Item azureBluet();

    public abstract Item birchLeaves();

    public abstract Item birchLog();

    public abstract Item birchPlanks();

    public abstract Item birchSapling();

    public abstract Item birchWoodSlab();

    public abstract Item blackBanner();

    public abstract Item blackBed();

    public abstract Item blackCarpet();

    public abstract Item blackConcrete();

    public abstract Item blackConcretePowder();

    public abstract Item blackDye();

    public abstract Item blackStainedGlass();

    public abstract Item blackStainedGlassPane();

    public abstract Item blackTerracotta();

    public abstract Item blackWool();

    public abstract Item blueBanner();

    public abstract Item blueBed();

    public abstract Item blueCarpet();

    public abstract Item blueOrchid();

    public abstract Item blueStainedGlass();

    public abstract Item blueStainedGlassPane();

    public abstract Item blueTerracotta();

    public abstract Item blueWool();

    public abstract Item brickSlab();

    public abstract Item brownBanner();

    public abstract Item brownBed();

    public abstract Item brownCarpet();

    public abstract Item brownConcrete();

    public abstract Item brownConcretePowder();

    public abstract Item brownDye();

    public abstract Item brownStainedGlass();

    public abstract Item brownStainedGlassPane();

    public abstract Item brownTerracotta();

    public abstract Item brownWool();

    public abstract Item charcoal();

    public abstract Item slightlyDamagedAnvil();

    public abstract Item chiseledRedSandstone();

    public abstract Item chiseledSandstone();

    public abstract Item chiseledStoneBricks();

    public abstract Item coarseDirt();

    public abstract Item cobblestoneSlab();

    public abstract Item cookedSalmon();

    public abstract Item crackedStoneBricks();

    public abstract Item creeperHead();

    public abstract Item cyanBanner();

    public abstract Item cyanBed();

    public abstract Item cyanCarpet();

    public abstract Item cyanConcrete();

    public abstract Item cyanConcretePowder();

    public abstract Item cyanDye();

    public abstract Item cyanStainedGlass();

    public abstract Item cyanStainedGlassPane();

    public abstract Item cyanTerracotta();

    public abstract Item cyanWool();

    public abstract Item veryDamagedAnvil();

    public abstract Item darkOakLeaves();

    public abstract Item darkOakLog();

    public abstract Item darkOakPlanks();

    public abstract Item darkOakSapling();

    public abstract Item darkOakWoodSlab();

    public abstract Item darkPrismarine();

    public abstract Item diorite();

    public abstract Item dragonHead();

    public abstract Item enchantedGoldenApple();

    public abstract Item fern();

    public abstract Item granite();

    public abstract Item grass();

    public abstract Item grayBanner();

    public abstract Item grayBed();

    public abstract Item grayCarpet();

    public abstract Item grayConcrete();

    public abstract Item grayConcretePowder();

    public abstract Item grayDye();

    public abstract Item grayStainedGlass();

    public abstract Item grayStainedGlassPane();

    public abstract Item grayTerracotta();

    public abstract Item grayWool();

    public abstract Item greenBanner();

    public abstract Item greenBed();

    public abstract Item greenCarpet();

    public abstract Item greenConcrete();

    public abstract Item greenConcretePowder();

    public abstract Item greenDye();

    public abstract Item greenStainedGlass();

    public abstract Item greenStainedGlassPane();

    public abstract Item greenTerracotta();

    public abstract Item greenWool();

    public abstract Item chiseledStoneBrickMonsterEgg();

    public abstract Item cobblestoneMonsterEgg();

    public abstract Item crackedStoneBrickMonsterEgg();

    public abstract Item mossyStoneBrickMonsterEgg();

    public abstract Item stoneBrickMonsterEgg();

    public abstract Item jungleLeaves();

    public abstract Item jungleLog();

    public abstract Item junglePlanks();

    public abstract Item jungleSapling();

    public abstract Item jungleWoodSlab();

    public abstract Item largeFern();

    public abstract Item lightBlueBanner();

    public abstract Item lightBlueBed();

    public abstract Item lightBlueCarpet();

    public abstract Item lightBlueConcrete();

    public abstract Item lightBlueConcretePowder();

    public abstract Item lightBlueDye();

    public abstract Item lightBlueStainedGlass();

    public abstract Item lightBlueStainedGlassPane();

    public abstract Item lightBlueTerracotta();

    public abstract Item lightBlueWool();

    public abstract Item lightGrayBanner();

    public abstract Item lightGrayBed();

    public abstract Item lightGrayCarpet();

    public abstract Item lightGrayConcrete();

    public abstract Item lightGrayConcretePowder();

    public abstract Item lightGrayDye();

    public abstract Item lightGrayStainedGlass();

    public abstract Item lightGrayStainedGlassPane();

    public abstract Item lightGrayTerracotta();

    public abstract Item lightGrayWool();

    public abstract Item lilac();

    public abstract Item limeBanner();

    public abstract Item limeBed();

    public abstract Item limeCarpet();

    public abstract Item limeConcrete();

    public abstract Item limeConcretePowder();

    public abstract Item limeDye();

    public abstract Item limeStainedGlass();

    public abstract Item limeStainedGlassPane();

    public abstract Item limeTerracotta();

    public abstract Item limeWool();

    public abstract Item magentaBanner();

    public abstract Item magentaBed();

    public abstract Item magentaCarpet();

    public abstract Item magentaConcrete();

    public abstract Item magentaConcretePowder();

    public abstract Item magentaDye();

    public abstract Item magentaStainedGlass();

    public abstract Item magentaStainedGlassPane();

    public abstract Item magentaTerracotta();

    public abstract Item magentaWool();

    public abstract Item mossyCobblestoneWall();

    public abstract Item mossyStoneBricks();

    public abstract Item netherBrickSlab();

    public abstract Item oakWoodSlab();

    public abstract Item orangeBanner();

    public abstract Item orangeBed();

    public abstract Item orangeCarpet();

    public abstract Item orangeConcrete();

    public abstract Item orangeConcretePowder();

    public abstract Item orangeDye();

    public abstract Item orangeStainedGlass();

    public abstract Item orangeStainedGlassPane();

    public abstract Item orangeTerracotta();

    public abstract Item orangeTulip();

    public abstract Item orangeWool();

    public abstract Item oxeyeDaisy();

    public abstract Item peony();

    public abstract Item pinkBanner();

    public abstract Item pinkBed();

    public abstract Item pinkCarpet();

    public abstract Item pinkConcrete();

    public abstract Item pinkConcretePowder();

    public abstract Item pinkDye();

    public abstract Item pinkStainedGlass();

    public abstract Item pinkStainedGlassPane();

    public abstract Item pinkTerracotta();

    public abstract Item pinkTulip();

    public abstract Item pinkWool();

    public abstract Item steveHead();

    public abstract Item podzol();

    public abstract Item polishedAndesite();

    public abstract Item polishedDiorite();

    public abstract Item polishedGranite();

    public abstract Item prismarineBricks();

    public abstract Item pufferfish();

    public abstract Item purpleBanner();

    public abstract Item purpleBed();

    public abstract Item purpleCarpet();

    public abstract Item purpleConcrete();

    public abstract Item purpleConcretePowder();

    public abstract Item purpleDye();

    public abstract Item purpleStainedGlass();

    public abstract Item purpleStainedGlassPane();

    public abstract Item purpleTerracotta();

    public abstract Item purpleWool();

    public abstract Item pillarQuartzBlock();

    public abstract Item quartzSlab();

    public abstract Item redBanner();

    public abstract Item redBed();

    public abstract Item redCarpet();

    public abstract Item redConcrete();

    public abstract Item redConcretePowder();

    public abstract Item redDye();

    public abstract Item redSand();

    public abstract Item redSandstoneSlab();

    public abstract Item redStainedGlass();

    public abstract Item redStainedGlassPane();

    public abstract Item redTerracotta();

    public abstract Item redTulip();

    public abstract Item redWool();

    public abstract Item roseBush();

    public abstract Item rawSalmon();

    public abstract Item sandstoneSlab();

    public abstract Item skeletonSkull();

    public abstract Item smoothRedSandstone();

    public abstract Item smoothSandstone();

    public abstract Item spruceLeaves();

    public abstract Item spruceLog();

    public abstract Item sprucePlanks();

    public abstract Item spruceSapling();

    public abstract Item spruceWoodSlab();

    public abstract Item stoneBrickSlab();

    public abstract Item doubleTallgrass();

    public abstract Item tropicalFish();

    public abstract Item wetSponge();

    public abstract Item whiteBanner();

    public abstract Item whiteBed();

    public abstract Item whiteCarpet();

    public abstract Item whiteConcrete();

    public abstract Item whiteConcretePowder();

    public abstract Item whiteDye();

    public abstract Item whiteStainedGlass();

    public abstract Item whiteStainedGlassPane();

    public abstract Item whiteTulip();

    public abstract Item witherSkeletonSkull();

    public abstract Item yellowBanner();

    public Item acaciaBoat() {
        return Item.of("minecraft:acacia_boat");
    }

    public Item acaciaButton() {
        return Item.of("minecraft:acacia_button");
    }

    public Item acaciaDoor() {
        return Item.of("minecraft:acacia_door");
    }

    public Item acaciaFence() {
        return Item.of("minecraft:acacia_fence");
    }

    public Item acaciaFenceGate() {
        return Item.of("minecraft:acacia_fence_gate");
    }

    public Item acaciaPressurePlate() {
        return Item.of("minecraft:acacia_pressure_plate");
    }

    public Item acaciaSign() {
        return Item.of("minecraft:acacia_sign");
    }

    public Item acaciaWoodStairs() {
        return Item.of("minecraft:acacia_stairs");
    }

    public Item acaciaTrapdoor() {
        return Item.of("minecraft:acacia_trapdoor");
    }

    public Item acaciaWoodWithBark() {
        return Item.of("minecraft:acacia_wood");
    }

    public Item activatorRails() {
        return Item.of("minecraft:activator_rail");
    }

    public Item ancientDebris() {
        return Item.of("minecraft:ancient_debris");
    }

    public Item andesiteSlab() {
        return Item.of("minecraft:andesite_slab");
    }

    public Item andesiteStairs() {
        return Item.of("minecraft:andesite_stairs");
    }

    public Item andesiteWall() {
        return Item.of("minecraft:andesite_wall");
    }

    public Item anvil() {
        return Item.of("minecraft:anvil");
    }

    public Item apple() {
        return Item.of("minecraft:apple");
    }

    public Item armorStand() {
        return Item.of("minecraft:armor_stand");
    }

    public Item arrow() {
        return Item.of("minecraft:arrow");
    }

    public Item bakedPotato() {
        return Item.of("minecraft:baked_potato");
    }

    public Item bamboo() {
        return Item.of("minecraft:bamboo");
    }

    public Item barrel() {
        return Item.of("minecraft:barrel");
    }

    public Item barrier() {
        return Item.of("minecraft:barrier");
    }

    public Item basalt() {
        return Item.of("minecraft:basalt");
    }

    public Item batSpawnEgg() {
        return Item.of("minecraft:bat_spawn_egg");
    }

    public Item beacon() {
        return Item.of("minecraft:beacon");
    }

    public Item bedrock() {
        return Item.of("minecraft:bedrock");
    }

    public Item beeNest() {
        return Item.of("minecraft:bee_nest");
    }

    public Item beeSpawnEgg() {
        return Item.of("minecraft:bee_spawn_egg");
    }

    public Item rawBeef() {
        return Item.of("minecraft:beef");
    }

    public Item beehive() {
        return Item.of("minecraft:beehive");
    }

    public Item beetroot() {
        return Item.of("minecraft:beetroot");
    }

    public Item beetrootSeeds() {
        return Item.of("minecraft:beetroot_seeds");
    }

    public Item beetrootSoup() {
        return Item.of("minecraft:beetroot_soup");
    }

    public Item bell() {
        return Item.of("minecraft:bell");
    }

    public Item birchBoat() {
        return Item.of("minecraft:birch_boat");
    }

    public Item birchButton() {
        return Item.of("minecraft:birch_button");
    }

    public Item birchDoor() {
        return Item.of("minecraft:birch_door");
    }

    public Item birchFence() {
        return Item.of("minecraft:birch_fence");
    }

    public Item birchFenceGate() {
        return Item.of("minecraft:birch_fence_gate");
    }

    public Item birchPressurePlate() {
        return Item.of("minecraft:birch_pressure_plate");
    }

    public Item birchSign() {
        return Item.of("minecraft:birch_sign");
    }

    public Item birchWoodStairs() {
        return Item.of("minecraft:birch_stairs");
    }

    public Item birchTrapdoor() {
        return Item.of("minecraft:birch_trapdoor");
    }

    public Item birchWoodWithBark() {
        return Item.of("minecraft:birch_wood");
    }

    public Item blackGlazedTerracotta() {
        return Item.of("minecraft:black_glazed_terracotta");
    }

    public Item blackShulkerBox() {
        return Item.of("minecraft:black_shulker_box");
    }

    public Item blackstone() {
        return Item.of("minecraft:blackstone");
    }

    public Item blackstoneSlab() {
        return Item.of("minecraft:blackstone_slab");
    }

    public Item blackstoneStairs() {
        return Item.of("minecraft:blackstone_stairs");
    }

    public Item blackstoneWall() {
        return Item.of("minecraft:blackstone_wall");
    }

    public Item blastFurnace() {
        return Item.of("minecraft:blast_furnace");
    }

    public Item blazePowder() {
        return Item.of("minecraft:blaze_powder");
    }

    public Item blazeRod() {
        return Item.of("minecraft:blaze_rod");
    }

    public Item blazeSpawnEgg() {
        return Item.of("minecraft:blaze_spawn_egg");
    }

    public Item blueConcrete() {
        return Item.of("minecraft:blue_concrete");
    }

    public Item blueConcretePowder() {
        return Item.of("minecraft:blue_concrete_powder");
    }

    public Item blueDye() {
        return Item.of("minecraft:blue_dye");
    }

    public Item blueGlazedTerracotta() {
        return Item.of("minecraft:blue_glazed_terracotta");
    }

    public Item blueIce() {
        return Item.of("minecraft:blue_ice");
    }

    public Item blueShulkerBox() {
        return Item.of("minecraft:blue_shulker_box");
    }

    public Item bone() {
        return Item.of("minecraft:bone");
    }

    public Item boneBlock() {
        return Item.of("minecraft:bone_block");
    }

    public Item boneMeal() {
        return Item.of("minecraft:bone_meal");
    }

    public Item book() {
        return Item.of("minecraft:book");
    }

    public Item bookshelf() {
        return Item.of("minecraft:bookshelf");
    }

    public Item bow() {
        return Item.of("minecraft:bow");
    }

    public Item bowl() {
        return Item.of("minecraft:bowl");
    }

    public Item brainCoral() {
        return Item.of("minecraft:brain_coral");
    }

    public Item brainCoralBlock() {
        return Item.of("minecraft:brain_coral_block");
    }

    public Item brainCoralFan() {
        return Item.of("minecraft:brain_coral_fan");
    }

    public Item bread() {
        return Item.of("minecraft:bread");
    }

    public Item brewingStand() {
        return Item.of("minecraft:brewing_stand");
    }

    public Item brick() {
        return Item.of("minecraft:brick");
    }

    public Item brickStairs() {
        return Item.of("minecraft:brick_stairs");
    }

    public Item brickWall() {
        return Item.of("minecraft:brick_wall");
    }

    public Item bricks() {
        return Item.of("minecraft:bricks");
    }

    public Item brownGlazedTerracotta() {
        return Item.of("minecraft:brown_glazed_terracotta");
    }

    public Item brownMushroom() {
        return Item.of("minecraft:brown_mushroom");
    }

    public Item brownMushroomBlock() {
        return Item.of("minecraft:brown_mushroom_block");
    }

    public Item brownShulkerBox() {
        return Item.of("minecraft:brown_shulker_box");
    }

    public Item bubbleCoral() {
        return Item.of("minecraft:bubble_coral");
    }

    public Item bubbleCoralBlock() {
        return Item.of("minecraft:bubble_coral_block");
    }

    public Item bubbleCoralFan() {
        return Item.of("minecraft:bubble_coral_fan");
    }

    public Item bucket() {
        return Item.of("minecraft:bucket");
    }

    public Item cactus() {
        return Item.of("minecraft:cactus");
    }

    public Item cake() {
        return Item.of("minecraft:cake");
    }

    public Item campfire() {
        return Item.of("minecraft:campfire");
    }

    public Item carrot() {
        return Item.of("minecraft:carrot");
    }

    public Item carrotOnAStick() {
        return Item.of("minecraft:carrot_on_a_stick");
    }

    public Item cartographyTable() {
        return Item.of("minecraft:cartography_table");
    }

    public Item carvedPumpkin() {
        return Item.of("minecraft:carved_pumpkin");
    }

    public Item catSpawnEgg() {
        return Item.of("minecraft:cat_spawn_egg");
    }

    public Item cauldron() {
        return Item.of("minecraft:cauldron");
    }

    public Item caveSpiderSpawnEgg() {
        return Item.of("minecraft:cave_spider_spawn_egg");
    }

    public Item chain() {
        return Item.of("minecraft:chain");
    }

    public Item chainCommandBlock() {
        return Item.of("minecraft:chain_command_block");
    }

    public Item chainBoots() {
        return Item.of("minecraft:chainmail_boots");
    }

    public Item chainChestplate() {
        return Item.of("minecraft:chainmail_chestplate");
    }

    public Item chainHelmet() {
        return Item.of("minecraft:chainmail_helmet");
    }

    public Item chainLeggings() {
        return Item.of("minecraft:chainmail_leggings");
    }

    public Item chest() {
        return Item.of("minecraft:chest");
    }

    public Item minecartWithChest() {
        return Item.of("minecraft:chest_minecart");
    }

    public Item rawChicken() {
        return Item.of("minecraft:chicken");
    }

    public Item chickenSpawnEgg() {
        return Item.of("minecraft:chicken_spawn_egg");
    }

    public Item chiseledNetherBricks() {
        return Item.of("minecraft:chiseled_nether_bricks");
    }

    public Item chiseledPolishedBlackstone() {
        return Item.of("minecraft:chiseled_polished_blackstone");
    }

    public Item chiseledQuartzBlock() {
        return Item.of("minecraft:chiseled_quartz_block");
    }

    public Item chorusFlower() {
        return Item.of("minecraft:chorus_flower");
    }

    public Item chorusFruit() {
        return Item.of("minecraft:chorus_fruit");
    }

    public Item chorusPlant() {
        return Item.of("minecraft:chorus_plant");
    }

    public Item clayBlock() {
        return Item.of("minecraft:clay");
    }

    public Item clay() {
        return Item.of("minecraft:clay_ball");
    }

    public Item clock() {
        return Item.of("minecraft:clock");
    }

    public Item coal() {
        return Item.of("minecraft:coal");
    }

    public Item coalBlock() {
        return Item.of("minecraft:coal_block");
    }

    public Item coalOre() {
        return Item.of("minecraft:coal_ore");
    }

    public Item cobblestone() {
        return Item.of("minecraft:cobblestone");
    }

    public Item cobblestoneStairs() {
        return Item.of("minecraft:cobblestone_stairs");
    }

    public Item cobblestoneWall() {
        return Item.of("minecraft:cobblestone_wall");
    }

    public Item cobweb() {
        return Item.of("minecraft:cobweb");
    }

    public Item cocoaBeans() {
        return Item.of("minecraft:cocoa_beans");
    }

    public Item rawCod() {
        return Item.of("minecraft:cod");
    }

    public Item bucketOfCod() {
        return Item.of("minecraft:cod_bucket");
    }

    public Item codSpawnEgg() {
        return Item.of("minecraft:cod_spawn_egg");
    }

    public Item commandBlock() {
        return Item.of("minecraft:command_block");
    }

    public Item minecartWithCommandBlock() {
        return Item.of("minecraft:command_block_minecart");
    }

    public Item redstoneComparator() {
        return Item.of("minecraft:comparator");
    }

    public Item compass() {
        return Item.of("minecraft:compass");
    }

    public Item composter() {
        return Item.of("minecraft:composter");
    }

    public Item conduit() {
        return Item.of("minecraft:conduit");
    }

    public Item steak() {
        return Item.of("minecraft:cooked_beef");
    }

    public Item cookedChicken() {
        return Item.of("minecraft:cooked_chicken");
    }

    public Item cookedCod() {
        return Item.of("minecraft:cooked_cod");
    }

    public Item cookedMutton() {
        return Item.of("minecraft:cooked_mutton");
    }

    public Item cookedPorkchop() {
        return Item.of("minecraft:cooked_porkchop");
    }

    public Item cookedRabbit() {
        return Item.of("minecraft:cooked_rabbit");
    }

    public Item cookie() {
        return Item.of("minecraft:cookie");
    }

    public Item cornflower() {
        return Item.of("minecraft:cornflower");
    }

    public Item cowSpawnEgg() {
        return Item.of("minecraft:cow_spawn_egg");
    }

    public Item crackedNetherBricks() {
        return Item.of("minecraft:cracked_nether_bricks");
    }

    public Item crackedPolishedBlackstoneBricks() {
        return Item.of("minecraft:cracked_polished_blackstone_bricks");
    }

    public Item craftingTable() {
        return Item.of("minecraft:crafting_table");
    }

    public Item creeperChargeBannerPattern() {
        return Item.of("minecraft:creeper_banner_pattern");
    }

    public Item creeperSpawnEgg() {
        return Item.of("minecraft:creeper_spawn_egg");
    }

    public Item crimsonButton() {
        return Item.of("minecraft:crimson_button");
    }

    public Item crimsonDoor() {
        return Item.of("minecraft:crimson_door");
    }

    public Item crimsonFence() {
        return Item.of("minecraft:crimson_fence");
    }

    public Item crimsonFenceGate() {
        return Item.of("minecraft:crimson_fence_gate");
    }

    public Item crimsonFungus() {
        return Item.of("minecraft:crimson_fungus");
    }

    public Item crimsonHyphae() {
        return Item.of("minecraft:crimson_hyphae");
    }

    public Item crimsonNylium() {
        return Item.of("minecraft:crimson_nylium");
    }

    public Item crimsonPlanks() {
        return Item.of("minecraft:crimson_planks");
    }

    public Item crimsonPressurePlate() {
        return Item.of("minecraft:crimson_pressure_plate");
    }

    public Item crimsonRoots() {
        return Item.of("minecraft:crimson_roots");
    }

    public Item crimsonSign() {
        return Item.of("minecraft:crimson_sign");
    }

    public Item crimsonSlab() {
        return Item.of("minecraft:crimson_slab");
    }

    public Item crimsonStairs() {
        return Item.of("minecraft:crimson_stairs");
    }

    public Item crimsonStem() {
        return Item.of("minecraft:crimson_stem");
    }

    public Item crimsonTrapdoor() {
        return Item.of("minecraft:crimson_trapdoor");
    }

    public Item crossbow() {
        return Item.of("minecraft:crossbow");
    }

    public Item cryingObsidian() {
        return Item.of("minecraft:crying_obsidian");
    }

    public Item cutRedSandstone() {
        return Item.of("minecraft:cut_red_sandstone");
    }

    public Item cutRedSandstoneSlab() {
        return Item.of("minecraft:cut_red_sandstone_slab");
    }

    public Item cutSandstone() {
        return Item.of("minecraft:cut_sandstone");
    }

    public Item cutSandstoneSlab() {
        return Item.of("minecraft:cut_sandstone_slab");
    }

    public Item cyanGlazedTerracotta() {
        return Item.of("minecraft:cyan_glazed_terracotta");
    }

    public Item cyanShulkerBox() {
        return Item.of("minecraft:cyan_shulker_box");
    }

    public Item dandelion() {
        return Item.of("minecraft:dandelion");
    }

    public Item darkOakBoat() {
        return Item.of("minecraft:dark_oak_boat");
    }

    public Item darkOakButton() {
        return Item.of("minecraft:dark_oak_button");
    }

    public Item darkOakDoor() {
        return Item.of("minecraft:dark_oak_door");
    }

    public Item darkOakFence() {
        return Item.of("minecraft:dark_oak_fence");
    }

    public Item darkOakFenceGate() {
        return Item.of("minecraft:dark_oak_fence_gate");
    }

    public Item darkOakPressurePlate() {
        return Item.of("minecraft:dark_oak_pressure_plate");
    }

    public Item darkOakSign() {
        return Item.of("minecraft:dark_oak_sign");
    }

    public Item darkOakWoodStairs() {
        return Item.of("minecraft:dark_oak_stairs");
    }

    public Item darkOakTrapdoor() {
        return Item.of("minecraft:dark_oak_trapdoor");
    }

    public Item darkOakWoodWithBark() {
        return Item.of("minecraft:dark_oak_wood");
    }

    public Item darkPrismarineSlab() {
        return Item.of("minecraft:dark_prismarine_slab");
    }

    public Item darkPrismarineStairs() {
        return Item.of("minecraft:dark_prismarine_stairs");
    }

    public Item daylightSensor() {
        return Item.of("minecraft:daylight_detector");
    }

    public Item deadBrainCoral() {
        return Item.of("minecraft:dead_brain_coral");
    }

    public Item deadBrainCoralBlock() {
        return Item.of("minecraft:dead_brain_coral_block");
    }

    public Item deadBrainCoralFan() {
        return Item.of("minecraft:dead_brain_coral_fan");
    }

    public Item deadBubbleCoral() {
        return Item.of("minecraft:dead_bubble_coral");
    }

    public Item deadBubbleCoralBlock() {
        return Item.of("minecraft:dead_bubble_coral_block");
    }

    public Item deadBubbleCoralFan() {
        return Item.of("minecraft:dead_bubble_coral_fan");
    }

    public Item deadBush() {
        return Item.of("minecraft:dead_bush");
    }

    public Item deadFireCoral() {
        return Item.of("minecraft:dead_fire_coral");
    }

    public Item deadFireCoralBlock() {
        return Item.of("minecraft:dead_fire_coral_block");
    }

    public Item deadFireCoralFan() {
        return Item.of("minecraft:dead_fire_coral_fan");
    }

    public Item deadHornCoral() {
        return Item.of("minecraft:dead_horn_coral");
    }

    public Item deadHornCoralBlock() {
        return Item.of("minecraft:dead_horn_coral_block");
    }

    public Item deadHornCoralFan() {
        return Item.of("minecraft:dead_horn_coral_fan");
    }

    public Item deadTubeCoral() {
        return Item.of("minecraft:dead_tube_coral");
    }

    public Item deadTubeCoralBlock() {
        return Item.of("minecraft:dead_tube_coral_block");
    }

    public Item deadTubeCoralFan() {
        return Item.of("minecraft:dead_tube_coral_fan");
    }

    public Item detectorRails() {
        return Item.of("minecraft:detector_rail");
    }

    public Item diamond() {
        return Item.of("minecraft:diamond");
    }

    public Item diamondAxe() {
        return Item.of("minecraft:diamond_axe");
    }

    public Item blockOfDiamond() {
        return Item.of("minecraft:diamond_block");
    }

    public Item diamondBoots() {
        return Item.of("minecraft:diamond_boots");
    }

    public Item diamondChestplate() {
        return Item.of("minecraft:diamond_chestplate");
    }

    public Item diamondHelmet() {
        return Item.of("minecraft:diamond_helmet");
    }

    public Item diamondHoe() {
        return Item.of("minecraft:diamond_hoe");
    }

    public Item diamondHorseArmor() {
        return Item.of("minecraft:diamond_horse_armor");
    }

    public Item diamondLeggings() {
        return Item.of("minecraft:diamond_leggings");
    }

    public Item diamondOre() {
        return Item.of("minecraft:diamond_ore");
    }

    public Item diamondPickaxe() {
        return Item.of("minecraft:diamond_pickaxe");
    }

    public Item diamondShovel() {
        return Item.of("minecraft:diamond_shovel");
    }

    public Item diamondSword() {
        return Item.of("minecraft:diamond_sword");
    }

    public Item dioriteSlab() {
        return Item.of("minecraft:diorite_slab");
    }

    public Item dioriteStairs() {
        return Item.of("minecraft:diorite_stairs");
    }

    public Item dioriteWall() {
        return Item.of("minecraft:diorite_wall");
    }

    public Item dirt() {
        return Item.of("minecraft:dirt");
    }

    public Item dispenser() {
        return Item.of("minecraft:dispenser");
    }

    public Item dolphinSpawnEgg() {
        return Item.of("minecraft:dolphin_spawn_egg");
    }

    public Item donkeySpawnEgg() {
        return Item.of("minecraft:donkey_spawn_egg");
    }

    public Item dragonsBreath() {
        return Item.of("minecraft:dragon_breath");
    }

    public Item dragonEgg() {
        return Item.of("minecraft:dragon_egg");
    }

    public Item driedKelp() {
        return Item.of("minecraft:dried_kelp");
    }

    public Item driedKelpBlock() {
        return Item.of("minecraft:dried_kelp_block");
    }

    public Item dropper() {
        return Item.of("minecraft:dropper");
    }

    public Item drownedSpawnEgg() {
        return Item.of("minecraft:drowned_spawn_egg");
    }

    public Item egg() {
        return Item.of("minecraft:egg");
    }

    public Item elderGuardianSpawnEgg() {
        return Item.of("minecraft:elder_guardian_spawn_egg");
    }

    public Item elytra() {
        return Item.of("minecraft:elytra");
    }

    public Item emerald() {
        return Item.of("minecraft:emerald");
    }

    public Item blockOfEmerald() {
        return Item.of("minecraft:emerald_block");
    }

    public Item emeraldOre() {
        return Item.of("minecraft:emerald_ore");
    }

    public Item enchantedBook() {
        return Item.of("minecraft:enchanted_book");
    }

    public Item enchantingTable() {
        return Item.of("minecraft:enchanting_table");
    }

    public Item endCrystal() {
        return Item.of("minecraft:end_crystal");
    }

    public Item endPortalFrame() {
        return Item.of("minecraft:end_portal_frame");
    }

    public Item endRod() {
        return Item.of("minecraft:end_rod");
    }

    public Item endStone() {
        return Item.of("minecraft:end_stone");
    }

    public Item endStoneBrickSlab() {
        return Item.of("minecraft:end_stone_brick_slab");
    }

    public Item endStoneBrickStairs() {
        return Item.of("minecraft:end_stone_brick_stairs");
    }

    public Item endStoneBrickWall() {
        return Item.of("minecraft:end_stone_brick_wall");
    }

    public Item endStoneBricks() {
        return Item.of("minecraft:end_stone_bricks");
    }

    public Item enderChest() {
        return Item.of("minecraft:ender_chest");
    }

    public Item eyeOfEnder() {
        return Item.of("minecraft:ender_eye");
    }

    public Item enderPearl() {
        return Item.of("minecraft:ender_pearl");
    }

    public Item endermanSpawnEgg() {
        return Item.of("minecraft:enderman_spawn_egg");
    }

    public Item endermiteSpawnEgg() {
        return Item.of("minecraft:endermite_spawn_egg");
    }

    public Item evokerSpawnEgg() {
        return Item.of("minecraft:evoker_spawn_egg");
    }

    public Item bottleOEnchanting() {
        return Item.of("minecraft:experience_bottle");
    }

    public Item farmland() {
        return Item.of("minecraft:farmland");
    }

    public Item feather() {
        return Item.of("minecraft:feather");
    }

    public Item fermentedSpiderEye() {
        return Item.of("minecraft:fermented_spider_eye");
    }

    public Item filledMap() {
        return Item.of("minecraft:filled_map");
    }

    public Item fireCharge() {
        return Item.of("minecraft:fire_charge");
    }

    public Item fireCoral() {
        return Item.of("minecraft:fire_coral");
    }

    public Item fireCoralBlock() {
        return Item.of("minecraft:fire_coral_block");
    }

    public Item fireCoralFan() {
        return Item.of("minecraft:fire_coral_fan");
    }

    public Item fireworkRocket() {
        return Item.of("minecraft:firework_rocket");
    }

    public Item fireworkStar() {
        return Item.of("minecraft:firework_star");
    }

    public Item fishingRod() {
        return Item.of("minecraft:fishing_rod");
    }

    public Item fletchingTable() {
        return Item.of("minecraft:fletching_table");
    }

    public Item flint() {
        return Item.of("minecraft:flint");
    }

    public Item flintAndSteel() {
        return Item.of("minecraft:flint_and_steel");
    }

    public Item flowerChargeBannerPattern() {
        return Item.of("minecraft:flower_banner_pattern");
    }

    public Item flowerPot() {
        return Item.of("minecraft:flower_pot");
    }

    public Item foxSpawnEgg() {
        return Item.of("minecraft:fox_spawn_egg");
    }

    public Item furnace() {
        return Item.of("minecraft:furnace");
    }

    public Item minecartWithFurnace() {
        return Item.of("minecraft:furnace_minecart");
    }

    public Item ghastSpawnEgg() {
        return Item.of("minecraft:ghast_spawn_egg");
    }

    public Item ghastTear() {
        return Item.of("minecraft:ghast_tear");
    }

    public Item gildedBlackstone() {
        return Item.of("minecraft:gilded_blackstone");
    }

    public Item glass() {
        return Item.of("minecraft:glass");
    }

    public Item glassBottle() {
        return Item.of("minecraft:glass_bottle");
    }

    public Item glassPane() {
        return Item.of("minecraft:glass_pane");
    }

    public Item glisteringMelon() {
        return Item.of("minecraft:glistering_melon_slice");
    }

    public Item globeBannerPattern() {
        return Item.of("minecraft:globe_banner_pattern");
    }

    public Item glowstone() {
        return Item.of("minecraft:glowstone");
    }

    public Item glowstoneDust() {
        return Item.of("minecraft:glowstone_dust");
    }

    public Item blockOfGold() {
        return Item.of("minecraft:gold_block");
    }

    public Item goldIngot() {
        return Item.of("minecraft:gold_ingot");
    }

    public Item goldNugget() {
        return Item.of("minecraft:gold_nugget");
    }

    public Item goldOre() {
        return Item.of("minecraft:gold_ore");
    }

    public Item goldenApple() {
        return Item.of("minecraft:golden_apple");
    }

    public Item goldenAxe() {
        return Item.of("minecraft:golden_axe");
    }

    public Item goldenBoots() {
        return Item.of("minecraft:golden_boots");
    }

    public Item goldenCarrot() {
        return Item.of("minecraft:golden_carrot");
    }

    public Item goldenChestplate() {
        return Item.of("minecraft:golden_chestplate");
    }

    public Item goldenHelmet() {
        return Item.of("minecraft:golden_helmet");
    }

    public Item goldenHoe() {
        return Item.of("minecraft:golden_hoe");
    }

    public Item goldHorseArmor() {
        return Item.of("minecraft:golden_horse_armor");
    }

    public Item goldenLeggings() {
        return Item.of("minecraft:golden_leggings");
    }

    public Item goldenPickaxe() {
        return Item.of("minecraft:golden_pickaxe");
    }

    public Item goldenShovel() {
        return Item.of("minecraft:golden_shovel");
    }

    public Item goldenSword() {
        return Item.of("minecraft:golden_sword");
    }

    public Item graniteSlab() {
        return Item.of("minecraft:granite_slab");
    }

    public Item graniteStairs() {
        return Item.of("minecraft:granite_stairs");
    }

    public Item graniteWall() {
        return Item.of("minecraft:granite_wall");
    }

    public Item grassBlock() {
        return Item.of("minecraft:grass_block");
    }

    public Item grassPath() {
        return Item.of("minecraft:grass_path");
    }

    public Item gravel() {
        return Item.of("minecraft:gravel");
    }

    public Item grayGlazedTerracotta() {
        return Item.of("minecraft:gray_glazed_terracotta");
    }

    public Item grayShulkerBox() {
        return Item.of("minecraft:gray_shulker_box");
    }

    public Item greenGlazedTerracotta() {
        return Item.of("minecraft:green_glazed_terracotta");
    }

    public Item greenShulkerBox() {
        return Item.of("minecraft:green_shulker_box");
    }

    public Item grindstone() {
        return Item.of("minecraft:grindstone");
    }

    public Item guardianSpawnEgg() {
        return Item.of("minecraft:guardian_spawn_egg");
    }

    public Item gunpowder() {
        return Item.of("minecraft:gunpowder");
    }

    public Item hayBale() {
        return Item.of("minecraft:hay_block");
    }

    public Item heartOfTheSea() {
        return Item.of("minecraft:heart_of_the_sea");
    }

    public Item heavyWeightedPressurePlate() {
        return Item.of("minecraft:heavy_weighted_pressure_plate");
    }

    public Item hoglinSpawnEgg() {
        return Item.of("minecraft:hoglin_spawn_egg");
    }

    public Item honeyBlock() {
        return Item.of("minecraft:honey_block");
    }

    public Item honeyBottle() {
        return Item.of("minecraft:honey_bottle");
    }

    public Item honeycomb() {
        return Item.of("minecraft:honeycomb");
    }

    public Item honeycombBlock() {
        return Item.of("minecraft:honeycomb_block");
    }

    public Item hopper() {
        return Item.of("minecraft:hopper");
    }

    public Item minecartWithHopper() {
        return Item.of("minecraft:hopper_minecart");
    }

    public Item hornCoral() {
        return Item.of("minecraft:horn_coral");
    }

    public Item hornCoralBlock() {
        return Item.of("minecraft:horn_coral_block");
    }

    public Item hornCoralFan() {
        return Item.of("minecraft:horn_coral_fan");
    }

    public Item horseSpawnEgg() {
        return Item.of("minecraft:horse_spawn_egg");
    }

    public Item huskSpawnEgg() {
        return Item.of("minecraft:husk_spawn_egg");
    }

    public Item ice() {
        return Item.of("minecraft:ice");
    }

    public Item stoneMonsterEgg() {
        return Item.of("minecraft:infested_stone");
    }

    public Item inkSac() {
        return Item.of("minecraft:ink_sac");
    }

    public Item ironAxe() {
        return Item.of("minecraft:iron_axe");
    }

    public Item ironBars() {
        return Item.of("minecraft:iron_bars");
    }

    public Item blockOfIron() {
        return Item.of("minecraft:iron_block");
    }

    public Item ironBoots() {
        return Item.of("minecraft:iron_boots");
    }

    public Item ironChestplate() {
        return Item.of("minecraft:iron_chestplate");
    }

    public Item ironDoor() {
        return Item.of("minecraft:iron_door");
    }

    public Item ironHelmet() {
        return Item.of("minecraft:iron_helmet");
    }

    public Item ironHoe() {
        return Item.of("minecraft:iron_hoe");
    }

    public Item ironHorseArmor() {
        return Item.of("minecraft:iron_horse_armor");
    }

    public Item ironIngot() {
        return Item.of("minecraft:iron_ingot");
    }

    public Item ironLeggings() {
        return Item.of("minecraft:iron_leggings");
    }

    public Item ironNugget() {
        return Item.of("minecraft:iron_nugget");
    }

    public Item ironOre() {
        return Item.of("minecraft:iron_ore");
    }

    public Item ironPickaxe() {
        return Item.of("minecraft:iron_pickaxe");
    }

    public Item ironShovel() {
        return Item.of("minecraft:iron_shovel");
    }

    public Item ironSword() {
        return Item.of("minecraft:iron_sword");
    }

    public Item ironTrapdoor() {
        return Item.of("minecraft:iron_trapdoor");
    }

    public Item itemFrame() {
        return Item.of("minecraft:item_frame");
    }

    public Item jackOLantern() {
        return Item.of("minecraft:jack_o_lantern");
    }

    public Item jukebox() {
        return Item.of("minecraft:jukebox");
    }

    public Item jungleBoat() {
        return Item.of("minecraft:jungle_boat");
    }

    public Item jungleButton() {
        return Item.of("minecraft:jungle_button");
    }

    public Item jungleDoor() {
        return Item.of("minecraft:jungle_door");
    }

    public Item jungleFence() {
        return Item.of("minecraft:jungle_fence");
    }

    public Item jungleFenceGate() {
        return Item.of("minecraft:jungle_fence_gate");
    }

    public Item junglePressurePlate() {
        return Item.of("minecraft:jungle_pressure_plate");
    }

    public Item jungleSign() {
        return Item.of("minecraft:jungle_sign");
    }

    public Item jungleWoodStairs() {
        return Item.of("minecraft:jungle_stairs");
    }

    public Item jungleTrapdoor() {
        return Item.of("minecraft:jungle_trapdoor");
    }

    public Item jungleWoodWithBark() {
        return Item.of("minecraft:jungle_wood");
    }

    public Item kelp() {
        return Item.of("minecraft:kelp");
    }

    public Item ladder() {
        return Item.of("minecraft:ladder");
    }

    public Item lantern() {
        return Item.of("minecraft:lantern");
    }

    public Item lapisLazuliBlock() {
        return Item.of("minecraft:lapis_block");
    }

    public Item lapisLazuli() {
        return Item.of("minecraft:lapis_lazuli");
    }

    public Item lapisLazuliOre() {
        return Item.of("minecraft:lapis_ore");
    }

    public Item lava() {
        return Item.of("minecraft:lava");
    }

    public Item lavaBucket() {
        return Item.of("minecraft:lava_bucket");
    }

    public Item lead() {
        return Item.of("minecraft:lead");
    }

    public Item leather() {
        return Item.of("minecraft:leather");
    }

    public Item leatherBoots() {
        return Item.of("minecraft:leather_boots");
    }

    public Item leatherTunic() {
        return Item.of("minecraft:leather_chestplate");
    }

    public Item leatherCap() {
        return Item.of("minecraft:leather_helmet");
    }

    public Item leatherHorseArmor() {
        return Item.of("minecraft:leather_horse_armor");
    }

    public Item leatherPants() {
        return Item.of("minecraft:leather_leggings");
    }

    public Item lecturn() {
        return Item.of("minecraft:lectern");
    }

    public Item lever() {
        return Item.of("minecraft:lever");
    }

    public Item lightBlueGlazedTerracotta() {
        return Item.of("minecraft:light_blue_glazed_terracotta");
    }

    public Item lightBlueShulkerBox() {
        return Item.of("minecraft:light_blue_shulker_box");
    }

    public Item lightGrayGlazedTerracotta() {
        return Item.of("minecraft:light_gray_glazed_terracotta");
    }

    public Item lightGrayShulkerBox() {
        return Item.of("minecraft:light_gray_shulker_box");
    }

    public Item lightWeightedPressurePlate() {
        return Item.of("minecraft:light_weighted_pressure_plate");
    }

    public Item lilyOfTheValley() {
        return Item.of("minecraft:lily_of_the_valley");
    }

    public Item lilyPad() {
        return Item.of("minecraft:lily_pad");
    }

    public Item limeGlazedTerracotta() {
        return Item.of("minecraft:lime_glazed_terracotta");
    }

    public Item limeShulkerBox() {
        return Item.of("minecraft:lime_shulker_box");
    }

    public Item lingeringPotion() {
        return Item.of("minecraft:lingering_potion");
    }

    public Item llamaSpawnEgg() {
        return Item.of("minecraft:llama_spawn_egg");
    }

    public Item lodestone() {
        return Item.of("minecraft:lodestone");
    }

    public Item loom() {
        return Item.of("minecraft:loom");
    }

    public Item magentaGlazedTerracotta() {
        return Item.of("minecraft:magenta_glazed_terracotta");
    }

    public Item magentaShulkerBox() {
        return Item.of("minecraft:magenta_shulker_box");
    }

    public Item magmaBlock() {
        return Item.of("minecraft:magma_block");
    }

    public Item magmaCream() {
        return Item.of("minecraft:magma_cream");
    }

    public Item magmaCubeSpawnEgg() {
        return Item.of("minecraft:magma_cube_spawn_egg");
    }

    public Item map() {
        return Item.of("minecraft:map");
    }

    public Item blockOfMelon() {
        return Item.of("minecraft:melon");
    }

    public Item melonSeeds() {
        return Item.of("minecraft:melon_seeds");
    }

    public Item melonSlice() {
        return Item.of("minecraft:melon_slice");
    }

    public Item milk() {
        return Item.of("minecraft:milk_bucket");
    }

    public Item minecart() {
        return Item.of("minecraft:minecart");
    }

    public Item thingBannerPattern() {
        return Item.of("minecraft:mojang_banner_pattern");
    }

    public Item mooshroomSpawnEgg() {
        return Item.of("minecraft:mooshroom_spawn_egg");
    }

    public Item mossyCobblestone() {
        return Item.of("minecraft:mossy_cobblestone");
    }

    public Item mossyCobblestoneSlab() {
        return Item.of("minecraft:mossy_cobblestone_slab");
    }

    public Item mossyCobblestoneStairs() {
        return Item.of("minecraft:mossy_cobblestone_stairs");
    }

    public Item mossyStoneBrickSlab() {
        return Item.of("minecraft:mossy_stone_brick_slab");
    }

    public Item mossyStoneBrickStairs() {
        return Item.of("minecraft:mossy_stone_brick_stairs");
    }

    public Item mossyStoneBrickWall() {
        return Item.of("minecraft:mossy_stone_brick_wall");
    }

    public Item muleSpawnEgg() {
        return Item.of("minecraft:mule_spawn_egg");
    }

    public Item mushroomStem() {
        return Item.of("minecraft:mushroom_stem");
    }

    public Item mushroomStew() {
        return Item.of("minecraft:mushroom_stew");
    }

    public Item musicDiscC418_11() {
        return Item.of("minecraft:music_disc_11");
    }

    public Item musicDiscC418_13() {
        return Item.of("minecraft:music_disc_13");
    }

    public Item musicDiscC418Blocks() {
        return Item.of("minecraft:music_disc_blocks");
    }

    public Item musicDiscC418Cat() {
        return Item.of("minecraft:music_disc_cat");
    }

    public Item musicDiscC418Chirp() {
        return Item.of("minecraft:music_disc_chirp");
    }

    public Item musicDiscC418Far() {
        return Item.of("minecraft:music_disc_far");
    }

    public Item musicDiscC418Mall() {
        return Item.of("minecraft:music_disc_mall");
    }

    public Item musicDiscC418Mellohi() {
        return Item.of("minecraft:music_disc_mellohi");
    }

    public Item musicDiscPigstep() {
        return Item.of("minecraft:music_disc_pigstep");
    }

    public Item musicDiscC418Stal() {
        return Item.of("minecraft:music_disc_stal");
    }

    public Item musicDiscC418Strad() {
        return Item.of("minecraft:music_disc_strad");
    }

    public Item musicDiscC418Wait() {
        return Item.of("minecraft:music_disc_wait");
    }

    public Item musicDiscC418Ward() {
        return Item.of("minecraft:music_disc_ward");
    }

    public Item rawMutton() {
        return Item.of("minecraft:mutton");
    }

    public Item mycelium() {
        return Item.of("minecraft:mycelium");
    }

    public Item nameTag() {
        return Item.of("minecraft:name_tag");
    }

    public Item nautilusShell() {
        return Item.of("minecraft:nautilus_shell");
    }

    public Item netherBrick() {
        return Item.of("minecraft:nether_brick");
    }

    public Item netherBrickFence() {
        return Item.of("minecraft:nether_brick_fence");
    }

    public Item netherBrickStairs() {
        return Item.of("minecraft:nether_brick_stairs");
    }

    public Item netherBrickWall() {
        return Item.of("minecraft:nether_brick_wall");
    }

    public Item blockOfNetherBricks() {
        return Item.of("minecraft:nether_bricks");
    }

    public Item netherGoldOre() {
        return Item.of("minecraft:nether_gold_ore");
    }

    public Item netherQuartzOre() {
        return Item.of("minecraft:nether_quartz_ore");
    }

    public Item netherSprouts() {
        return Item.of("minecraft:nether_sprouts");
    }

    public Item netherStar() {
        return Item.of("minecraft:nether_star");
    }

    public Item netherWart() {
        return Item.of("minecraft:nether_wart");
    }

    public Item netherWartBlock() {
        return Item.of("minecraft:nether_wart_block");
    }

    public Item netheriteAxe() {
        return Item.of("minecraft:netherite_axe");
    }

    public Item blockOfNetherite() {
        return Item.of("minecraft:netherite_block");
    }

    public Item netheriteBoots() {
        return Item.of("minecraft:netherite_boots");
    }

    public Item netheriteChestplate() {
        return Item.of("minecraft:netherite_chestplate");
    }

    public Item netheriteHelmet() {
        return Item.of("minecraft:netherite_helmet");
    }

    public Item netheriteHoe() {
        return Item.of("minecraft:netherite_hoe");
    }

    public Item netheriteIngot() {
        return Item.of("minecraft:netherite_ingot");
    }

    public Item netheriteLeggings() {
        return Item.of("minecraft:netherite_leggings");
    }

    public Item netheritePickaxe() {
        return Item.of("minecraft:netherite_pickaxe");
    }

    public Item netheriteScrap() {
        return Item.of("minecraft:netherite_scrap");
    }

    public Item netheriteShovel() {
        return Item.of("minecraft:netherite_shovel");
    }

    public Item netheriteSword() {
        return Item.of("minecraft:netherite_sword");
    }

    public Item netherrack() {
        return Item.of("minecraft:netherrack");
    }

    public Item quartzBricks() {
        return Item.of("minecraft:quartz_bricks");
    }

    public Item noteBlock() {
        return Item.of("minecraft:note_block");
    }

    public Item oakBoat() {
        return Item.of("minecraft:oak_boat");
    }

    public Item oakButton() {
        return Item.of("minecraft:oak_button");
    }

    public Item oakDoor() {
        return Item.of("minecraft:oak_door");
    }

    public Item oakFence() {
        return Item.of("minecraft:oak_fence");
    }

    public Item oakFenceGate() {
        return Item.of("minecraft:oak_fence_gate");
    }

    public Item oakLeaves() {
        return Item.of("minecraft:oak_leaves");
    }

    public Item oakLog() {
        return Item.of("minecraft:oak_log");
    }

    public Item oakPlanks() {
        return Item.of("minecraft:oak_planks");
    }

    public Item oakPressurePlate() {
        return Item.of("minecraft:oak_pressure_plate");
    }

    public Item oakSapling() {
        return Item.of("minecraft:oak_sapling");
    }

    public Item oakSign() {
        return Item.of("minecraft:oak_sign");
    }

    public Item oakWoodStairs() {
        return Item.of("minecraft:oak_stairs");
    }

    public Item oakTrapdoor() {
        return Item.of("minecraft:oak_trapdoor");
    }

    public Item oakWoodWithBark() {
        return Item.of("minecraft:oak_wood");
    }

    public Item observer() {
        return Item.of("minecraft:observer");
    }

    public Item obsidian() {
        return Item.of("minecraft:obsidian");
    }

    public Item ocelotSpawnEgg() {
        return Item.of("minecraft:ocelot_spawn_egg");
    }

    public Item orangeGlazedTerracotta() {
        return Item.of("minecraft:orange_glazed_terracotta");
    }

    public Item orangeShulkerBox() {
        return Item.of("minecraft:orange_shulker_box");
    }

    public Item packedIce() {
        return Item.of("minecraft:packed_ice");
    }

    public Item painting() {
        return Item.of("minecraft:painting");
    }

    public Item pandaSpawnEgg() {
        return Item.of("minecraft:panda_spawn_egg");
    }

    public Item paper() {
        return Item.of("minecraft:paper");
    }

    public Item parrotSpawnEgg() {
        return Item.of("minecraft:parrot_spawn_egg");
    }

    public Item petrifiedOakSlab() {
        return Item.of("minecraft:petrified_oak_slab");
    }

    public Item phantomMembrane() {
        return Item.of("minecraft:phantom_membrane");
    }

    public Item phantomSpawnEgg() {
        return Item.of("minecraft:phantom_spawn_egg");
    }

    public Item pigSpawnEgg() {
        return Item.of("minecraft:pig_spawn_egg");
    }

    public Item snoutBannerPattern() {
        return Item.of("minecraft:piglin_banner_pattern");
    }

    public Item spawnPiglinBrute() {
        return Item.of("minecraft:piglin_brute_spawn_egg");
    }

    public Item piglinSpawnEgg() {
        return Item.of("minecraft:piglin_spawn_egg");
    }

    public Item pillagerSpawnEgg() {
        return Item.of("minecraft:pillager_spawn_egg");
    }

    public Item pinkGlazedTerracotta() {
        return Item.of("minecraft:pink_glazed_terracotta");
    }

    public Item pinkShulkerBox() {
        return Item.of("minecraft:pink_shulker_box");
    }

    public Item piston() {
        return Item.of("minecraft:piston");
    }

    public Item poisonousPotato() {
        return Item.of("minecraft:poisonous_potato");
    }

    public Item polarBearSpawnEgg() {
        return Item.of("minecraft:polar_bear_spawn_egg");
    }

    public Item polishedAndesiteSlab() {
        return Item.of("minecraft:polished_andesite_slab");
    }

    public Item polishedAndesiteStairs() {
        return Item.of("minecraft:polished_andesite_stairs");
    }

    public Item polishedBasalt() {
        return Item.of("minecraft:polished_basalt");
    }

    public Item polishedBlackstone() {
        return Item.of("minecraft:polished_blackstone");
    }

    public Item polishedBlackstoneBrickSlab() {
        return Item.of("minecraft:polished_blackstone_brick_slab");
    }

    public Item polishedBlackstoneBrickStairs() {
        return Item.of("minecraft:polished_blackstone_brick_stairs");
    }

    public Item polishedBlackstoneBrickWall() {
        return Item.of("minecraft:polished_blackstone_brick_wall");
    }

    public Item polishedBlackstoneBricks() {
        return Item.of("minecraft:polished_blackstone_bricks");
    }

    public Item polishedBlackstoneButton() {
        return Item.of("minecraft:polished_blackstone_button");
    }

    public Item polishedBlackstonePressurePlate() {
        return Item.of("minecraft:polished_blackstone_pressure_plate");
    }

    public Item polishedBlackstoneSlab() {
        return Item.of("minecraft:polished_blackstone_slab");
    }

    public Item polishedBlackstoneStairs() {
        return Item.of("minecraft:polished_blackstone_stairs");
    }

    public Item polishedBlackstoneWall() {
        return Item.of("minecraft:polished_blackstone_wall");
    }

    public Item polishedDioriteSlab() {
        return Item.of("minecraft:polished_diorite_slab");
    }

    public Item polishedDioriteStairs() {
        return Item.of("minecraft:polished_diorite_stairs");
    }

    public Item polishedGraniteSlab() {
        return Item.of("minecraft:polished_granite_slab");
    }

    public Item polishedGraniteStairs() {
        return Item.of("minecraft:polished_granite_stairs");
    }

    public Item poppedChorusFruit() {
        return Item.of("minecraft:popped_chorus_fruit");
    }

    public Item poppy() {
        return Item.of("minecraft:poppy");
    }

    public Item rawPorkchop() {
        return Item.of("minecraft:porkchop");
    }

    public Item potato() {
        return Item.of("minecraft:potato");
    }

    public Item potion() {
        return Item.of("minecraft:potion");
    }

    public Item waterBottle() {
        return Item.of("minecraft:potion");
    }

    public Item poweredRails() {
        return Item.of("minecraft:powered_rail");
    }

    public Item prismarine() {
        return Item.of("minecraft:prismarine");
    }

    public Item prismarineBrickSlab() {
        return Item.of("minecraft:prismarine_brick_slab");
    }

    public Item prismarineBricksStairs() {
        return Item.of("minecraft:prismarine_brick_stairs");
    }

    public Item prismarineCrystals() {
        return Item.of("minecraft:prismarine_crystals");
    }

    public Item prismarineShard() {
        return Item.of("minecraft:prismarine_shard");
    }

    public Item prismarineSlab() {
        return Item.of("minecraft:prismarine_slab");
    }

    public Item prismarineStairs() {
        return Item.of("minecraft:prismarine_stairs");
    }

    public Item prismarineWall() {
        return Item.of("minecraft:prismarine_wall");
    }

    public Item bucketOfPufferfish() {
        return Item.of("minecraft:pufferfish_bucket");
    }

    public Item pufferfishSpawnEgg() {
        return Item.of("minecraft:pufferfish_spawn_egg");
    }

    public Item pumpkin() {
        return Item.of("minecraft:pumpkin");
    }

    public Item pumpkinPie() {
        return Item.of("minecraft:pumpkin_pie");
    }

    public Item pumpkinSeeds() {
        return Item.of("minecraft:pumpkin_seeds");
    }

    public Item purpleGlazedTerracotta() {
        return Item.of("minecraft:purple_glazed_terracotta");
    }

    public Item purpleShulkerBox() {
        return Item.of("minecraft:purple_shulker_box");
    }

    public Item purpurBlock() {
        return Item.of("minecraft:purpur_block");
    }

    public Item purpurPillar() {
        return Item.of("minecraft:purpur_pillar");
    }

    public Item purpurSlab() {
        return Item.of("minecraft:purpur_slab");
    }

    public Item purpurStairs() {
        return Item.of("minecraft:purpur_stairs");
    }

    public Item netherQuartz() {
        return Item.of("minecraft:quartz");
    }

    public Item blockOfQuartz() {
        return Item.of("minecraft:quartz_block");
    }

    public Item quartzStairs() {
        return Item.of("minecraft:quartz_stairs");
    }

    public Item rawRabbit() {
        return Item.of("minecraft:rabbit");
    }

    public Item rabbitsFoot() {
        return Item.of("minecraft:rabbit_foot");
    }

    public Item rabbitHide() {
        return Item.of("minecraft:rabbit_hide");
    }

    public Item rabbitSpawnEgg() {
        return Item.of("minecraft:rabbit_spawn_egg");
    }

    public Item rabbitStew() {
        return Item.of("minecraft:rabbit_stew");
    }

    public Item rails() {
        return Item.of("minecraft:rail");
    }

    public Item ravagerSpawnEgg() {
        return Item.of("minecraft:ravager_spawn_egg");
    }

    public Item redGlazedTerracotta() {
        return Item.of("minecraft:red_glazed_terracotta");
    }

    public Item redMushroom() {
        return Item.of("minecraft:red_mushroom");
    }

    public Item redMushroomBlock() {
        return Item.of("minecraft:red_mushroom_block");
    }

    public Item redNetherBrickSlab() {
        return Item.of("minecraft:red_nether_brick_slab");
    }

    public Item redNetherBrickStairs() {
        return Item.of("minecraft:red_nether_brick_stairs");
    }

    public Item redNetherBrickWall() {
        return Item.of("minecraft:red_nether_brick_wall");
    }

    public Item redNetherBrick() {
        return Item.of("minecraft:red_nether_bricks");
    }

    public Item redSandstone() {
        return Item.of("minecraft:red_sandstone");
    }

    public Item redSandstoneStairs() {
        return Item.of("minecraft:red_sandstone_stairs");
    }

    public Item redSandstoneWall() {
        return Item.of("minecraft:red_sandstone_wall");
    }

    public Item redShulkerBox() {
        return Item.of("minecraft:red_shulker_box");
    }

    public Item redstoneDust() {
        return Item.of("minecraft:redstone");
    }

    public Item blockOfRedstone() {
        return Item.of("minecraft:redstone_block");
    }

    public Item redstoneLamp() {
        return Item.of("minecraft:redstone_lamp");
    }

    public Item redstoneOre() {
        return Item.of("minecraft:redstone_ore");
    }

    public Item redstoneTorch() {
        return Item.of("minecraft:redstone_torch");
    }

    public Item redstoneRepeater() {
        return Item.of("minecraft:repeater");
    }

    public Item repeatingCommandBlock() {
        return Item.of("minecraft:repeating_command_block");
    }

    public Item respawnAnchor() {
        return Item.of("minecraft:respawn_anchor");
    }

    public Item rottenFlesh() {
        return Item.of("minecraft:rotten_flesh");
    }

    public Item saddle() {
        return Item.of("minecraft:saddle");
    }

    public Item bucketOfSalmon() {
        return Item.of("minecraft:salmon_bucket");
    }

    public Item salmonSpawnEgg() {
        return Item.of("minecraft:salmon_spawn_egg");
    }

    public Item sand() {
        return Item.of("minecraft:sand");
    }

    public Item sandstone() {
        return Item.of("minecraft:sandstone");
    }

    public Item sandstoneStairs() {
        return Item.of("minecraft:sandstone_stairs");
    }

    public Item sandstoneWall() {
        return Item.of("minecraft:sandstone_wall");
    }

    public Item scaffolding() {
        return Item.of("minecraft:scaffolding");
    }

    public Item scute() {
        return Item.of("minecraft:scute");
    }

    public Item seaLantern() {
        return Item.of("minecraft:sea_lantern");
    }

    public Item seaPickle() {
        return Item.of("minecraft:sea_pickle");
    }

    public Item seagrass() {
        return Item.of("minecraft:seagrass");
    }

    public Item shears() {
        return Item.of("minecraft:shears");
    }

    public Item sheepSpawnEgg() {
        return Item.of("minecraft:sheep_spawn_egg");
    }

    public Item shield() {
        return Item.of("minecraft:shield");
    }

    public Item shroomlight() {
        return Item.of("minecraft:shroomlight");
    }

    public Item shulkerBox() {
        return Item.of("minecraft:shulker_box");
    }

    public Item shulkerShell() {
        return Item.of("minecraft:shulker_shell");
    }

    public Item shulkerSpawnEgg() {
        return Item.of("minecraft:shulker_spawn_egg");
    }

    public Item silverfishSpawnEgg() {
        return Item.of("minecraft:silverfish_spawn_egg");
    }

    public Item skeletonHorseSpawnEgg() {
        return Item.of("minecraft:skeleton_horse_spawn_egg");
    }

    public Item skeletonSpawnEgg() {
        return Item.of("minecraft:skeleton_spawn_egg");
    }

    public Item skullChargeBannerPattern() {
        return Item.of("minecraft:skull_banner_pattern");
    }

    public Item slimeball() {
        return Item.of("minecraft:slime_ball");
    }

    public Item slimeBlock() {
        return Item.of("minecraft:slime_block");
    }

    public Item slimeSpawnEgg() {
        return Item.of("minecraft:slime_spawn_egg");
    }

    public Item smithingTable() {
        return Item.of("minecraft:smithing_table");
    }

    public Item smoker() {
        return Item.of("minecraft:smoker");
    }

    public Item smoothQuartz() {
        return Item.of("minecraft:smooth_quartz");
    }

    public Item smoothQuartzSlab() {
        return Item.of("minecraft:smooth_quartz_slab");
    }

    public Item smoothQuartzStairs() {
        return Item.of("minecraft:smooth_quartz_stairs");
    }

    public Item smoothRedSandstoneSlab() {
        return Item.of("minecraft:smooth_red_sandstone_slab");
    }

    public Item smoothRedSandstoneStairs() {
        return Item.of("minecraft:smooth_red_sandstone_stairs");
    }

    public Item smoothSandstoneSlab() {
        return Item.of("minecraft:smooth_sandstone_slab");
    }

    public Item smoothSandstoneStairs() {
        return Item.of("minecraft:smooth_sandstone_stairs");
    }

    public Item smoothStone() {
        return Item.of("minecraft:smooth_stone");
    }

    public Item smoothStoneSlab() {
        return Item.of("minecraft:smooth_stone_slab");
    }

    public Item snow() {
        return Item.of("minecraft:snow");
    }

    public Item snowBlock() {
        return Item.of("minecraft:snow_block");
    }

    public Item snowball() {
        return Item.of("minecraft:snowball");
    }

    public Item soulCampfire() {
        return Item.of("minecraft:soul_campfire");
    }

    public Item soulLantern() {
        return Item.of("minecraft:soul_lantern");
    }

    public Item soulSand() {
        return Item.of("minecraft:soul_sand");
    }

    public Item soulSoil() {
        return Item.of("minecraft:soul_soil");
    }

    public Item soulTorch() {
        return Item.of("minecraft:soul_torch");
    }

    public Item monsterSpawner() {
        return Item.of("minecraft:spawner");
    }

    public Item spectralArrow() {
        return Item.of("minecraft:spectral_arrow");
    }

    public Item spiderEye() {
        return Item.of("minecraft:spider_eye");
    }

    public Item spiderSpawnEgg() {
        return Item.of("minecraft:spider_spawn_egg");
    }

    public Item splashPotion() {
        return Item.of("minecraft:splash_potion");
    }

    public Item sponge() {
        return Item.of("minecraft:sponge");
    }

    public Item spruceBoat() {
        return Item.of("minecraft:spruce_boat");
    }

    public Item spruceButton() {
        return Item.of("minecraft:spruce_button");
    }

    public Item spruceDoor() {
        return Item.of("minecraft:spruce_door");
    }

    public Item spruceFence() {
        return Item.of("minecraft:spruce_fence");
    }

    public Item spruceFenceGate() {
        return Item.of("minecraft:spruce_fence_gate");
    }

    public Item sprucePressurePlate() {
        return Item.of("minecraft:spruce_pressure_plate");
    }

    public Item spruceSign() {
        return Item.of("minecraft:spruce_sign");
    }

    public Item spruceWoodStairs() {
        return Item.of("minecraft:spruce_stairs");
    }

    public Item spruceTrapdoor() {
        return Item.of("minecraft:spruce_trapdoor");
    }

    public Item spruceWoodWithBark() {
        return Item.of("minecraft:spruce_wood");
    }

    public Item squidSpawnEgg() {
        return Item.of("minecraft:squid_spawn_egg");
    }

    public Item stick() {
        return Item.of("minecraft:stick");
    }

    public Item stickyPiston() {
        return Item.of("minecraft:sticky_piston");
    }

    public Item stone() {
        return Item.of("minecraft:stone");
    }

    public Item stoneAxe() {
        return Item.of("minecraft:stone_axe");
    }

    public Item stoneBrickStairs() {
        return Item.of("minecraft:stone_brick_stairs");
    }

    public Item stoneBrickWall() {
        return Item.of("minecraft:stone_brick_wall");
    }

    public Item stoneBricks() {
        return Item.of("minecraft:stone_bricks");
    }

    public Item stoneButton() {
        return Item.of("minecraft:stone_button");
    }

    public Item stoneHoe() {
        return Item.of("minecraft:stone_hoe");
    }

    public Item stonePickaxe() {
        return Item.of("minecraft:stone_pickaxe");
    }

    public Item stonePressurePlate() {
        return Item.of("minecraft:stone_pressure_plate");
    }

    public Item stoneShovel() {
        return Item.of("minecraft:stone_shovel");
    }

    public Item stoneSlab() {
        return Item.of("minecraft:stone_slab");
    }

    public Item stoneStairs() {
        return Item.of("minecraft:stone_stairs");
    }

    public Item stoneSword() {
        return Item.of("minecraft:stone_sword");
    }

    public Item stonecutter() {
        return Item.of("minecraft:stonecutter");
    }

    public Item straySpawnEgg() {
        return Item.of("minecraft:stray_spawn_egg");
    }

    public Item striderSpawnEgg() {
        return Item.of("minecraft:strider_spawn_egg");
    }

    public Item string() {
        return Item.of("minecraft:string");
    }

    public Item strippedAcaciaLog() {
        return Item.of("minecraft:stripped_acacia_log");
    }

    public Item strippedAcaciaWood() {
        return Item.of("minecraft:stripped_acacia_wood");
    }

    public Item strippedBirchWood() {
        return Item.of("minecraft:stripped_birch_wood");
    }

    public Item strippedCrimsonHyphae() {
        return Item.of("minecraft:stripped_crimson_hyphae");
    }

    public Item strippedCrimsonStem() {
        return Item.of("minecraft:stripped_crimson_stem");
    }

    public Item strippedDarkOakLog() {
        return Item.of("minecraft:stripped_dark_oak_log");
    }

    public Item strippedDarkOakWood() {
        return Item.of("minecraft:stripped_dark_oak_wood");
    }

    public Item strippedJungleWood() {
        return Item.of("minecraft:stripped_jungle_wood");
    }

    public Item strippedOakLog() {
        return Item.of("minecraft:stripped_oak_log");
    }

    public Item strippedOakWood() {
        return Item.of("minecraft:stripped_oak_wood");
    }

    public Item strippedSpruceLog() {
        return Item.of("minecraft:stripped_spruce_log");
    }

    public Item strippedSpruceWood() {
        return Item.of("minecraft:stripped_spruce_wood");
    }

    public Item strippedWarpedHyphae() {
        return Item.of("minecraft:stripped_warped_hyphae");
    }

    public Item strippedWarpedStem() {
        return Item.of("minecraft:stripped_warped_stem");
    }

    public Item strippedBirchLog() {
        return Item.of("minecraft:stripped_birch_log");
    }

    public Item strippedJungleLog() {
        return Item.of("minecraft:stripped_jungle_log");
    }

    public Item structureBlock() {
        return Item.of("minecraft:structure_block");
    }

    public Item structureVoid() {
        return Item.of("minecraft:structure_void");
    }

    public Item sugar() {
        return Item.of("minecraft:sugar");
    }

    public Item sugarCanes() {
        return Item.of("minecraft:sugar_cane");
    }

    public Item sunflower() {
        return Item.of("minecraft:sunflower");
    }

    public Item suspiciousStew() {
        return Item.of("minecraft:suspicious_stew");
    }

    public Item sweetBerries() {
        return Item.of("minecraft:sweet_berries");
    }

    public Item target() {
        return Item.of("minecraft:target");
    }

    public Item terracotta() {
        return Item.of("minecraft:terracotta");
    }

    public Item tippedArrow() {
        return Item.of("minecraft:tipped_arrow");
    }

    public Item tnt() {
        return Item.of("minecraft:tnt");
    }

    public Item minecartWithTnt() {
        return Item.of("minecraft:tnt_minecart");
    }

    public Item torch() {
        return Item.of("minecraft:torch");
    }

    public Item totemOfUndying() {
        return Item.of("minecraft:totem_of_undying");
    }

    public Item traderLlamaSpawnEgg() {
        return Item.of("minecraft:trader_llama_spawn_egg");
    }

    public Item trappedChest() {
        return Item.of("minecraft:trapped_chest");
    }

    public Item trident() {
        return Item.of("minecraft:trident");
    }

    public Item tripwireHook() {
        return Item.of("minecraft:tripwire_hook");
    }

    public Item bucketOfTropicalFish() {
        return Item.of("minecraft:tropical_fish_bucket");
    }

    public Item tropicalFishSpawnEgg() {
        return Item.of("minecraft:tropical_fish_spawn_egg");
    }

    public Item tubeCoral() {
        return Item.of("minecraft:tube_coral");
    }

    public Item tubeCoralBlock() {
        return Item.of("minecraft:tube_coral_block");
    }

    public Item tubeCoralFan() {
        return Item.of("minecraft:tube_coral_fan");
    }

    public Item turtleEgg() {
        return Item.of("minecraft:turtle_egg");
    }

    public Item turtleShell() {
        return Item.of("minecraft:turtle_helmet");
    }

    public Item turtleSpawnEgg() {
        return Item.of("minecraft:turtle_spawn_egg");
    }

    public Item twistingVines() {
        return Item.of("minecraft:twisting_vines");
    }

    public Item vexSpawnEgg() {
        return Item.of("minecraft:vex_spawn_egg");
    }

    public Item villagerSpawnEgg() {
        return Item.of("minecraft:villager_spawn_egg");
    }

    public Item vindicatorSpawnEgg() {
        return Item.of("minecraft:vindicator_spawn_egg");
    }

    public Item vines() {
        return Item.of("minecraft:vine");
    }

    public Item wanderingTraderSpawnEgg() {
        return Item.of("minecraft:wandering_trader_spawn_egg");
    }

    public Item warpedButton() {
        return Item.of("minecraft:warped_button");
    }

    public Item warpedDoor() {
        return Item.of("minecraft:warped_door");
    }

    public Item warpedFence() {
        return Item.of("minecraft:warped_fence");
    }

    public Item warpedFenceGate() {
        return Item.of("minecraft:warped_fence_gate");
    }

    public Item warpedFungus() {
        return Item.of("minecraft:warped_fungus");
    }

    public Item warpedFungusOnAStick() {
        return Item.of("minecraft:warped_fungus_on_a_stick");
    }

    public Item warpedHyphae() {
        return Item.of("minecraft:warped_hyphae");
    }

    public Item warpedNylium() {
        return Item.of("minecraft:warped_nylium");
    }

    public Item warpedPlanks() {
        return Item.of("minecraft:warped_planks");
    }

    public Item warpedPressurePlate() {
        return Item.of("minecraft:warped_pressure_plate");
    }

    public Item warpedRoots() {
        return Item.of("minecraft:warped_roots");
    }

    public Item warpedSign() {
        return Item.of("minecraft:warped_sign");
    }

    public Item warpedSlab() {
        return Item.of("minecraft:warped_slab");
    }

    public Item warpedStairs() {
        return Item.of("minecraft:warped_stairs");
    }

    public Item warpedStem() {
        return Item.of("minecraft:warped_stem");
    }

    public Item warpedTrapdoor() {
        return Item.of("minecraft:warped_trapdoor");
    }

    public Item warpedWartBlock() {
        return Item.of("minecraft:warped_wart_block");
    }

    public Item water() {
        return Item.of("minecraft:water");
    }

    public Item waterBucket() {
        return Item.of("minecraft:water_bucket");
    }

    public Item weepingVines() {
        return Item.of("minecraft:weeping_vines");
    }

    public Item wheat() {
        return Item.of("minecraft:wheat");
    }

    public Item seeds() {
        return Item.of("minecraft:wheat_seeds");
    }

    public Item whiteGlazedTerracotta() {
        return Item.of("minecraft:white_glazed_terracotta");
    }

    public Item whiteShulkerBox() {
        return Item.of("minecraft:white_shulker_box");
    }

    public Item whiteTerracotta() {
        return Item.of("minecraft:white_terracotta");
    }

    public Item whiteWool() {
        return Item.of("minecraft:white_wool");
    }

    public Item witchSpawnEgg() {
        return Item.of("minecraft:witch_spawn_egg");
    }

    public Item witherRose() {
        return Item.of("minecraft:wither_rose");
    }

    public Item witherSkeletonSpawnEgg() {
        return Item.of("minecraft:wither_skeleton_spawn_egg");
    }

    public Item wolfSpawnEgg() {
        return Item.of("minecraft:wolf_spawn_egg");
    }

    public Item woodenAxe() {
        return Item.of("minecraft:wooden_axe");
    }

    public Item woodenHoe() {
        return Item.of("minecraft:wooden_hoe");
    }

    public Item woodenPickaxe() {
        return Item.of("minecraft:wooden_pickaxe");
    }

    public Item woodenShovel() {
        return Item.of("minecraft:wooden_shovel");
    }

    public Item woodenSword() {
        return Item.of("minecraft:wooden_sword");
    }

    public Item bookAndQuill() {
        return Item.of("minecraft:writable_book");
    }

    public Item writtenBook() {
        return Item.of("minecraft:written_book");
    }

    public Item yellowGlazedTerracotta() {
        return Item.of("minecraft:yellow_glazed_terracotta");
    }

    public Item yellowShulkerBox() {
        return Item.of("minecraft:yellow_shulker_box");
    }

    public Item zoglinSpawnEgg() {
        return Item.of("minecraft:zoglin_spawn_egg");
    }

    public Item zombieHorseSpawnEgg() {
        return Item.of("minecraft:zombie_horse_spawn_egg");
    }

    public Item zombieSpawnEgg() {
        return Item.of("minecraft:zombie_spawn_egg");
    }

    public Item zombieVillagerSpawnEgg() {
        return Item.of("minecraft:zombie_villager_spawn_egg");
    }

    public Item zombifiedPiglinSpawnEgg() {
        return Item.of("minecraft:zombified_piglin_spawn_egg");
    }

    public abstract Item yellowBed();

    public abstract Item yellowCarpet();

    public abstract Item yellowConcrete();

    public abstract Item yellowConcretePowder();

    public abstract Item yellowDye();

    public abstract Item yellowStainedGlass();

    public abstract Item yellowStainedGlassPane();

    public abstract Item yellowTerracotta();

    public abstract Item yellowWool();

    public abstract Item zombieHead();
}
