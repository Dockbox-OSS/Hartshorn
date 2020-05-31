package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("worldunloader")
public class WorldUnloaderTranslations {

    public static final Translation WU_ADDED = Translation.create("added", "$1Added $2{0} $1to the unload blacklist");
    public static final Translation WORLD_NOT_FOUND = Translation.create("error_not_found", "$4Could not find that world!");

    private WorldUnloaderTranslations() {
    }
}
