package org.dockbox.hartshorn.blockregistry.init;

import org.dockbox.hartshorn.blockregistry.init.VanillaProps.ModGroups;
import org.dockbox.hartshorn.blockregistry.init.VanillaProps.RenderLayer;
import org.dockbox.hartshorn.blockregistry.init.VanillaProps.TypeList;

public class BarrelsInit {

    public static void init() {
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("empty_round_barrel")
                .manual()
                .solid(false)
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("round_barrel")
                .manual()
                .solid(false)
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("rusty_round_barrel")
                .manual()
                .solid(false)
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("empty_rusty_round_barrel")
                .manual()
                .solid(false)
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("broken_rusty_round_barrel")
                .manual()
                .solid(false)
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("barrel_of_dirt")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/barrel_of_dirt")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .waterColor()
                .group(ModGroups.STORAGE)
                .name("empty_barrel_with_grille")
                .manual()
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .waterColor()
                .group(ModGroups.STORAGE)
                .name("empty_barrel")
                .manual()
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("empty_bucket")
                .manual()
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .waterColor()
                .group(ModGroups.STORAGE)
                .name("empty_small_old_wicker_basket")
                .manual()
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.stone()
                .group(ModGroups.FOOD_BLOCKS)
                .name("honey_pot")
                .manual()
                .blocking(false)
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("small_old_wicker_basket_with_apples")
                .manual()
                .render(RenderLayer.CUTOUT)
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("closed_old_wicker_basket")
                .texture("end", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("*", "block/3_decoration/4_cloth/old_wicker_basket_opaque")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .waterColor()
                .group(ModGroups.STORAGE)
                .name("old_wicker_basket")
                .manual()
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .waterColor()
                .group(ModGroups.STORAGE)
                .name("wicker_basket")
                .manual()
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("crate_of_iron_ore")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/crate_of_iron_ore")
                .texture("*", "block/3_decoration/3_wood/crate_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("crate_of_diamonds")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/crate_of_diamonds")
                .texture("*", "block/3_decoration/3_wood/crate_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("crate_of_emeralds")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/crate_of_emeralds")
                .texture("*", "block/3_decoration/3_wood/crate_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("crate_of_lapis_lazuli")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/crate_of_lapis_lazuli")
                .texture("*", "block/3_decoration/3_wood/crate_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("barrel_of_clay")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/barrel_of_clay")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("full_chest_of_gold")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/full_chest_of_gold_top")
                .texture("north", "block/3_decoration/3_wood/full_chest_of_gold_side_1")
                .texture("south", "block/3_decoration/3_wood/full_chest_of_gold_side_1")
                .texture("east", "block/3_decoration/3_wood/full_chest_of_gold_side_2")
                .texture("west", "block/3_decoration/3_wood/full_chest_of_gold_side_2")
                .texture("*", "block/3_decoration/3_wood/full_chest_of_gold_side_2")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("full_barrel_of_gold_ore")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/full_barrel_of_gold_ore")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("sack_of_gold")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("top", "block/3_decoration/4_cloth/sack_of_gold")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.metal()
                .group(ModGroups.METAL)
                .name("pile_of_gold_coins")
                .texture("block/3_decoration/5_treasure/pile_of_gold_coins")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("sack_of_rubies")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("top", "block/3_decoration/4_cloth/sack_of_rubies")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("sack_of_pipe_weed")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("top", "block/6_foodstuffs/4_food/sack_of_pipe_weed")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("crate_of_coal")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/3_decoration/3_wood/crate_of_coal_top")
                .texture("*", "block/3_decoration/3_wood/crate_of_coal")
                .register(TypeList.of(Void.class));
        VanillaProps.metal()
                .waterColor()
                .group(ModGroups.APPLIANCES)
                .name("cauldron_with_grille")
                .manual()
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("wooden_box")
                .texture("block/3_decoration/3_wood/wooden_box")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.STORAGE)
                .name("cloth_covered_crate")
                .texture("bottom", "block/1_basic_refined/3_wood/spruce/spruce_paneling")
                .texture("top", "block/3_decoration/3_wood/cloth_covered_crate_top")
                .texture("*", "block/3_decoration/3_wood/cloth_covered_crate")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_barrel_of_apples")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_apples")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_sack_of_apples")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_apples")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_wicker_basket_of_apples")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_apples")
                .texture("*", "block/3_decoration/4_cloth/old_wicker_basket_opaque")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_barrel_of_bread")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_bread")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_sack_of_bread")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_bread")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_barrel_of_cabbage")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_cabbage")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_barrel_of_cocoa")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_cocoa")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_sack_of_cocoa")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_cocoa")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_wicker_basket_of_cocoa")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_cocoa")
                .texture("*", "block/3_decoration/4_cloth/old_wicker_basket_opaque")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_barrel_of_eggs")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_eggs")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_barrel_of_fish")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_fish")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_barrel_of_potatoes")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_potatoes")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_sack_of_potatoes")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_potatoes")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_barrel_of_turnips")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/4_food/full_barrel_of_turnips")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_sack_of_flour")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("top", "block/6_foodstuffs/4_food/full_sack_of_flour")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_sack_of_grapes")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("top", "block/6_foodstuffs/4_food/full_sack_of_grapes")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("full_sack_of_hops")
                .texture("bottom", "block/3_decoration/4_cloth/sack_bottom")
                .texture("top", "block/6_foodstuffs/4_food/full_sack_of_hops")
                .texture("*", "block/3_decoration/4_cloth/sack_side")
                .register(TypeList.of(Void.class));
        VanillaProps.planks()
                .group(ModGroups.FOOD_BLOCKS)
                .name("wine_barrel")
                .texture("bottom", "block/3_decoration/3_wood/barrel_topbottom")
                .texture("top", "block/6_foodstuffs/6_glass/wine_barrel")
                .texture("*", "block/3_decoration/3_wood/barrel_side")
                .register(TypeList.of(Void.class));
    }
}