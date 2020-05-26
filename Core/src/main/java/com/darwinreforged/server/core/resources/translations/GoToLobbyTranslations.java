package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("gotolobby")
public class GoToLobbyTranslations {

    public static final Translation GTL_WARPED = Translation.create("$1You have been teleported to the lobby as the world you were previously in is disabled");

    private GoToLobbyTranslations() {
    }

}
