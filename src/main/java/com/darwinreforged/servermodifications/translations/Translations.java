package com.darwinreforged.servermodifications.translations;

import com.intellectualcrafters.plot.util.StringMan;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Translations {

//    Default formats
    PREFIX("$3[] $1"),
    COLOR_PRIMARY("b"),
    COLOR_SECONDARY("3"),
    COLOR_MINOR("7"),
    COLOR_ERROR("c"),
    PLAYER_ONLY_COMMAND("$4This command can only be executed by players"),
    
//    Time differences
    TIME_DAYS_AGO("$1{0} $2days ago"),
    TIME_YESTERDAY("$1Yesterday"),
    TIME_HOURS_AGO("$1{0} $2hours ago"),
    TIME_HOUR_AGO("$1One $2hour ago"),
    TIME_MINUTES_AGO("$1{0} $2minutes ago"),
    TIME_MINUTE_AGO("$1One $2minute ago"),
    TIME_SECONDS_AGO("$1{0} $2seconds ago"),
    TIME_JUST_NOW("$1Just now"),

//    Tickets
    TICKET_OPEN("$2Open"),
    TICKET_HELD("$2Held"),
    TICKET_CLAIMED("$2Claimed"),
    TICKET_CLOSED("$2Closed"),
    REJECTED_TICKET_SOURCE("Rejected and closed ticket #{0}"),
    REJECTED_TICKET_TARGET("$4Your application was reviewed but was not yet approved, make sure to read the given feedback on your plot, and apply again once ready!"),

//    BrushToolTips
    HOLDING_UNSET_TOOL("$4It seems you are holding a brush tool but it is not set. To suggest it, click this command : $2"),

//    Friends
    ACCEPTING_TP("$2Accept teleports from friends : $1{0}"),
    FRIEND_TELEPORTED("$2{0} $1teleported to your location"),
    NO_TP_NOT_FRIENDS("$4You are not friends with that user so you cannot teleport to them"),
    ALREADY_FRIENDS("You are already friends with $2{0}"),
    FRIEND_ADDED("You are now freinds with $2{0}"),

//    WeatherPlugin
    UNKNOWN_WEATHER_TYPE("$4That weather type is unknown"),
    PLOT_WEATHER_SET("$2Plot weather set to: $1{0}"),

//    HeadsEvolved
    OPEN_GUI_ERROR("$4Failed to open Head Database GUI"),

//    Layerheight
    HEIGHT_TOO_HIGH("$4Height cannot be above 8"),
    HEIGHT_TOO_LOW("$4Height cannot be below 1"),
    HEIGHTTOOL_NAME("$1Layer Height Tool: $2{0}"),
    HEIGHTTOOL_SET("Successfully set the layer height to $2{0}"),
    HEIGHTTOOL_FAILED_BIND("$4Tool cannot be bound to a block"),
    OUTSIDE_PLOT("$4You are not standing inside a plot"),

//    HotbarShare
    SHARED_HOTBAR_WITH("You shared your hotbar with {0}"),
    PLAYER_SHARED_HOTBAR("$2{0} $1shared their hotbar"),
    FULL_HOTBAR("$4Your hotbar is full, please clear one or more slots"),
    HOTBAR_SHARE_HEADER("$2===== $1{0}'s Hotbar $2====="),
    HOTBAR_SHARE_INDEX("$2#{0} : $1{1}"),
    HOTBAR_SHARE_ENCHANTED("$2  Enchanted : "),
    HOTBAR_SHARE_LORE("$2  Lore : ")
    ;

    private String s;

    Translations(String s) {
        this.s = s;
    }

    public String s() {
        return parseColors(this.s);
    }

    public static String format(String m, Object... args) {
        if (args.length == 0) return m;
        Map<String, String> map = new LinkedHashMap<>();
        if (args.length > 0) {
            for (int i = args.length - 1; i >= 0; i--) {
                String arg = "" + args[i];
                if (arg == null || arg.isEmpty()) map.put(String.format("{%d}", i), "");
                if (i == 0) map.put("%s", arg);
            }
        }
        m = StringMan.replaceFromMap(m, map);
        return parseColors(m);
    }

    private static String parseColors(String m) {
        return m
                .replaceAll("\\$1", '§' + COLOR_PRIMARY.s)
                .replaceAll("\\$2", '§' + COLOR_SECONDARY.s)
                .replaceAll("\\$3", '§' + COLOR_MINOR.s)
                .replaceAll("\\$4", '§' + COLOR_ERROR.s);
    }

    public String f(final Object... args) {
        return format(this.s, args);
    }

    public static String shorten(String m, int maxChars) {
        if (m.length() > maxChars) m = String.format("%s...", m.substring(0, maxChars));
        return m;
    }

    public static String shorten(String m) {
        return shorten(m, 20);
    }

    public String sh() {
        return shorten(s());
    }

    public String sh(int maxChars) {
        return shorten(s(), maxChars);
    }
}
