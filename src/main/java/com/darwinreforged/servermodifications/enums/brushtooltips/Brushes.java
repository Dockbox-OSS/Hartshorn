package com.darwinreforged.servermodifications.enums.brushtooltips;

public enum Brushes {
    COPYPASTE(
            "Copy paste",
            "copypaste",
            Arguments.DEPTH.at(0),
            Flags.RANDOM_ROTATION.getFlag(),
            Flags.AUTO_VIEW.getFlag()),

    COMMAND("Commands", "command", Arguments.COMMAND_RADIUS.at(0), Arguments.COMMANDS.at(1)),

    POPULATE_SCHEMATIC(
            "Populate Schematic",
            "populateschematic",
            Arguments.MASK.at(0),
            Arguments.SOURCE.at(1),
            Arguments.RADIUS.at(2),
            Arguments.POINTS.at(3),
            Flags.RANDOM_ROTATION.getFlag()),

    SCATTER_COMMAND(
            "Commands scatter",
            "scmd",
            Arguments.SCATTER_RADIUS.at(0),
            Arguments.POINTS.at(1),
            Arguments.COMMAND_RADIUS.at(2),
            Arguments.COMMANDS.at(3)),

    SHATTER(
            "Shatter",
            "shatter",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.COUNT.at(2)),

    ERODE("Erode", "erode", Arguments.RADIUS.at(0)),

    SPHERE(
            "Sphere",
            "sphere",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Flags.HOLLOW.getFlag(),
            Flags.FALLING.getFlag()),

    PULL("Pull", "pull", Arguments.RADIUS.at(0)),

    STENCIL(
            "Stencil",
            "stencil",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.SOURCE.at(2),
            Arguments.ROTATION.at(3),
            Arguments.YSCALE.at(4),
            Flags.MAX_SATURATION.getFlag(),
            Flags.RANDOM_ROTATION.getFlag()),

    RECURSIVE(
            "Recursive",
            "recursive",
            Arguments.PATTERN_TO.at(0),
            Arguments.RADIUS.at(1),
            Flags.DEPTH_FIRST.getFlag()),

    SPLINE("Spline", "spline", Arguments.PATTERN.at(0)),

    SWEEP("Sweep", "sweep", Arguments.COPIES.at(0)),

    CATENARY(
            "Catenary",
            "catenary",
            Arguments.PATTERN.at(0),
            Arguments.LENGTHFACTOR.at(1),
            Arguments.SIZE.at(2),
            Flags.HOLLOW.getFlag(),
            Flags.SELECT_AFTER.getFlag()),

    LINE(
            "Line",
            "line",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Flags.HOLLOW.getFlag(),
            Flags.SELECT_AFTER.getFlag(),
            Flags.FLAT_LINE.getFlag()),

    SURFACE_SPLINE(
            "Surface Spline",
            "sspl",
            Arguments.PATTERN.at(0),
            Arguments.SIZE.at(1),
            Arguments.TENSION.at(2),
            Arguments.BIAS.at(3),
            Arguments.CONTINUITY.at(4),
            Arguments.QUALITY.at(5)),

    BLENDBALL("Blend Ball", "blendball", Arguments.RADIUS.at(0)),

    CIRCLE("Circle", "circle", Arguments.PATTERN.at(0), Arguments.RADIUS.at(1)),

    ROCK(
            "Rock",
            "rock",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.ROUNDNESS.at(2),
            Arguments.FREQUENCY.at(3),
            Arguments.AMPLITUDE.at(4),
            Flags.HOLLOW.getFlag()),

    HEIGHT(
            "Height",
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
            "Flatten",
            "flatten",
            Arguments.RADIUS.at(0),
            Arguments.SOURCE.at(1),
            Arguments.ROTATION.at(2),
            Arguments.YSCALE.at(3),
            Flags.HOLLOW.getFlag(),
            Flags.RANDOM_ROTATION.getFlag(),
            Flags.SNOW_LAYERS.getFlag(),
            Flags.DISABLE_SMOOTHING.getFlag()),

    LAYER("Layer", "layer", Arguments.RADIUS.at(0), Arguments.PATTERN.at(1)),

    CYLINDER(
            "Cylinder",
            "cylinder",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.HEIGHT.at(2),
            Flags.HOLLOW.getFlag()),

    SURFACE(
            "Surface",
            "surface",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Flags.MAX_SATURATION.getFlag(),
            Flags.RANDOM_ROTATION.getFlag()),

    EXTINGUISHER("Extinguisher", "ex", Arguments.RADIUS.at(0)),

    GRAVITY("Gravity", "gravity", Arguments.RADIUS.at(0), Flags.HOLLOW.getFlag()),

    CLIPBOARD("Clipboard", "clipboard", Flags.NO_AIR.getFlag(), Flags.RELATIVE_LOC.getFlag()),

    SPLATTER(
            "Splatter",
            "splatter",
            Arguments.PATTERN.at(0),
            Arguments.RADIUS.at(1),
            Arguments.SEEDS.at(2),
            Arguments.RECURSION.at(3),
            Arguments.SOLID.at(4)),

    CLIFF(
            "Cliff",
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
            "Smooth",
            "smooth",
            Arguments.SIZE.at(0),
            Arguments.ITERATIONS.at(1),
            Flags.NATURAL_OCCURRING.getFlag()),

    SCATTER(
            "Scatter",
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
