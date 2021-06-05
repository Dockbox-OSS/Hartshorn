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

import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.MinecraftVersion;

@SuppressWarnings({ "unused", "OverlyComplexClass", "MagicNumber" })
public class Minecraft112Items extends MinecraftItems {

    // White (0) is skipped as item meta defaults to zero
    private static final int ORANGE = 1;
    private static final int MAGENTA = 2;
    private static final int LIGHT_BLUE = 3;
    private static final int YELLOW = 4;
    private static final int LIME = 5;
    private static final int PINK = 6;
    private static final int GRAY = 7;
    private static final int LIGHT_GRAY = 8;
    private static final int CYAN = 9;
    private static final int PURPLE = 10;
    private static final int BLUE = 11;
    private static final int BROWN = 12;
    private static final int GREEN = 13;
    private static final int RED = 14;
    private static final int BLACK = 15;

    private static final int SPRUCE = 1;
    private static final int BIRCH = 2;
    private static final int JUNGLE = 3;
    private static final int ACACIA = 4;

    private static final int DARK_OAK_1 = 1;
    private static final int DARK_OAK_2 = 5;

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.MC1_12;
    }

    @Override
    public Item getAcaciaLeaves() {
        return Item.of("minecraft:leaves2");
    }

    @Override
    public Item getAcaciaLog() {
        return Item.of("minecraft:log2");
    }

    @Override
    public Item getAcaciaPlanks() {
        return this.getOakPlanks().withMeta(ACACIA);
    }

    @Override
    public Item getAcaciaSapling() {
        return this.getOakSapling().withMeta(ACACIA);
    }

    @Override
    public Item getAcaciaWoodSlab() {
        return this.getOakWoodSlab().withMeta(ACACIA);
    }

    @Override
    public Item getAllium() {
        return this.getPoppy().withMeta(2);
    }

    @Override
    public Item getAndesite() {
        return this.getStone().withMeta(5);
    }

    @Override
    public Item getAzureBluet() {
        return this.getPoppy().withMeta(3);
    }

    @Override
    public Item getBirchLeaves() {
        return this.getOakLeaves().withMeta(BIRCH);
    }

    @Override
    public Item getBirchLog() {
        return this.getOakLog().withMeta(BIRCH);
    }

    @Override
    public Item getBirchPlanks() {
        return this.getOakPlanks().withMeta(BIRCH);
    }

    @Override
    public Item getBirchSapling() {
        return this.getOakSapling().withMeta(BIRCH);
    }

    @Override
    public Item getBirchWoodSlab() {
        return this.getOakWoodSlab().withMeta(BIRCH);
    }

    @Override
    public Item getBlackBanner() {
        return Item.of("minecraft:banner");
    }

    @Override
    public Item getBlackBed() {
        return this.getWhiteBed().withMeta(BLACK);
    }

    @Override
    public Item getBlackCarpet() {
        return this.getWhiteCarpet().withMeta(BLACK);
    }

    @Override
    public Item getBlackConcrete() {
        return this.getWhiteConcrete().withMeta(BLACK);
    }

    @Override
    public Item getBlackConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(BLACK);
    }

    @Override
    public Item getBlackDye() {
        return Item.of("minecraft:dye");
    }

    @Override
    public Item getBlackStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(BLACK);
    }

    @Override
    public Item getBlackStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(BLACK);
    }

    @Override
    public Item getBlackTerracotta() {
        return this.getWhiteTerracotta().withMeta(BLACK);
    }

    @Override
    public Item getBlackWool() {
        return this.getWhiteWool().withMeta(BLACK);
    }

    @Override
    public Item getBlueBanner() {
        return this.getBlackBanner().withMeta(4);
    }

    @Override
    public Item getBlueBed() {
        return this.getWhiteBed().withMeta(BLUE);
    }

    @Override
    public Item getBlueCarpet() {
        return this.getWhiteCarpet().withMeta(BLUE);
    }

    @Override
    public Item getBlueOrchid() {
        return this.getPoppy().withMeta(1);
    }

    @Override
    public Item getBlueStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(BLUE);
    }

    @Override
    public Item getBlueStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(BLUE);
    }

    @Override
    public Item getBlueTerracotta() {
        return this.getWhiteTerracotta().withMeta(BLUE);
    }

    @Override
    public Item getBlueWool() {
        return this.getWhiteWool().withMeta(BLUE);
    }

    @Override
    public Item getBrickSlab() {
        return this.getStoneSlab().withMeta(4);
    }

    @Override
    public Item getBrownBanner() {
        return this.getBlackBanner().withMeta(3);
    }

    @Override
    public Item getBrownBed() {
        return this.getWhiteBed().withMeta(BROWN);
    }

    @Override
    public Item getBrownCarpet() {
        return this.getWhiteCarpet().withMeta(BROWN);
    }

    @Override
    public Item getBrownConcrete() {
        return this.getWhiteConcrete().withMeta(BROWN);
    }

    @Override
    public Item getBrownConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(BROWN);
    }

    @Override
    public Item getBrownDye() {
        return this.getBlackDye().withMeta(3);
    }

    @Override
    public Item getBrownStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(BROWN);
    }

    @Override
    public Item getBrownStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(BROWN);
    }

    @Override
    public Item getBrownTerracotta() {
        return this.getWhiteTerracotta().withMeta(BROWN);
    }

    @Override
    public Item getBrownWool() {
        return this.getWhiteWool().withMeta(BROWN);
    }

    @Override
    public Item getCharcoal() {
        return this.getCoal().withMeta(1);
    }

    @Override
    public Item getSlightlyDamagedAnvil() {
        return this.getAnvil().withMeta(1);
    }

    @Override
    public Item getChiseledRedSandstone() {
        return this.getRedSandstone().withMeta(1);
    }

    @Override
    public Item getChiseledSandstone() {
        return this.getSandstone().withMeta(1);
    }

    @Override
    public Item getChiseledStoneBricks() {
        return this.getStoneBricks().withMeta(3);
    }

    @Override
    public Item getCoarseDirt() {
        return this.getDirt().withMeta(1);
    }

    @Override
    public Item getCobblestoneSlab() {
        return this.getStoneSlab().withMeta(3);
    }

    @Override
    public Item getCookedSalmon() {
        return this.getCookedCod().withMeta(1);
    }

    @Override
    public Item getCrackedStoneBricks() {
        return this.getStoneBricks().withMeta(2);
    }

    @Override
    public Item getCreeperHead() {
        return this.getSkeletonSkull().withMeta(4);
    }

    @Override
    public Item getCyanBanner() {
        return this.getBlackBanner().withMeta(6);
    }

    @Override
    public Item getCyanBed() {
        return this.getWhiteBed().withMeta(CYAN);
    }

    @Override
    public Item getCyanCarpet() {
        return this.getWhiteCarpet().withMeta(CYAN);
    }

    @Override
    public Item getCyanConcrete() {
        return this.getWhiteConcrete().withMeta(CYAN);
    }

    @Override
    public Item getCyanConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(CYAN);
    }

    @Override
    public Item getCyanDye() {
        return this.getBlackDye().withMeta(6);
    }

    @Override
    public Item getCyanStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(CYAN);
    }

    @Override
    public Item getCyanStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(CYAN);
    }

    @Override
    public Item getCyanTerracotta() {
        return this.getWhiteTerracotta().withMeta(CYAN);
    }

    @Override
    public Item getCyanWool() {
        return this.getWhiteWool().withMeta(CYAN);
    }

    @Override
    public Item getVeryDamagedAnvil() {
        return this.getAnvil().withMeta(2);
    }

    @Override
    public Item getDarkOakLeaves() {
        return this.getAcaciaLeaves().withMeta(DARK_OAK_1);
    }

    @Override
    public Item getDarkOakLog() {
        return this.getAcaciaLeaves().withMeta(DARK_OAK_1);
    }

    @Override
    public Item getDarkOakPlanks() {
        return this.getOakPlanks().withMeta(DARK_OAK_2);
    }

    @Override
    public Item getDarkOakSapling() {
        return this.getOakSapling().withMeta(DARK_OAK_2);
    }

    @Override
    public Item getDarkOakWoodSlab() {
        return this.getOakWoodSlab().withMeta(DARK_OAK_2);
    }

    @Override
    public Item getDarkPrismarine() {
        return this.getPrismarine().withMeta(2);
    }

    @Override
    public Item getDiorite() {
        return this.getStone().withMeta(3);
    }

    @Override
    public Item getDragonHead() {
        return this.getSkeletonSkull().withMeta(5);
    }

    @Override
    public Item getEnchantedGoldenApple() {
        return this.getGoldenApple().withMeta(1);
    }

    @Override
    public Item getFern() {
        return this.getGrass().withMeta(2);
    }

    @Override
    public Item getGranite() {
        return this.getStone().withMeta(1);
    }

    @Override
    public Item getGrass() {
        return Item.of("minecraft:tallgrass");
    }

    @Override
    public Item getGrayBanner() {
        return this.getBlackBanner().withMeta(8);
    }

    @Override
    public Item getGrayBed() {
        return this.getWhiteBed().withMeta(GRAY);
    }

    @Override
    public Item getGrayCarpet() {
        return this.getWhiteCarpet().withMeta(GRAY);
    }

    @Override
    public Item getGrayConcrete() {
        return this.getWhiteConcrete().withMeta(GRAY);
    }

    @Override
    public Item getGrayConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(GRAY);
    }

    @Override
    public Item getGrayDye() {
        return this.getBlackDye().withMeta(8);
    }

    @Override
    public Item getGrayStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(GRAY);
    }

    @Override
    public Item getGrayStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(GRAY);
    }

    @Override
    public Item getGrayTerracotta() {
        return this.getWhiteTerracotta().withMeta(GRAY);
    }

    @Override
    public Item getGrayWool() {
        return this.getWhiteWool().withMeta(GRAY);
    }

    @Override
    public Item getGreenBanner() {
        return this.getBlackBanner().withMeta(2);
    }

    @Override
    public Item getGreenBed() {
        return this.getWhiteBed().withMeta(GREEN);
    }

    @Override
    public Item getGreenCarpet() {
        return this.getWhiteCarpet().withMeta(GREEN);
    }

    @Override
    public Item getGreenConcrete() {
        return this.getWhiteConcrete().withMeta(GREEN);
    }

    @Override
    public Item getGreenConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(GREEN);
    }

    @Override
    public Item getGreenDye() {
        return this.getBlackDye().withMeta(2);
    }

    @Override
    public Item getGreenStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(GREEN);
    }

    @Override
    public Item getGreenStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(GREEN);
    }

    @Override
    public Item getGreenTerracotta() {
        return this.getWhiteTerracotta().withMeta(GREEN);
    }

    @Override
    public Item getGreenWool() {
        return this.getWhiteWool().withMeta(GREEN);
    }

    @Override
    public Item getChiseledStoneBrickMonsterEgg() {
        return this.getStoneMonsterEgg().withMeta(5);
    }

    @Override
    public Item getCobblestoneMonsterEgg() {
        return this.getStoneMonsterEgg().withMeta(1);
    }

    @Override
    public Item getCrackedStoneBrickMonsterEgg() {
        return this.getStoneMonsterEgg().withMeta(4);
    }

    @Override
    public Item getMossyStoneBrickMonsterEgg() {
        return this.getStoneMonsterEgg().withMeta(3);
    }

    @Override
    public Item getStoneBrickMonsterEgg() {
        return this.getStoneMonsterEgg().withMeta(2);
    }

    @Override
    public Item getJungleLeaves() {
        return this.getOakLeaves().withMeta(JUNGLE);
    }

    @Override
    public Item getJungleLog() {
        return this.getOakLog().withMeta(JUNGLE);
    }

    @Override
    public Item getJunglePlanks() {
        return this.getOakPlanks().withMeta(JUNGLE);
    }

    @Override
    public Item getJungleSapling() {
        return this.getOakSapling().withMeta(JUNGLE);
    }

    @Override
    public Item getJungleWoodSlab() {
        return this.getOakWoodSlab().withMeta(JUNGLE);
    }

    @Override
    public Item getLargeFern() {
        return this.getSunflower().withMeta(3);
    }

    @Override
    public Item getLightBlueBanner() {
        return this.getBlackBanner().withMeta(12);
    }

    @Override
    public Item getLightBlueBed() {
        return this.getWhiteBed().withMeta(LIGHT_BLUE);
    }

    @Override
    public Item getLightBlueCarpet() {
        return this.getWhiteCarpet().withMeta(LIGHT_BLUE);
    }

    @Override
    public Item getLightBlueConcrete() {
        return this.getWhiteConcrete().withMeta(LIGHT_BLUE);
    }

    @Override
    public Item getLightBlueConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(LIGHT_BLUE);
    }

    @Override
    public Item getLightBlueDye() {
        return this.getBlackDye().withMeta(12);
    }

    @Override
    public Item getLightBlueStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(LIGHT_BLUE);
    }

    @Override
    public Item getLightBlueStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(LIGHT_BLUE);
    }

    @Override
    public Item getLightBlueTerracotta() {
        return this.getWhiteTerracotta().withMeta(LIGHT_BLUE);
    }

    @Override
    public Item getLightBlueWool() {
        return this.getWhiteWool().withMeta(LIGHT_BLUE);
    }

    @Override
    public Item getLightGrayBanner() {
        return this.getBlackBanner().withMeta(7);
    }

    @Override
    public Item getLightGrayBed() {
        return this.getWhiteBed().withMeta(LIGHT_GRAY);
    }

    @Override
    public Item getLightGrayCarpet() {
        return this.getWhiteCarpet().withMeta(LIGHT_GRAY);
    }

    @Override
    public Item getLightGrayConcrete() {
        return this.getWhiteConcrete().withMeta(LIGHT_GRAY);
    }

    @Override
    public Item getLightGrayConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(LIGHT_GRAY);
    }

    @Override
    public Item getLightGrayDye() {
        return this.getBlackDye().withMeta(7);
    }

    @Override
    public Item getLightGrayStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(LIGHT_GRAY);
    }

    @Override
    public Item getLightGrayStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(LIGHT_GRAY);
    }

    @Override
    public Item getLightGrayTerracotta() {
        return this.getWhiteTerracotta().withMeta(LIGHT_GRAY);
    }

    @Override
    public Item getLightGrayWool() {
        return this.getWhiteWool().withMeta(LIGHT_GRAY);
    }

    @Override
    public Item getLilac() {
        return this.getSunflower().withMeta(1);
    }

    @Override
    public Item getLimeBanner() {
        return this.getBlackBanner().withMeta(10);
    }

    @Override
    public Item getLimeBed() {
        return this.getWhiteBed().withMeta(LIME);
    }

    @Override
    public Item getLimeCarpet() {
        return this.getWhiteCarpet().withMeta(LIME);
    }

    @Override
    public Item getLimeConcrete() {
        return this.getWhiteConcrete().withMeta(LIME);
    }

    @Override
    public Item getLimeConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(LIME);
    }

    @Override
    public Item getLimeDye() {
        return this.getBlackDye().withMeta(10);
    }

    @Override
    public Item getLimeStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(LIME);
    }

    @Override
    public Item getLimeStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(LIME);
    }

    @Override
    public Item getLimeTerracotta() {
        return this.getWhiteTerracotta().withMeta(LIME);
    }

    @Override
    public Item getLimeWool() {
        return this.getWhiteWool().withMeta(LIME);
    }

    @Override
    public Item getMagentaBanner() {
        return this.getBlackBanner().withMeta(13);
    }

    @Override
    public Item getMagentaBed() {
        return this.getWhiteBed().withMeta(MAGENTA);
    }

    @Override
    public Item getMagentaCarpet() {
        return this.getWhiteCarpet().withMeta(MAGENTA);
    }

    @Override
    public Item getMagentaConcrete() {
        return this.getWhiteConcrete().withMeta(MAGENTA);
    }

    @Override
    public Item getMagentaConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(MAGENTA);
    }

    @Override
    public Item getMagentaDye() {
        return this.getBlackDye().withMeta(13);
    }

    @Override
    public Item getMagentaStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(MAGENTA);
    }

    @Override
    public Item getMagentaStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(MAGENTA);
    }

    @Override
    public Item getMagentaTerracotta() {
        return this.getWhiteTerracotta().withMeta(MAGENTA);
    }

    @Override
    public Item getMagentaWool() {
        return this.getWhiteWool().withMeta(MAGENTA);
    }

    @Override
    public Item getMossyCobblestoneWall() {
        return this.getCobblestoneWall().withMeta(1);
    }

    @Override
    public Item getMossyStoneBricks() {
        return this.getStoneBricks().withMeta(1);
    }

    @Override
    public Item getNetherBrickSlab() {
        return this.getStoneSlab().withMeta(6);
    }

    @Override
    public Item getOakWoodSlab() {
        return Item.of("minecraft:wooden_slab");
    }

    @Override
    public Item getOrangeBanner() {
        return this.getBlackBanner().withMeta(14);
    }

    @Override
    public Item getOrangeBed() {
        return this.getWhiteBed().withMeta(ORANGE);
    }

    @Override
    public Item getOrangeCarpet() {
        return this.getWhiteCarpet().withMeta(ORANGE);
    }

    @Override
    public Item getOrangeConcrete() {
        return this.getWhiteConcrete().withMeta(ORANGE);
    }

    @Override
    public Item getOrangeConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(ORANGE);
    }

    @Override
    public Item getOrangeDye() {
        return this.getBlackDye().withMeta(14);
    }

    @Override
    public Item getOrangeStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(ORANGE);
    }

    @Override
    public Item getOrangeStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(ORANGE);
    }

    @Override
    public Item getOrangeTerracotta() {
        return this.getWhiteTerracotta().withMeta(ORANGE);
    }

    @Override
    public Item getOrangeTulip() {
        return this.getPoppy().withMeta(5);
    }

    @Override
    public Item getOrangeWool() {
        return this.getWhiteWool().withMeta(ORANGE);
    }

    @Override
    public Item getOxeyeDaisy() {
        return this.getPoppy().withMeta(8);
    }

    @Override
    public Item getPeony() {
        return this.getSunflower().withMeta(5);
    }

    @Override
    public Item getPinkBanner() {
        return this.getBlackBanner().withMeta(9);
    }

    @Override
    public Item getPinkBed() {
        return this.getWhiteBed().withMeta(PINK);
    }

    @Override
    public Item getPinkCarpet() {
        return this.getWhiteCarpet().withMeta(PINK);
    }

    @Override
    public Item getPinkConcrete() {
        return this.getWhiteConcrete().withMeta(PINK);
    }

    @Override
    public Item getPinkConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(PINK);
    }

    @Override
    public Item getPinkDye() {
        return this.getBlackDye().withMeta(9);
    }

    @Override
    public Item getPinkStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(PINK);
    }

    @Override
    public Item getPinkStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(PINK);
    }

    @Override
    public Item getPinkTerracotta() {
        return this.getWhiteTerracotta().withMeta(PINK);
    }

    @Override
    public Item getPinkTulip() {
        return this.getPoppy().withMeta(7);
    }

    @Override
    public Item getPinkWool() {
        return this.getWhiteWool().withMeta(PINK);
    }

    @Override
    public Item getSteveHead() {
        return this.getSkeletonSkull().withMeta(3);
    }

    @Override
    public Item getPodzol() {
        return this.getDirt().withMeta(2);
    }

    @Override
    public Item getPolishedAndesite() {
        return this.getStone().withMeta(6);
    }

    @Override
    public Item getPolishedDiorite() {
        return this.getStone().withMeta(4);
    }

    @Override
    public Item getPolishedGranite() {
        return this.getStone().withMeta(2);
    }

    @Override
    public Item getPrismarineBricks() {
        return this.getDarkPrismarine().withMeta(1);
    }

    @Override
    public Item getPufferfish() {
        return this.getRawCod().withMeta(3);
    }

    @Override
    public Item getPurpleBanner() {
        return this.getBlackBanner().withMeta(5);
    }

    @Override
    public Item getPurpleBed() {
        return this.getWhiteBed().withMeta(PURPLE);
    }

    @Override
    public Item getPurpleCarpet() {
        return this.getWhiteCarpet().withMeta(PURPLE);
    }

    @Override
    public Item getPurpleConcrete() {
        return this.getWhiteConcrete().withMeta(PURPLE);
    }

    @Override
    public Item getPurpleConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(PURPLE);
    }

    @Override
    public Item getPurpleDye() {
        return this.getBlackDye().withMeta(5);
    }

    @Override
    public Item getPurpleStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(PURPLE);
    }

    @Override
    public Item getPurpleStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(PURPLE);
    }

    @Override
    public Item getPurpleTerracotta() {
        return this.getWhiteTerracotta().withMeta(PURPLE);
    }

    @Override
    public Item getPurpleWool() {
        return this.getWhiteWool().withMeta(PURPLE);
    }

    @Override
    public Item getPillarQuartzBlock() {
        return this.getBlockOfQuartz().withMeta(2);
    }

    @Override
    public Item getQuartzSlab() {
        return this.getStoneSlab().withMeta(7);
    }

    @Override
    public Item getRedBanner() {
        return this.getBlackBanner().withMeta(1);
    }

    @Override
    public Item getRedBed() {
        return this.getWhiteBed().withMeta(RED);
    }

    @Override
    public Item getRedCarpet() {
        return this.getWhiteCarpet().withMeta(RED);
    }

    @Override
    public Item getRedConcrete() {
        return this.getWhiteConcrete().withMeta(RED);
    }

    @Override
    public Item getRedConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(RED);
    }

    @Override
    public Item getRedDye() {
        return this.getBlackDye().withMeta(1);
    }

    @Override
    public Item getRedSand() {
        return this.getSand().withMeta(1);
    }

    @Override
    public Item getRedSandstoneSlab() {
        return Item.of("minecraft:stone_slab2");
    }

    @Override
    public Item getRedStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(RED);
    }

    @Override
    public Item getRedStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(RED);
    }

    @Override
    public Item getRedTerracotta() {
        return this.getWhiteTerracotta().withMeta(RED);
    }

    @Override
    public Item getRedTulip() {
        return this.getPoppy().withMeta(4);
    }

    @Override
    public Item getRedWool() {
        return this.getWhiteWool().withMeta(RED);
    }

    @Override
    public Item getRoseBush() {
        return this.getSunflower().withMeta(4);
    }

    @Override
    public Item getRawSalmon() {
        return this.getRawCod().withMeta(1);
    }

    @Override
    public Item getSandstoneSlab() {
        return this.getStoneSlab().withMeta(1);
    }

    @Override
    public Item getSkeletonSkull() {
        return Item.of("minecraft:skull");
    }

    @Override
    public Item getSmoothRedSandstone() {
        return this.getRedSandstone().withMeta(2);
    }

    @Override
    public Item getSmoothSandstone() {
        return this.getSandstone().withMeta(2);
    }

    @Override
    public Item getSpruceLeaves() {
        return this.getOakLeaves().withMeta(SPRUCE);
    }

    @Override
    public Item getSpruceLog() {
        return this.getOakLog().withMeta(SPRUCE);
    }

    @Override
    public Item getSprucePlanks() {
        return this.getOakPlanks().withMeta(SPRUCE);
    }

    @Override
    public Item getSpruceSapling() {
        return this.getOakSapling().withMeta(SPRUCE);
    }

    @Override
    public Item getSpruceWoodSlab() {
        return this.getOakWoodSlab().withMeta(SPRUCE);
    }

    @Override
    public Item getStoneBrickSlab() {
        return this.getStoneSlab().withMeta(5);
    }

    @Override
    public Item getDoubleTallgrass() {
        return this.getSunflower().withMeta(2);
    }

    @Override
    public Item getTropicalFish() {
        return this.getRawCod().withMeta(2);
    }

    @Override
    public Item getWetSponge() {
        return this.getSponge().withMeta(1);
    }

    @Override
    public Item getWhiteBanner() {
        return this.getBlackBanner().withMeta(15);
    }

    @Override
    public Item getWhiteBed() {
        return Item.of("minecraft:bed");
    }

    @Override
    public Item getWhiteCarpet() {
        return Item.of("minecraft:carpet");
    }

    @Override
    public Item getWhiteConcrete() {
        return Item.of("minecraft:concrete");
    }

    @Override
    public Item getWhiteConcretePowder() {
        return Item.of("minecraft:concrete_powder");
    }

    @Override
    public Item getWhiteDye() {
        return this.getBlackDye().withMeta(15);
    }

    @Override
    public Item getWhiteStainedGlass() {
        return Item.of("minecraft:stained_glass");
    }

    @Override
    public Item getWhiteStainedGlassPane() {
        return Item.of("minecraft:stained_glass_pane");
    }

    @Override
    public Item getWhiteTulip() {
        return this.getPoppy().withMeta(6);
    }

    @Override
    public Item getWitherSkeletonSkull() {
        return this.getSkeletonSkull().withMeta(1);
    }

    @Override
    public Item getYellowBanner() {
        return this.getBlackBanner().withMeta(11);
    }

    @Override
    public Item getCarvedPumpkin() {
        return Item.of("minecraft:lit_pumpkin");
    }

    @Override
    public Item getRawCod() {
        return Item.of("minecraft:fish");
    }

    @Override
    public Item getCookedCod() {
        return Item.of("minecraft:cooked_fish");
    }

    @Override
    public Item getStoneMonsterEgg() {
        return Item.of("minecraft:monster_egg");
    }

    @Override
    public Item getInkSac() {
        return this.getBlackDye();
    }

    @Override
    public Item getPoppy() {
        return Item.of("minecraft:red_flower");
    }

    @Override
    public Item getSunflower() {
        return Item.of("minecraft:double_plant");
    }

    @Override
    public Item getWhiteWool() {
        return Item.of("minecraft:wool");
    }

    @Override
    public Item getYellowBed() {
        return this.getWhiteBed().withMeta(YELLOW);
    }

    @Override
    public Item getYellowCarpet() {
        return this.getWhiteCarpet().withMeta(YELLOW);
    }

    @Override
    public Item getYellowConcrete() {
        return this.getWhiteConcrete().withMeta(YELLOW);
    }

    @Override
    public Item getYellowConcretePowder() {
        return this.getWhiteConcretePowder().withMeta(YELLOW);
    }

    @Override
    public Item getYellowDye() {
        return this.getBlackDye().withMeta(11);
    }

    @Override
    public Item getYellowStainedGlass() {
        return this.getWhiteStainedGlass().withMeta(YELLOW);
    }

    @Override
    public Item getYellowStainedGlassPane() {
        return this.getWhiteStainedGlassPane().withMeta(YELLOW);
    }

    @Override
    public Item getYellowTerracotta() {
        return this.getWhiteTerracotta().withMeta(YELLOW);
    }

    @Override
    public Item getYellowWool() {
        return this.getWhiteWool().withMeta(YELLOW);
    }

    @Override
    public Item getZombieHead() {
        return this.getSkeletonSkull().withMeta(0);
    }
}
