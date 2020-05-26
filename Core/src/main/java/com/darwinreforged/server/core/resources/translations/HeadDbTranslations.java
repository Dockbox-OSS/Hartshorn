package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("headdatabase")
public class HeadDbTranslations {

    public static final Translation OPEN_GUI_ERROR = Translation.create("$4Failed to open Head Database GUI");
    public static final Translation HEADS_EVOLVED_API_URL = Translation.create("https://minecraft-heads.com/scripts/api.php?tags=true&cat={0}");
    public static final Translation HEADS_EVOLVED_FAILED_LOAD = Translation.create("$4Failed to load {0} heads");

    private HeadDbTranslations() {
    }

}
