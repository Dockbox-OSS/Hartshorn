package com.darwinreforged.servermodifications.enums.brushtooltips;

public enum Flags {
  AUTO_VIEW("-a", "Auto view"),
  RANDOM_ROTATION("-r", "Random rotation"),
  HOLLOW("-h", "Hollow"),
  FALLING("-f", "Falling"),
  FLAT_LINE("-f", "Flat line"),
  MAX_SATURATION("-w", "Maximum saturation"),
  DEPTH_FIRST("-d", "Depth first"),
  SNOW_LAYERS("-l", "Snow layers"),
  DISABLE_SMOOTHING("-s", "Smoothing disabled"),
  NO_AIR("-a", "No air"),
  RELATIVE_LOC("-p", "Relative location"),
  NATURAL_OCCURRING("-n", "Naturally occurring"),
  SELECT_AFTER("-s", "Select after drawing"),
  OVERLAY("-o", "Overlay");

  private Flag flag;

  Flags(String flag, String description) {
    this.flag = new Flag(flag, description);
  }

  public Flag getFlag() {
    return flag;
  }
}
