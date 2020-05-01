package com.darwinreforged.server.modules.brushtooltip.enums;

import com.darwinreforged.server.core.resources.Translations;

public enum Brushes {
    COPYPASTE(
            Translations.BRUSH_NAME_COPYPASTE.s(),
            "copypaste",
            Arguments.DEPTH.at(0),
            Flags.RANDOM_ROTATION.getFlag(),
            Flags.AUTO_VIEW.getFlag()),

    COMMAND(Translations.BRUSH_NAME_COMMAND.s(), "command", Arguments.COMMAND_RADIUS.at(0), Arguments.COMMANDS.at(1)),

    POPULATE_SCHEMATIC(
            Translations.BRUSH_NAME_POPULATE_SCHEMATIC.s(),
            "populateschematic",
            Arguments.MASK.at(0),
            Arguments.SOURCE.at(1),
            Arguments.RADIUS.at(2),
            Arguments.POINTS.at(3),
            Flags.RANDOM_ROTATION.getFlag()),

    SCATTER_COMMAND(
            Translations.BRUSH_NAME_SCATTER_COMMAND.s(),
            "scmd",
            Arguments.SCATTER_RADIUS.at(0),
            Arguments.POINTS.at(1),
            Arguments.COMMAND_RADIUS.at(2),
            Arguments.COMMANDS.at(3)),

    SHATTER(
            Translations.BRUSH_NAME_SHATTER.s(),
            "shatter",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.COUNT.at(2)),

    ERODE(Translations.BRUSH_NAME_ERODE.s(), "erode", Arguments.RADIUS.at(0)),

    SPHERE(
            Translations.BRUSH_NAME_SPHERE.s(),
            "sphere",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Flags.HOLLOW.getFlag(),
            Flags.FALLING.getFlag()),

    PULL(Translations.BRUSH_NAME_PULL.s(), "pull", Arguments.RADIUS.at(0)),

    STENCIL(
            Translations.BRUSH_NAME_STENCIL.s(),
            "stencil",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.SOURCE.at(2),
            Arguments.ROTATION.at(3),
            Arguments.YSCALE.at(4),
            Flags.MAX_SATURATION.getFlag(),
            Flags.RANDOM_ROTATION.getFlag()),

    RECURSIVE(
            Translations.BRUSH_NAME_RECURSIVE.s(),
            "recursive",
            Arguments.PATTERN_TO.at(0),
            Arguments.RADIUS.at(1),
            Flags.DEPTH_FIRST.getFlag()),

    SPLINE(Translations.BRUSH_NAME_SPLINE.s(), "spline", Arguments.PATTERN.at(0)),

    SWEEP(Translations.BRUSH_NAME_SWEEP.s(), "sweep", Arguments.COPIES.at(0)),

    CATENARY(
            Translations.BRUSH_NAME_CATENARY.s(),
            "catenary",
            Arguments.PATTERN.at(0),
            Arguments.LENGTHFACTOR.at(1),
            Arguments.SIZE.at(2),
            Flags.HOLLOW.getFlag(),
            Flags.SELECT_AFTER.getFlag()),

    LINE(
            Translations.BRUSH_NAME_LINE.s(),
            "line",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Flags.HOLLOW.getFlag(),
            Flags.SELECT_AFTER.getFlag(),
            Flags.FLAT_LINE.getFlag()),

    SURFACE_SPLINE(
            Translations.BRUSH_NAME_SURFACE_SPLINE.s(),
            "sspl",
            Arguments.PATTERN.at(0),
            Arguments.SIZE.at(1),
            Arguments.TENSION.at(2),
            Arguments.BIAS.at(3),
            Arguments.CONTINUITY.at(4),
            Arguments.QUALITY.at(5)),

    BLENDBALL(Translations.BRUSH_NAME_BLENDBALL.s(), "blendball", Arguments.RADIUS.at(0)),

    CIRCLE(Translations.BRUSH_NAME_CIRCLE.s(), "circle", Arguments.PATTERN.at(0), Arguments.RADIUS.at(1)),

