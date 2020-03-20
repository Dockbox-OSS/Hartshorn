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
    
//    Time differences
    TIME_DAYS_AGO("$1{0} $2days ago"),
    TIME_YESTERDAY("$1Yesterday"),
    TIME_HOURS_AGO("$1{0} $2hours ago"),
    TIME_HOUR_AGO("$1One $2hour ago"),
    TIME_MINUTES_AGO("$1{0} $2minutes ago"),
    TIME_MINUTE_AGO("$1One $2minute ago"),
    TIME_SECONDS_AGO("$1{0} $2seconds ago"),
    TIME_JUST_NOW("$1Just now"),

//    Ticket status
    TICKET_OPEN("$2Open"),
    TICKET_HELD("$2Held"),
    TICKET_CLAIMED("$2Claimed"),
    TICKET_CLOSED("$2Closed"),

//    BrushToolTips
    HOLDING_UNSET_TOOL("$1It seems you are holding a brush tool but it is not set. To suggest it, click this command : $2"),

//    Friends
    ACCEPTING_TP("$2Accept teleports from friends : $1{0}"),

//    WeatherPlugin
    UNKNOWN_WEATHER_TYPE("$1That weather type is unknown"),
    PLOT_WEATHER_SET("$2Plot weather set to: $1{0}")
    ;

    private String s;

    Translations(String s) {
        this.s = s;
    }

    public String s() {
        return this.s;
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
                .replaceAll("\\$3", '§' + COLOR_MINOR.s);
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
        return shorten(this.s);
    }

    public String sh(int maxChars) {
        return shorten(this.s, maxChars);
    }
}
