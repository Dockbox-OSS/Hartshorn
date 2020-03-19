package com.darwinreforged.servermodifications.enums.brushtooltips;

public final class Flag implements Prototype {

  private String flag;
  private String description;

  Flag(String flag, String description) {
    this.flag = flag;
    this.description = description;
  }

  public String getFlag() {
    return flag;
  }

  public String getDescription() {
    return description;
  }
}
