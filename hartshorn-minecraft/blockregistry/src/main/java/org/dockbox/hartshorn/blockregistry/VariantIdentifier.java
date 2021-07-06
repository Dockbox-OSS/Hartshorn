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

package org.dockbox.hartshorn.blockregistry;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.persistence.registry.RegistryIdentifier;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Locale;
import java.util.Map;

public enum VariantIdentifier implements RegistryIdentifier {
    FULL,
    SMALL_ARCH,
    SMALL_ARCH_HALF,
    TWO_METER_ARCH,
    ARROWSLIT,
    SMALL_WINDOW,
    SMALL_WINDOW_HALF,
    BALUSTRADE,
    CAPITAL,
    SPHERE,
    SLAB,
    QUARTER_SLAB,
    CORNER_SLAB,
    EIGHTH_SLAB,
    TWO_METER_ARCH_HALF,
    VERTICAL_CORNER_SLAB,
    VERTICAL_SLAB,
    VERTICAL_CORNER,
    VERTICAL_QUARTER,
    STAIRS,
    WALL,
    PILLAR_SMALL,
    PILLAR,
    PILLAR_LARGE,
    ROUND_ARCH,
    GOTHIC_ARCH,
    SEGMENTAL_ARCH,

    LARGE_BRANCH,
    BRANCH,
    SMALL_BRANCH,
    STUMP,

    BEAM_HORIZONTAL,
    BEAM_VERTICAL,
    BEAM_POSTS,
    BEAM_DOOR_FRAME_POSTS,
    BEAM_LINTEL,
    BEAM_DOOR_FRAME_LINTEL,

    //TODO: Go through rest of these
    FENCE,
    FENCE_GATE,
    TRAPDOOR,
    BOARDS,
    LAYER("snow"),
    ROCKS,
    RAIL,
    DOOR,

    PIPE,
    MOUNTED_PIPE,
    SIX_WAY_FLANGE("6_way_flange"),
    FIVE_WAY_FLANGE("5_way_flange"),
    FOUR_WAY_FLANGE("4_way_flange"),
    THREE_WAY_FLANGE("3_way_flange"),
    ELBOW_FLANGE,
    GATE_VALVE,
    BALL_VALVE,
    CROSS_FLANGE,
    T_FLANGE,

    RAILING_HORIZONTAL,
    RAILING_DIAGONAL,
    RAILING_CORNER,

    CARPET,
    LADDER,
    PANE,
    VINE,
    IRONBAR,
    SHUTTERS;


    private final String identifier;
    private static final Map<String, VariantIdentifier> identifierMap = HartshornUtils.emptyConcurrentMap();

    static {
        for (VariantIdentifier variantIdentifier : values()) {
            identifierMap.put(variantIdentifier.identifier, variantIdentifier);
        }
    }

    VariantIdentifier(String identifier) {
        this.identifier = identifier;
    }

    VariantIdentifier() {
        this.identifier = this.name().toLowerCase(Locale.ROOT);
    }

    public static Exceptional<VariantIdentifier> of(String identifier) {
        identifier = identifier.toLowerCase();

        return identifierMap.containsKey(identifier)
                ? Exceptional.of(identifierMap.get(identifier))
                : Exceptional.empty();
    }

    public String getIdentifier() {
        return this.identifier;
    }
}