package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("locator")
public class PlayerLocatorTranslations {

    public static final Translation PLAYER_IN_WORLD = Translation.create("$1{0} $2is in $1{1}");
    public static final Translation PLAYER_ON_ROAD = Translation.create("$1{0} $2is on a road");
    public static final Translation PLAYER_IN_PLOT = Translation.create("$1{0} $2is in $1{1}, {2}");

    private PlayerLocatorTranslations() {
    }

}
