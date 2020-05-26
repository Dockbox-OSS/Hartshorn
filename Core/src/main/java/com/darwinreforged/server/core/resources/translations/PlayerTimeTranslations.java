package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("ptime")
public class PlayerTimeTranslations {

    public static final Translation PTIME_INVALID_NUMBER = Translation.create("'{0}' is not a valid number");
    public static final Translation PTIME_NUMBER_TOO_SMALL = Translation.create("The number you have entered ({0}) is too small, it must be at least 0");
    public static final Translation PTIME_IN_SYNC = Translation.create("Your time is currently in sync with the server's time");
    public static final Translation PTIME_AHEAD = Translation.create("Your time is currently running {0} ticks ahead of the server");

    private PlayerTimeTranslations() {
    }
}
