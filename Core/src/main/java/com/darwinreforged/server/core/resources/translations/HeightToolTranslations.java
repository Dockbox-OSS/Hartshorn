package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("heighttool")
public class HeightToolTranslations {

    public static final Translation HEIGHT_TOO_HIGH = Translation.create("$4Height cannot be above 8");
    public static final Translation HEIGHT_TOO_LOW = Translation.create("$4Height cannot be below 1");
    public static final Translation HEIGHTTOOL_NAME = Translation.create("$1Layer Height Tool: $2{0}");
    public static final Translation HEIGHTTOOL_SET = Translation.create("Successfully set the layer height to $2{0}");
    public static final Translation HEIGHTTOOL_FAILED_BIND = Translation.create("$4Tool cannot be bound to a block");

    private HeightToolTranslations() {
    }

}