    ROCK(
            Translations.BRUSH_NAME_ROCK.s(),
            "rock",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.ROUNDNESS.at(2),
            Arguments.FREQUENCY.at(3),
            Arguments.AMPLITUDE.at(4),
            Flags.HOLLOW.getFlag()),

    HEIGHT(
            Translations.BRUSH_NAME_HEIGHT.s(),
            "height",
            Arguments.RADIUS.at(0),
            Arguments.SOURCE.at(1),
            Arguments.ROTATION.at(2),
            Arguments.YSCALE.at(3),
            Flags.HOLLOW.getFlag(),
            Flags.RANDOM_ROTATION.getFlag(),
            Flags.SNOW_LAYERS.getFlag(),
            Flags.DISABLE_SMOOTHING.getFlag()),

    FLATTEN(
            Translations.BRUSH_NAME_FLATTEN.s(),
            "flatten",
            Arguments.RADIUS.at(0),
            Arguments.SOURCE.at(1),
            Arguments.ROTATION.at(2),
            Arguments.YSCALE.at(3),
            Flags.HOLLOW.getFlag(),
            Flags.RANDOM_ROTATION.getFlag(),
            Flags.SNOW_LAYERS.getFlag(),
            Flags.DISABLE_SMOOTHING.getFlag()),

    LAYER(Translations.BRUSH_NAME_LAYER.s(), "layer", Arguments.RADIUS.at(0), Arguments.PATTERN.at(1)),

    CYLINDER(
            Translations.BRUSH_NAME_CYLINDER.s(),
            "cylinder",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.HEIGHT.at(2),
            Flags.HOLLOW.getFlag()),

    SURFACE(
            Translations.BRUSH_NAME_SURFACE.s(),
            "surface",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Flags.MAX_SATURATION.getFlag(),
            Flags.RANDOM_ROTATION.getFlag()),

    EXTINGUISHER(Translations.BRUSH_NAME_EXTINGUISHER.s(), "ex", Arguments.RADIUS.at(0)),

    GRAVITY(Translations.BRUSH_NAME_GRAVITY.s(), "gravity", Arguments.RADIUS.at(0), Flags.HOLLOW.getFlag()),

    CLIPBOARD(Translations.BRUSH_NAME_CLIPBOARD.s(), "clipboard", Flags.NO_AIR.getFlag(), Flags.RELATIVE_LOC.getFlag()),

    SPLATTER(
            Translations.BRUSH_NAME_SPLATTER.s(),
            "splatter",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.SEEDS.at(2),
            Arguments.RECURSION.at(3),
            Arguments.SOLID.at(4)),

    CLIFF(
            Translations.BRUSH_NAME_CLIFF.s(),
            "cliff",
            Arguments.RADIUS.at(0),
            Arguments.SOURCE.at(1),
            Arguments.ROTATION.at(2),
            Arguments.YSCALE.at(3),
            Flags.HOLLOW.getFlag(),
            Flags.RANDOM_ROTATION.getFlag(),
            Flags.SNOW_LAYERS.getFlag(),
            Flags.DISABLE_SMOOTHING.getFlag()),

    SMOOTH(
            Translations.BRUSH_NAME_SMOOTH.s(),
            "smooth",
            Arguments.SIZE.at(0),
            Arguments.ITERATIONS.at(1),
            Flags.NATURAL_OCCURRING.getFlag()),

    SCATTER(
            Translations.BRUSH_NAME_SCATTER.s(),
            "scatter",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.POINTS.at(2),
            Arguments.DISTANCE.at(3),
            Flags.OVERLAY.getFlag());

    private Brush brush;
    private String alias;

    Brushes(String name, String alias, Prototype... prototypes) {
        this.brush = new Brush(name, prototypes);
        this.alias = alias;
    }

    public Brush getBrush() {
        return brush;
    }

    public String getAlias() {
        return alias;
    }
}
