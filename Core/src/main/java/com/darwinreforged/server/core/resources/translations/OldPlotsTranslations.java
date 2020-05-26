package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("oldplots")
public class OldPlotsTranslations {

    public static final Translation OLP_NO_STORAGE_FILE = Translation.create("$4No OldPlots storage file present!");
    public static final Translation OLP_LIST_ITEM = Translation.create("$3 - $2#{0} : $1{1}$2, $1{2},{3}");
    public static final Translation OLP_LIST_HEADER = Translation.create("$1OldPlots for $1{0}");
    public static final Translation OLP_FAILED_READ = Translation.create("$4Failed to obtain information from database");

    private OldPlotsTranslations() {
    }
}
