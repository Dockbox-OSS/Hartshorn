package com.darwinreforged.servermodifications.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HeadsEvolvedHead {
  public enum Category {
    ALPHABET,
    ANIMALS,
    BLOCKS,
    DECORATION,
    FOOD_DRINKS,
    HUMANS,
    HUMANOID,
    MISCELLANEOUS,
    MONSTERS,
    PLANTS
  }

  private static Set<HeadsEvolvedHead> HeadsEvolvedHeadSet = new HashSet<>();

  private String name;
  private String uuid;
  private String value;
  private Category category;
  private String[] tags;

  public HeadsEvolvedHead(String name, String uuid, String value, String tags, Category category) {
    this.name = name;
    this.uuid = uuid;
    this.value = value;
    this.category = category;
    this.tags = tags.split(",");
    HeadsEvolvedHeadSet.add(this);
  }

  public static Set<HeadsEvolvedHead> getHeadsEvolvedHeadSet() {
    return HeadsEvolvedHeadSet;
  }

  public String getName() {
    return name;
  }

  public String getUuid() {
    return uuid;
  }

  public String getValue() {
    return value;
  }

  public String[] getTags() {
    return tags;
  }

  public Category getCategory() {
    return category;
  }

  public static HeadsEvolvedHead getFirstFromCategory(Category category) {
    return (HeadsEvolvedHead) getByCategory(category).toArray()[0];
  }

  public static Set<HeadsEvolvedHead> getByNameAndTag(String query) {
    return HeadsEvolvedHeadSet.stream()
        .filter(
            object -> {
              for (String tag : object.getTags()) {
                if (tag.toLowerCase().contains(query.toLowerCase())) return true;
              }
              if (object.getName().toLowerCase().contains(query.toLowerCase())) return true;
              return false;
            })
        .collect(Collectors.toSet());
  }

  public static Set<HeadsEvolvedHead> getByCategory(Category category) {
    return HeadsEvolvedHeadSet.stream()
        .filter(object -> object.getCategory() == category)
        .collect(Collectors.toSet());
  }

  public static Set<HeadsEvolvedHead> getByNameIncludes(String name) {
    return HeadsEvolvedHeadSet.stream()
        .filter(object -> object.getName().toLowerCase().contains(name.toLowerCase()))
        .collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return name + "\n\tCategory : " + category + "\n\tUUID : " + uuid + "\n\tValue : " + value;
  }
}
