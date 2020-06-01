package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("oldplots")
public class OldPlotsTranslations {

    public static final Translation OLP_NO_STORAGE_FILE = Translation.create("error_no_storage", "$4No OldPlots storage file present!");
    public static final Translation OLP_LIST_ITEM = Translation.create("list_row", "$3 - $2#{0} : $1{1}$2, $1{2},{3}");
    public static final Translation OLP_LIST_HEADER = Translation.create("list_header", "$1OldPlots for $1{0}");
    public static final Translation OLP_FAILED_READ = Translation.create("error_failed_read", "$4Failed to obtain information from database");
    public static final Translation OLP_TELEPORT_HOVER = Translation.create("teleport_hover", "$1Teleport to $2{0}, {1};{2}");
    public static final Translation OLP_TELEPORTED_TO = Translation.create("teleported_to", "$2Teleported you to $1{0}, {1};{2}");
    public static final Translation OLP_NO_WORLD_PRESENT = Translation.create("error_no_world_present", "$4No world present for value '{0}'");
    public static final Translation OLP_NOT_ASSOCIATED = Translation.create("error_not_associated", "$4Could not find a OldPlot world associated with value '{0}'");

    public OldPlotsTranslations() {
    }
}
