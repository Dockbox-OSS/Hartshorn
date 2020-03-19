package com.darwinreforged.servermodifications.enums.brushtooltips;

public class Argument implements Prototype {

  private int index;
  private String description;

  Argument(int index, String description) {
    this.index = index;
    this.description = description;
  }

  public int getIndex() {
    return index;
  }

  public String getDescription() {
    return description;
  }
}
