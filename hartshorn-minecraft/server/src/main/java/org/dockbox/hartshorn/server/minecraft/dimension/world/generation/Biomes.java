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

package org.dockbox.hartshorn.server.minecraft.dimension.world.generation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Biomes implements Biome {
    BADLANDS("badlands"),
    BADLANDS_PLATEAU("badlands_plateau"),
    BAMBOO_JUNGLE("bamboo_jungle"),
    BAMBOO_JUNGLE_HILLS("bamboo_jungle_hills"),
    BASALT_DELTAS("basalt_deltas"),
    BEACH("beach"),
    BIRCH_FOREST("birch_forest"),
    BIRCH_FOREST_HILLS("birch_forest_hills"),
    COLD_OCEAN("cold_ocean"),
    CRIMSON_FOREST("crimson_forest"),
    DARK_FOREST("dark_forest"),
    DARK_FOREST_HILLS("dark_forest_hills"),
    DEEP_COLD_OCEAN("deep_cold_ocean"),
    DEEP_FROZEN_OCEAN("deep_frozen_ocean"),
    DEEP_LUKEWARM_OCEAN("deep_lukewarm_ocean"),
    DEEP_OCEAN("deep_ocean"),
    DEEP_WARM_OCEAN("deep_warm_ocean"),
    DESERT("desert"),
    DESERT_HILLS("desert_hills"),
    DESERT_LAKES("desert_lakes"),
    END_BARRENS("end_barrens"),
    END_HIGHLANDS("end_highlands"),
    END_MIDLANDS("end_midlands"),
    ERODED_BADLANDS("eroded_badlands"),
    FLOWER_FOREST("flower_forest"),
    FOREST("forest"),
    FROZEN_OCEAN("frozen_ocean"),
    FROZEN_RIVER("frozen_river"),
    GIANT_SPRUCE_TAIGA("giant_spruce_taiga"),
    GIANT_SPRUCE_TAIGA_HILLS("giant_spruce_taiga_hills"),
    GIANT_TREE_TAIGA("giant_tree_taiga"),
    GIANT_TREE_TAIGA_HILLS("giant_tree_taiga_hills"),
    GRAVELLY_MOUNTAINS("gravelly_mountains"),
    ICE_SPIKES("ice_spikes"),
    JUNGLE("jungle"),
    JUNGLE_EDGE("jungle_edge"),
    JUNGLE_HILLS("jungle_hills"),
    LUKEWARM_OCEAN("lukewarm_ocean"),
    MODIFIED_BADLANDS_PLATEAU("modified_badlands_plateau"),
    MODIFIED_GRAVELLY_MOUNTAINS("modified_gravelly_mountains"),
    MODIFIED_JUNGLE("modified_jungle"),
    MODIFIED_JUNGLE_EDGE("modified_jungle_edge"),
    MODIFIED_WOODED_BADLANDS_PLATEAU("modified_wooded_badlands_plateau"),
    MOUNTAIN_EDGE("mountain_edge"),
    MOUNTAINS("mountains"),
    MUSHROOM_FIELD_SHORE("mushroom_field_shore"),
    MUSHROOM_FIELDS("mushroom_fields"),
    NETHER_WASTES("nether_wastes"),
    OCEAN("ocean"),
    PLAINS("plains"),
    RIVER("river"),
    SAVANNA("savanna"),
    SAVANNA_PLATEAU("savanna_plateau"),
    SHATTERED_SAVANNA("shattered_savanna"),
    SHATTERED_SAVANNA_PLATEAU("shattered_savanna_plateau"),
    SMALL_END_ISLANDS("small_end_islands"),
    SNOWY_BEACH("snowy_beach"),
    SNOWY_MOUNTAINS("snowy_mountains"),
    SNOWY_TAIGA("snowy_taiga"),
    SNOWY_TAIGA_HILLS("snowy_taiga_hills"),
    SNOWY_TAIGA_MOUNTAINS("snowy_taiga_mountains"),
    SNOWY_TUNDRA("snowy_tundra"),
    SOUL_SAND_VALLEY("soul_sand_valley"),
    STONE_SHORE("stone_shore"),
    SUNFLOWER_PLAINS("sunflower_plains"),
    SWAMP("swamp"),
    SWAMP_HILLS("swamp_hills"),
    TAIGA("taiga"),
    TAIGA_HILLS("taiga_hills"),
    TAIGA_MOUNTAINS("taiga_mountains"),
    TALL_BIRCH_FOREST("tall_birch_forest"),
    TALL_BIRCH_HILLS("tall_birch_hills"),
    THE_END("the_end"),
    THE_VOID("the_void"),
    WARM_OCEAN("warm_ocean"),
    WARPED_FOREST("warped_forest"),
    WOODED_BADLANDS_PLATEAU("wooded_badlands_plateau"),
    WOODED_HILLS("wooded_hills"),
    WOODED_MOUNTAINS("wooded_mountains"),
    ;

    @Getter private final String id;
}
