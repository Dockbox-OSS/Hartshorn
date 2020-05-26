package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("pweather")
public class PlayerWeatherTranslations {

    public static final Translation UNKNOWN_WEATHER_TYPE = Translation.create("$4That weather type is unknown");
    public static final Translation PLOT_WEATHER_SET = Translation.create("$2Plot weather set to: $1{0}");
    public static final Translation WEATHER_ERROR_NO_OWNER = Translation.create("You must be the owner of the plot to execute this command");
    public static final Translation WEATHER_ERROR_NO_WEATHER_TYPE = Translation.create("You must enter a weather type");
    public static final Translation WEATHER_USING_GLOBAL = Translation.create("Using global weather : {0}");
    public static final Translation WEATHER_DEBUG = Translation.create("Current Weather Type: {0}\nIs Lightning Player: {1}\n{2}");
    public static final Translation WEATHER_DISABLED_USER = Translation.create("$4Sorry, but pweather is currently disabled");
    public static final Translation LIGHTNING_SCHEDULE_ACTIVE = Translation.create("Lightning schedular active with {0} players");
    public static final Translation LIGHTNING_SCHEDULE_INACTIVE = Translation.create("Lightning schedular inactive with {0} players");

    private PlayerWeatherTranslations() {
    }
}
