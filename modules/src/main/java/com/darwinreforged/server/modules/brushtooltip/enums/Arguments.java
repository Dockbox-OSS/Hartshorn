package com.darwinreforged.server.modules.brushtooltip.enums;

import com.darwinreforged.server.api.resources.Translations;

public enum Arguments {
    DEPTH(Translations.BRUSH_DESCRIPTION_DEPTH.s()),
    RADIUS(Translations.BRUSH_DESCRIPTION_RADIUS.s()),
    COMMANDS(Translations.BRUSH_DESCRIPTION_COMMANDS.s()),
    MASK(Translations.BRUSH_DESCRIPTION_MASK.s()),
    SOURCE(Translations.BRUSH_DESCRIPTION_SOURCE.s()),
    POINTS(Translations.BRUSH_DESCRIPTION_POINTS.s()),
    COMMAND_RADIUS(Translations.BRUSH_DESCRIPTION_COMMAND_RADIUS.s()),
    SCATTER_RADIUS(Translations.BRUSH_DESCRIPTION_SCATTER_RADIUS.s()),
    PATTERN(Translations.BRUSH_DESCRIPTION_PATTERN.s()),
    COUNT(Translations.BRUSH_DESCRIPTION_COUNT.s()),
    ROTATION(Translations.BRUSH_DESCRIPTION_ROTATION.s()),
    YSCALE(Translations.BRUSH_DESCRIPTION_YSCALE.s()),
    PATTERN_TO(Translations.BRUSH_DESCRIPTION_PATTERN_TO.s()),
    COPIES(Translations.BRUSH_DESCRIPTION_COPIES.s()),
    LENGTHFACTOR(Translations.BRUSH_DESCRIPTION_LENGTHFACTOR.s()),
    SIZE(Translations.BRUSH_DESCRIPTION_SIZE.s()),
    TENSION(Translations.BRUSH_DESCRIPTION_TENSION.s()),
    BIAS(Translations.BRUSH_DESCRIPTION_BIAS.s()),
    CONTINUITY(Translations.BRUSH_DESCRIPTION_CONTINUITY.s()),
    QUALITY(Translations.BRUSH_DESCRIPTION_QUALITY.s()),
    ROUNDNESS(Translations.BRUSH_DESCRIPTION_ROUNDNESS.s()),
    FREQUENCY(Translations.BRUSH_DESCRIPTION_FREQUENCY.s()),
    AMPLITUDE(Translations.BRUSH_DESCRIPTION_AMPLITUDE.s()),
    SEEDS(Translations.BRUSH_DESCRIPTION_SEEDS.s()),
    RECURSION(Translations.BRUSH_DESCRIPTION_RECURSION.s()),
    SOLID(Translations.BRUSH_DESCRIPTION_SOLID.s()),
    ITERATIONS(Translations.BRUSH_DESCRIPTION_ITERATIONS.s()),
    DISTANCE(Translations.BRUSH_DESCRIPTION_DISTANCE.s()),
    HEIGHT(Translations.BRUSH_DESCRIPTION_HEIGHT.s());

    private String description;

    Arguments(String description) {
        this.description = description;
    }

    public Argument at(int index) {
        return new Argument(index, description);
    }
}
