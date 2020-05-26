package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("pidbar")
public class PlotIdBarTranslations {

    public static final Translation PID_USERS_TRUSTED_MORE = Translation.create("{0}, {1} and {2} others");
    public static final Translation PID_USERS_TRUSTED = Translation.create("{0}, {1}");
    public static final Translation PID_WORLD_FORMAT = Translation.create("$2World ID : $1{0}");
    public static final Translation PID_PLOT_FORMAT = Translation.create("$2Plot ID : $1{0}, {1}");
    public static final Translation PID_OWNER_FORMAT = Translation.create("$2Owner : $1{0}");
    public static final Translation PID_BAR_SEPARATOR = Translation.create(" &f|-=-| ");
    public static final Translation PID_BAR_MEMBERS = Translation.create("$2Members : $1{0}");
    public static final Translation PID_TOGGLE_BAR = Translation.create("Updated PlotID Bar preference to $2{0}");
    public static final Translation PID_TOGGLE_MEMBERS = Translation.create("Updated PlotID Members preference to $2{0}");

    private PlotIdBarTranslations() {
    }
}
