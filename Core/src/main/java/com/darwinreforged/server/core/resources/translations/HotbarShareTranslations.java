package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("hotbar")
public class HotbarShareTranslations {

    public static final Translation SHARED_HOTBAR_WITH = Translation.create("You shared your hotbar with {0}");
    public static final Translation PLAYER_SHARED_HOTBAR = Translation.create("$2{0} $1shared their hotbar");
    public static final Translation FULL_HOTBAR = Translation.create("$4Your hotbar is full, please clear one or more slots");
    public static final Translation HOTBAR_SHARE_HEADER = Translation.create("$1&m------- &r$2{0}'s Hotbar $1&m-------");
    public static final Translation HOTBAR_SHARE_INDEX = Translation.create("$2#{0} : $1{1}");
    public static final Translation HOTBAR_SHARE_ENCHANTED = Translation.create("$2  Enchanted : ");
    public static final Translation HOTBAR_SHARE_LORE = Translation.create("$2  Lore : ");
    public static final Translation HOTBAR_VIEW_BUTTON = Translation.create("$2[$1View$2]");
    public static final Translation HOTBAR_VIEW_BUTTON_HOVER = Translation.create("View hotbar");
    public static final Translation HOTBAR_LOAD_BUTTON = Translation.create("$2[$1Load$2]");
    public static final Translation HOTBAR_LOAD_BUTTON_HOVER = Translation.create("Load hotbar");

    private HotbarShareTranslations() {
    }
}
