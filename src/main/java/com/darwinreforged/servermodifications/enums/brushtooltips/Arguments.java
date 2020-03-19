package com.darwinreforged.servermodifications.enums.brushtooltips;

public enum Arguments {
  DEPTH("Depth"),
  RADIUS("Radius"),
  COMMANDS("Commands"),
  MASK("Mask"),
  SOURCE("Source"),
  POINTS("Points"),
  COMMAND_RADIUS("Radius"),
  SCATTER_RADIUS("Radius"),
  PATTERN("Pattern"),
  COUNT("Count"),
  ROTATION("Rotation"),
  YSCALE("Y-scale"),
  PATTERN_TO("To pattern"),
  COPIES("Copies"),
  LENGTHFACTOR("Length factor"),
  SIZE("Radius"),
  TENSION("Tension"),
  BIAS("Bias"),
  CONTINUITY("Continuity"),
  QUALITY("Quality"),
  ROUNDNESS("Roundness"),
  FREQUENCY("Frequency"),
  AMPLITUDE("Amplitude"),
  SEEDS("Seeds"),
  RECURSION("Recursion"),
  SOLID("Solid"),
  ITERATIONS("Iterations"),
  DISTANCE("Distance"),
    HEIGHT("Height");

  private String description;

  Arguments(String description) {
    this.description = description;
  }

  public Argument at(int index) {
    return new Argument(index, description);
  }
}
