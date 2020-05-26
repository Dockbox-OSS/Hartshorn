package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("default")
public class DefaultTranslations {

    public static final Translation PREFIX = Translation.create("$3[] $1");
    public static final Translation DEFAULT_SINGLE_MESSAGE = Translation.create("$3[] $1{0}");
    public static final Translation COLOR_PRIMARY = Translation.create("b");
    public static final Translation COLOR_SECONDARY = Translation.create("3");
    public static final Translation COLOR_MINOR = Translation.create("7");
    public static final Translation COLOR_ERROR = Translation.create("c");
    public static final Translation PLAYER_ONLY_COMMAND = Translation.create("$4This command can only be executed by players");
    public static final Translation COMMAND_NO_PERMISSION = Translation.create("$4You do not have permission to use this command $3({0})");
    public static final Translation PLOTS1_NAME = Translation.create("Plots1");
    public static final Translation PLOTS2_NAME = Translation.create("Plots2");
    public static final Translation PLOTS500_NAME = Translation.create("Plots500");
    public static final Translation MASTERPLOTS_NAME = Translation.create("MasterPlots");
    public static final Translation MEMBER_RANK_DISPLAY = Translation.create("Member");
    public static final Translation EXPERT_RANK_DISPLAY = Translation.create("Expert");
    public static final Translation MASTER_ARCHITECTURE_DISPLAY = Translation.create("Mastered Skill Architecture");
    public static final Translation MASTER_NATURE_DISPLAY = Translation.create("Mastered Skill Nature");
    public static final Translation MASTER_BOTH_DISPLAY = Translation.create("both Mastered Skills");
    public static final Translation MASTER_RANK_DISPLAY = Translation.create("Master");
    public static final Translation DEFAULT_SEPARATOR = Translation.create(" - ");
    public static final Translation DEFAULT_PADDING = Translation.create(" $1- ");
    public static final Translation DEFAULT_ON = Translation.create("On");
    public static final Translation DEFAULT_OFF = Translation.create("Off");
    public static final Translation UNKNOWN = Translation.create("Unknown");
    public static final Translation NONE = Translation.create("None");
    public static final Translation CONSOLE = Translation.create("Console");
    public static final Translation ONLINE_PLAYER = Translation.create("$1{0}");
    public static final Translation OFFLINE_PLAYER = Translation.create("$2{0}");
    public static final Translation UNOWNED = Translation.create("Unowned");
    public static final Translation EVERYONE = Translation.create("Everyone");
    public static final Translation UNKNOWN_PLAYER = Translation.create("Unknown player");
    public static final Translation ARGUMENT_NOT_PROVIDED = Translation.create("$4No argument provided for value '{0}'");
    public static final Translation PLAYER_NOT_FOUND = Translation.create("$4Could not find player '{0}'");
    public static final Translation DEFAULT_TITLE = Translation.create("$1{0}");
    public static final Translation UNKNOWN_ERROR = Translation.create("$1An error occurred. {0}");
    public static final Translation NOT_PERMITTED_CMD_USE = Translation.create("$4You are not allowed to use this command $3({0})");
    public static final Translation COMMAND_HELP_COMMENT = Translation.create("$2{0} $3- $1{1}");
    public static final Translation COMMAND_HELP_COMMENT_ARGS = Translation.create("$2{0} $1{1} $3- $1{2}");
    public static final Translation OUTSIDE_PLOT = Translation.create("$4You are not standing inside a plot");
    public static final Translation CMD_USAGE_TITLE = Translation.create("$3$2Usage for $1{0}");
    public static final Translation CMD_USAGE = Translation.create("$3- $1/{0}");
    public static final Translation CMD_FLAGS = Translation.create("$3- $2Flags: $1{0}");
    public static final Translation CMD_DESCRIPTION = Translation.create("$3- $2Summary: $1{0}");
    public static final Translation DISABLED_MODULE_ROW = Translation.create("$2 - &7[Disabled] $3{0} $3- $2{1} {2}");
    public static final Translation FAILED_MODULE_ROW = Translation.create("$2 - $4[Failed] {0}");
    public static final Translation ACTIVE_MODULE_ROW = Translation.create("$2 - &a[Loaded] $1{0} $3- $2{1} {2}");
    public static final Translation DARWIN_MODULE_TITLE = Translation.create("$1Darwin Server Info");
    public static final Translation DARWIN_MODULE_PADDING = Translation.create("&m$2=");
    public static final Translation DARWIN_SERVER_VERSION = Translation.create("$2&lDarwin Server &r$3($1Version$3: $1{0}$3)");
    public static final Translation DARWIN_SERVER_UPDATE = Translation.create("$2&lLast updated&r$3: $1{0}");
    public static final Translation DARWIN_SERVER_AUTHOR = Translation.create("$2&lAuthor&r$3: $1{0}");
    public static final Translation DARWIN_SERVER_MODULE_HEAD = Translation.create("$2&lModules&r$3:");
    public static final Translation MODULE_SOURCE = Translation.create("&e[{0}]");
    public static final Translation DARWIN_SINGLE_MODULE_HEADER = Translation.create("$2About $1{0}");
    public static final Translation DARWIN_SINGLE_MODULE_DATA = Translation.create("$2ID : $1{0}\n" +
            "$2Name : $1{1}\n" +
            "$2Description : $1{2}\n" +
            "$2Version : $1{3}\n" +
            "$2URL : $1{4}\n" +
            "$2Dependencies : $1{5}\n" +
            "$2Author(s) : $1{6}\n" +
            "$2Source : $1{7}");
    public static final Translation DARWIN_SINGLE_MODULE_DEPENDENCY = Translation.create("$1{0} $2({1} $3- {2}$2)");
    public static final Translation DARWIN_SERVER_MODULE_HOVER = Translation.create("$1More information for '{0}'");

    DefaultTranslations() {
    }

}
