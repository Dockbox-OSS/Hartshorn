package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("dave")
public class DaveTranslations {

    public static final Translation DAVE_LINK_SUGGESTION = Translation.create("Here's a useful link, $1{0}");
    public static final Translation DAVE_LINK_SUGGESTION_HOVER = Translation.create("$2Click to open $1{0}");
    public static final Translation DAVE_DISCORD_FORMAT = Translation.create("**Dave** ≫ {0}");
    public static final Translation DAVE_MUTED = Translation.create("Muted Dave, note that important triggers will always show");
    public static final Translation DAVE_UNMUTED = Translation.create("Unmuted Dave");
    public static final Translation DAVE_RELOADED_USER = Translation.create("{0} &f: Reloaded Dave without breaking stuff, whoo!");
    public static final Translation DAVE_TRIGGER_LIST_ITEM = Translation.create("$3 - $1{0}");
    public static final Translation DAVE_TRIGGER_HOVER = Translation.create("$1Click to perform trigger");

    DaveTranslations() {
    }
}
